package com.autosignin

import com.autosignin.config.ConfigManager
import com.autosignin.network.NetworkLoginService
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * Handles automatic login and logout operations for the campus network without user interaction.
 * Used when the application is run with the --auto-login or --auto-logout arguments.
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
}
