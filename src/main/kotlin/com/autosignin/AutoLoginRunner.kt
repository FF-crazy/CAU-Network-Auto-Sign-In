package com.autosignin

import com.autosignin.config.ConfigManager
import com.autosignin.network.NetworkLoginService
import org.slf4j.LoggerFactory
import java.text.DecimalFormat
import kotlin.system.exitProcess

/**
 * Handles automatic login, logout, and data usage query operations for the campus network without user interaction.
 * Used when the application is run with the --auto-login, --auto-logout, or --query-usage arguments.
 */
class AutoLoginRunner {
    private val logger = LoggerFactory.getLogger(AutoLoginRunner::class.java)

    /**
     * Runs the automatic login process with the specified account.
     *
     * @param accountIndex The index of the account to use for login (optional)
     */
    fun run(accountIndex: Int? = null) {
        logger.info("Running in auto-login mode")

        // Load configuration
        val configManager = ConfigManager()

        // Select account if specified
        if (accountIndex != null) {
            if (!configManager.selectAccount(accountIndex)) {
                logger.error("Invalid account index: $accountIndex")
                println("Invalid account index: $accountIndex")
                println("Available accounts:")
                configManager.getAvailableAccounts().forEach { (index, username, name) ->
                    println("${index}. $name (username: $username)")
                }
                exitProcess(1)
            }
            logger.info("Using account at index $accountIndex for login")
        }

        val config = configManager.loadConfig()

        if (config.username.isBlank() || config.password.isBlank()) {
            logger.error("Username or password not configured in Config.kt")
            println("Please configure your username and password in Config.kt.")
            exitProcess(1)
        }

        // Initialize network service
        val loginService = NetworkLoginService(config)

        // Attempt login
        val result = loginService.login()

        if (result.success) {
            logger.info("Login successful: ${result.message}")
            println("Successfully logged in to campus network with account: ${config.username}!")
        } else {
            logger.error("Login failed: ${result.message}")
            println("Failed to log in: ${result.message}")
            exitProcess(1)
        }
    }

    /**
     * Runs the automatic logout process for a device with MAC address 111111111111.
     */
    fun runLogout() {
        logger.info("Running in auto-logout mode")

        // Load configuration
        val configManager = ConfigManager()
        val config = configManager.loadConfig()

        if (config.loginUrl.isBlank()) {
            logger.error("Login URL not configured in Config.kt")
            println("Please configure your login URL in Config.kt.")
            exitProcess(1)
        }

        // Initialize network service
        val loginService = NetworkLoginService(config)

        // Attempt logout
        val result = loginService.logout()

        if (result.success) {
            logger.info("Logout successful: ${result.message}")
            println("Successfully logged out device with MAC 111111111111 from campus network!")
        } else {
            logger.error("Logout failed: ${result.message}")
            println("Failed to log out device: ${result.message}")
            exitProcess(1)
        }
    }

