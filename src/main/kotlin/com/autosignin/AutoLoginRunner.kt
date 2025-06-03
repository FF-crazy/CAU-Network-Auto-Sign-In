package com.autosignin

import com.autosignin.config.ConfigManager
import com.autosignin.network.NetworkLoginService
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * Handles automatic login to the campus network without user interaction.
 * Used when the application is run with the --auto-login argument.
 */
class AutoLoginRunner {
    private val logger = LoggerFactory.getLogger(AutoLoginRunner::class.java)
    
    /**
     * Runs the automatic login process.
     */
    fun run() {
        logger.info("Running in auto-login mode")
        
        // Load configuration
        val configManager = ConfigManager()
        val config = configManager.loadConfig()
        
        if (config.username.isBlank() || config.password.isBlank()) {
            logger.error("Username or password not configured")
            println("Please configure your username and password first.")
            exitProcess(1)
        }
        
        // Initialize network service
        val loginService = NetworkLoginService(config)
        
        // Attempt login
        val result = loginService.login()
        
        if (result.success) {
            logger.info("Login successful: ${result.message}")
            println("Successfully logged in to campus network!")
        } else {
            logger.error("Login failed: ${result.message}")
            println("Failed to log in: ${result.message}")
            exitProcess(1)
        }
    }
}