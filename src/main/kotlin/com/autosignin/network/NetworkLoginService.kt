package com.autosignin.network

import com.autosignin.config.Config
import okhttp3.*
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Service responsible for handling network login and logout operations.
 * Uses OkHttp for making HTTP requests to the campus network endpoints.
 *
 * @property config The configuration containing login credentials and settings
 */
class NetworkLoginService(private val config: Config) {
    private val logger = LoggerFactory.getLogger(NetworkLoginService::class.java)
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Attempts to log in to the campus network using the configured credentials.
     * Implements retry logic based on configuration settings.
     *
     * @return A LoginResult indicating success or failure
     */
    fun login(): LoginResult {
        var attempts = 0
        var lastResult: LoginResult? = null

        do {
            if (attempts > 0) {
                logger.info("Retrying login (attempt ${attempts + 1}/${config.maxRetries + 1})")
                Thread.sleep(config.retryDelayMs)
            }

            lastResult = attemptLogin()
            attempts++

            if (lastResult.success) {
                return lastResult
            }
        } while (config.autoRetry && attempts <= config.maxRetries)

        return lastResult ?: LoginResult(false, "Login failed after $attempts attempts")
    }

    /**
     * Makes a single login attempt to the campus network.
     *
     * @return A LoginResult indicating success or failure
     */
    private fun attemptLogin(): LoginResult {
        // Create form body with login credentials
        val formBody = FormBody.Builder()
            .add("DDDDD", config.username)
            .add("upass", config.password)
            .build()

        // Construct the full login URL with correct port and path
        val loginUrl = if (config.loginUrl.endsWith("/")) {
            "${config.loginUrl.removeSuffix("/")}:801/eportal/?c=ACSetting&a=Login"
        } else {
            "${config.loginUrl}:801/eportal/?c=ACSetting&a=Login"
        }

        logger.info("Attempting to log in to $loginUrl")

        // Build the request
        val request = Request.Builder()
            .url(loginUrl)
            .post(formBody)
            .header("User-Agent", "CampusAutoLogin/1.0")
            .build()

        try {
            // Execute the request
            client.newCall(request).execute().use { response ->
                val statusCode = response.code
                val responseBody = response.body?.string() ?: ""

                logger.debug("Login response: $statusCode, body length: ${responseBody.length}")

                return if (response.isSuccessful) {
                    // Check for specific success indicators in the response
                    // Based on the login page HTML, successful login might redirect to or include "Dr.COMWebLoginID_3.htm"
                    if (responseBody.contains("Dr.COMWebLoginID_3.htm") || 
                        responseBody.contains("success") || 
                        responseBody.contains("logged in") ||
                        responseBody.contains("登录成功")) {
                        LoginResult(true, "Login successful", statusCode)
                    } else if (responseBody.contains("Dr.COMWebLoginID_2.htm")) {
                        // Login failure indicator from the HTML
                        LoginResult(false, "Login failed: incorrect credentials", statusCode)
                    } else {
                        // Response was 200 OK but might not indicate successful login
                        logger.debug("Response body: $responseBody")
                        LoginResult(false, "Login may have failed: unexpected response content", statusCode)
                    }
                } else {
                    // HTTP error response
                    logger.debug("Response body: $responseBody")
                    LoginResult(false, "Login failed with status code: $statusCode", statusCode)
                }
            }
        } catch (e: IOException) {
            logger.error("Network error during login attempt", e)
            return LoginResult(false, "Network error: ${e.message}")
        } catch (e: Exception) {
            logger.error("Unexpected error during login attempt", e)
            return LoginResult(false, "Error: ${e.message}")
        }
    }

    /**
     * Logs out a device with the specified MAC address from the campus network.
     * This is particularly useful for administrative purposes or when a device
     * needs to be forcibly disconnected.
     *
     * @param macAddress The MAC address of the device to log out, defaults to "111111111111"
     * @return A LoginResult indicating success or failure of the logout operation
     */
    fun logout(macAddress: String = "111111111111"): LoginResult {
        logger.info("Attempting to log out device with MAC address: $macAddress")

        // Construct the logout URL
        val logoutUrl = if (config.loginUrl.endsWith("/")) {
            "${config.loginUrl.removeSuffix("/")}:801/eportal/?c=ACSetting&a=Logout&ver=1.0"
        } else {
            "${config.loginUrl}:801/eportal/?c=ACSetting&a=Logout&ver=1.0"
        }

        // Build the request with the MAC address as a parameter
        val request = Request.Builder()
            .url("$logoutUrl&wlanusermac=$macAddress")
            .header("User-Agent", "CampusAutoLogin/1.0")
            .build()

        try {
            // Execute the request
            client.newCall(request).execute().use { response ->
                val statusCode = response.code
                val responseBody = response.body?.string() ?: ""

                logger.debug("Logout response: $statusCode, body length: ${responseBody.length}")

                return if (response.isSuccessful) {
                    // Check for specific success indicators in the response
                    if (responseBody.contains("success") || 
                        responseBody.contains("logged out") ||
                        responseBody.contains("注销成功")) {
                        LoginResult(true, "Device with MAC $macAddress logged out successfully", statusCode)
                    } else {
                        // Response was 200 OK but might not indicate successful logout
                        logger.debug("Response body: $responseBody")
                        LoginResult(false, "Logout may have failed: unexpected response content", statusCode)
                    }
                } else {
                    // HTTP error response
                    logger.debug("Response body: $responseBody")
                    LoginResult(false, "Logout failed with status code: $statusCode", statusCode)
                }
            }
        } catch (e: IOException) {
            logger.error("Network error during logout attempt", e)
            return LoginResult(false, "Network error: ${e.message}")
        } catch (e: Exception) {
            logger.error("Unexpected error during logout attempt", e)
            return LoginResult(false, "Error: ${e.message}")
        }
    }
}