    /**
     * Runs the automatic data usage query process for the specified account.
     *
     * @param accountIndex The index of the account to use for the query (optional)
     */
    fun runQueryDataUsage(accountIndex: Int? = null) {
        logger.info("Running in data usage query mode")

        // Load configuration
        val configManager = ConfigManager()

        // Select account if specified
        if (accountIndex != null) {
            if (!configManager.selectAccount(accountIndex)) {
                logger.error("Invalid account index: $accountIndex")
                println("Invalid account index: $accountIndex")
                println("Available accounts:")
                configManager.getAvailableAccounts().forEach { (index, username, name) ->
                    println("${index}. $name (username: $username)")
                }
                exitProcess(1)
            }
            logger.info("Using account at index $accountIndex for data usage query")
        }

        val config = configManager.loadConfig()

        if (config.username.isBlank() || config.password.isBlank()) {
            logger.error("Username or password not configured in Config.kt")
            println("Please configure your username and password in Config.kt.")
            exitProcess(1)
        }

        // Initialize network service
        val loginService = NetworkLoginService(config)

        // First login before querying data usage
        logger.info("Logging in before querying data usage")
        val loginResult = loginService.login()

        if (!loginResult.success) {
            logger.error("Login failed: ${loginResult.message}")
            println("Failed to log in: ${loginResult.message}")
            exitProcess(1)
        }

        logger.info("Login successful, now querying data usage")

        // Attempt to query data usage
        val result = loginService.queryDataUsage()

        if (result.success) {
            logger.info("Data usage query successful")
            println("Data usage for account: ${config.username}")

            val formatter = DecimalFormat("#,##0.00")

            if (result.rawUseflow != null) {
                println("Used data (raw): ${result.rawUseflow} MB")
            }

            if (result.usedMegabytes != null) {
                println("Used data: ${formatter.format(result.usedMegabytes)} MB")
            } else if (result.usedBytes != null) {
                println("Used data: ${result.usedBytes} bytes")
            } else {
                println("Used data: Not available")
            }

            if (result.rawUserflow != null) {
                println("Total data (raw): ${result.rawUserflow} MB")
            }

            if (result.totalMegabytes != null) {
                println("Total data: ${formatter.format(result.totalMegabytes)} MB")
            } else if (result.totalBytes != null) {
                println("Total data: ${result.totalBytes} bytes")
            } else {
                println("Total data: Not available")
            }

            if (result.remainingMegabytes != null) {
                println("Remaining data: ${formatter.format(result.remainingMegabytes)} MB")
            } else if (result.remainingBytes != null) {
                println("Remaining data: ${result.remainingBytes} bytes")
            } else {
                println("Remaining data: Not available")
            }
        } else {
            logger.error("Data usage query failed: ${result.message}")
            println("Failed to query data usage: ${result.message}")
            exitProcess(1)
        }
    }
    /**
     * Runs the automatic account selection process.
     * Finds an account with data usage less than 30GB and logs in with it.
     * If no suitable account is found, logs out and displays a message.
     */
    fun runAutoSelect() {
        logger.info("Running in auto-select mode")

        // Load configuration and get all available accounts
        val configManager = ConfigManager()
        val accounts = configManager.getAvailableAccounts()

        if (accounts.isEmpty()) {
            logger.error("No accounts available")
            println("No accounts available. Please configure at least one account in Config.kt.")
            exitProcess(1)
        }

        logger.info("Found ${accounts.size} accounts, checking data usage for each")

        // Try each account until we find one with data usage less than 30GB
        for ((index, username, name) in accounts) {
            logger.info("Checking account $index: $name ($username)")

            // Select this account
            configManager.selectAccount(index)
            val config = configManager.loadConfig()

            // Initialize network service
            val loginService = NetworkLoginService(config)

            // First login before querying data usage
            logger.info("Logging in with account $index before querying data usage")
            val loginResult = loginService.login()

            if (!loginResult.success) {
                logger.warn("Login failed for account $index: ${loginResult.message}, trying next account")
                continue
            }

            logger.info("Login successful for account $index, now querying data usage")

            // Query data usage
            val result = loginService.queryDataUsage()

            if (!result.success) {
                logger.warn("Data usage query failed for account $index: ${result.message}, trying next account")
                continue
            }

            // Check if data usage is less than 30GB (30,000 MB)
            val usedMB = result.rawUseflow ?: result.usedMegabytes?.toLong() ?: Long.MAX_VALUE

            logger.info("Account $index has used $usedMB MB of data")

            if (usedMB < 30000) {
                logger.info("Found suitable account $index with data usage less than 30GB")
                println("Successfully logged in with account: $name ($username)")
                println("Data usage: $usedMB MB (less than 30GB)")
                return
            } else {
                logger.info("Account $index has data usage >= 30GB, trying next account")
                // Logout before trying the next account
                loginService.logout()
            }
        }

        // If we get here, no suitable account was found
        logger.warn("No account with data usage less than 30GB found")
        println("没有符合条件的用户")

        // Ensure we're logged out
        try {
            val config = configManager.loadConfig()
            val loginService = NetworkLoginService(config)
            loginService.logout()
            logger.info("Logged out successfully")
        } catch (e: Exception) {
            logger.error("Error during logout", e)
        }
    }
}
