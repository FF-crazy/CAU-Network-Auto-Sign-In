package com.autosignin.cli

import com.autosignin.config.ConfigManager
import com.autosignin.network.DataUsageResult
import com.autosignin.network.NetworkLoginService
import org.slf4j.LoggerFactory
import java.util.Scanner

/**
 * Command-line interface for the Campus Network Auto Sign-In application.
 * Provides interactive commands for selecting accounts, testing login, and more.
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
            println("1. Select account")
            println("2. Test login")
            println("3. Show current configuration")
            println("4. Logout device with MAC 111111111111")
            println("5. Query data usage")
            println("6. Exit")
            print("\nEnter command number: ")

            when (readLine()) {
                "1" -> selectAccount()
                "2" -> testLogin()
                "3" -> showConfiguration()
                "4" -> logoutDevice()
                "5" -> queryDataUsage()
                "6" -> {
                    running = false
                    println("Exiting...")
                }
                else -> println("Invalid command. Please try again.")
            }
        }
    }

    /**
     * Displays available accounts and prompts the user to select one.
     */
    private fun selectAccount() {
        println("\n=== Select Account ===")

        val accounts = configManager.getAvailableAccounts()
        if (accounts.isEmpty()) {
            println("No accounts configured in Config.kt.")
            return
        }

        println("Available accounts:")
        accounts.forEach { (index, username, name) ->
            println("${index + 1}. $name (username: $username)")
        }

        print("\nEnter account number (1-${accounts.size}): ")
        val selection = readLine()?.toIntOrNull()

        if (selection == null || selection < 1 || selection > accounts.size) {
            println("Invalid selection. Please try again.")
            return
        }

        val accountIndex = selection - 1
        if (configManager.selectAccount(accountIndex)) {
            println("Selected account: ${accounts[accountIndex].third}")
        } else {
            println("Failed to select account. Please try again.")
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
     * Displays the current configuration including all available accounts.
     */
    private fun showConfiguration() {
        println("\n=== Current Configuration ===")

        val config = configManager.loadConfig()
        val accounts = configManager.getAvailableAccounts()

        println("Available Accounts:")
        accounts.forEach { (index, username, name) ->
            val isSelected = index == config.selectedAccountIndex
            val marker = if (isSelected) "* " else "  "
            println("$marker${index + 1}. $name (username: $username)")
        }

        println("\nSelected Account: ${config.selectedAccountIndex + 1}")
        println("Username: ${config.username}")
        println("Password: ${if (config.password.isNotEmpty()) "********" else "(not set)"}")
        println("\nOther Settings:")
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
     * Queries and displays the current account's data usage.
     */
    private fun queryDataUsage() {
        println("\n=== Query Data Usage ===")

        val config = configManager.loadConfig()

        if (config.username.isBlank() || config.password.isBlank()) {
            println("Username or password not configured in Config.kt.")
            return
        }

        println("Querying data usage for account: ${config.username}")

        val loginService = NetworkLoginService(config)
        val result = loginService.queryDataUsage()

        if (result.success) {
            println("Data usage query successful:")

            if (result.usedMegabytes != null) {
                println("Used data: ${String.format("%.2f", result.usedMegabytes)} MB")
            } else if (result.usedBytes != null) {
                println("Used data: ${result.usedBytes} bytes")
            } else {
                println("Used data: Not available")
            }

            if (result.totalMegabytes != null) {
                println("Total data: ${String.format("%.2f", result.totalMegabytes)} MB")
            } else if (result.totalBytes != null) {
                println("Total data: ${result.totalBytes} bytes")
            } else {
                println("Total data: Not available")
            }

            if (result.remainingMegabytes != null) {
                println("Remaining data: ${String.format("%.2f", result.remainingMegabytes)} MB")
            } else if (result.remainingBytes != null) {
                println("Remaining data: ${result.remainingBytes} bytes")
            } else {
                println("Remaining data: Not available")
            }
        } else {
            println("Data usage query failed: ${result.message}")
        }
    }

    /**
     * Reads a line from the standard input.
     */
    private fun readLine(): String {
        return scanner.nextLine().trim()
    }
}
