package com.autosignin.cli

import com.autosignin.config.ConfigManager
import com.autosignin.network.NetworkLoginService
import org.slf4j.LoggerFactory
import java.util.Scanner

/**
 * Command-line interface for the Campus Network Auto Sign-In application.
 * Provides interactive commands for setting up credentials and testing login.
 */
class CommandLineInterface {
    private val logger = LoggerFactory.getLogger(CommandLineInterface::class.java)
    private val configManager = ConfigManager()
    private val scanner = Scanner(System.`in`)

    /**
     * Starts the command-line interface.
     */
    fun start() {
        println("=== Campus Network Auto Sign-In ===")

        var running = true
        while (running) {
            println("\nAvailable commands:")
            println("1. Test login")
            println("2. Show current configuration")
            println("3. Logout device with MAC 111111111111")
            println("4. Exit")
            print("\nEnter command number: ")

            when (readLine()) {
                "1" -> testLogin()
                "2" -> showConfiguration()
                "3" -> logoutDevice()
                "4" -> {
                    running = false
                    println("Exiting...")
                }
                else -> println("Invalid command. Please try again.")
            }
        }
    }


    /**
     * Tests the login functionality with the current configuration.
     */
    private fun testLogin() {
        println("\n=== Test Login ===")

        val config = configManager.loadConfig()

        if (config.username.isBlank() || config.password.isBlank()) {
            println("Username or password not configured in Config.kt.")
            return
        }

        println("Attempting to log in with username: ${config.username}")

        val loginService = NetworkLoginService(config)
        val result = loginService.login()

        if (result.success) {
            println("Login successful: ${result.message}")
        } else {
            println("Login failed: ${result.message}")
        }
    }

    /**
     * Displays the current configuration.
     */
    private fun showConfiguration() {
        println("\n=== Current Configuration ===")

        val config = configManager.loadConfig()

        println("Username: ${config.username}")
        println("Password: ${if (config.password.isNotEmpty()) "********" else "(not set)"}")
        println("Login URL: ${config.loginUrl}")
        println("Auto Retry: ${config.autoRetry}")
        println("Max Retries: ${config.maxRetries}")
        println("Retry Delay: ${config.retryDelayMs}ms")
    }

    /**
     * Logs out a device with MAC address 111111111111 from the campus network.
     */
    private fun logoutDevice() {
        println("\n=== Logout Device ===")

        val config = configManager.loadConfig()

        if (config.loginUrl.isBlank()) {
            println("Login URL not configured in Config.kt.")
            return
        }

        println("Attempting to log out device with MAC address: 111111111111")

        val loginService = NetworkLoginService(config)
        val result = loginService.logout()

        if (result.success) {
            println("Logout successful: ${result.message}")
        } else {
            println("Logout failed: ${result.message}")
        }
    }

    /**
     * Reads a line from the standard input.
     */
    private fun readLine(): String {
        return scanner.nextLine().trim()
    }
}
