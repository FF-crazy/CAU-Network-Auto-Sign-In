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
            println("1. Set credentials")
            println("2. Test login")
            println("3. Show current configuration")
            println("4. Exit")
            print("\nEnter command number: ")
            
            when (readLine()) {
                "1" -> setCredentials()
                "2" -> testLogin()
                "3" -> showConfiguration()
                "4" -> {
                    running = false
                    println("Exiting...")
                }
                else -> println("Invalid command. Please try again.")
            }
        }
    }
    
    /**
     * Prompts the user to enter their login credentials and saves them to the configuration.
     */
    private fun setCredentials() {
        println("\n=== Set Credentials ===")
        
        print("Enter username: ")
        val username = readLine() ?: ""
        
        print("Enter password: ")
        val password = readLine() ?: ""
        
        print("Enter login URL (leave empty for default): ")
        val loginUrl = readLine()?.takeIf { it.isNotEmpty() }
        
        val success = configManager.createConfig(username, password, loginUrl)
        
        if (success) {
            println("Credentials saved successfully!")
        } else {
            println("Failed to save credentials. Please try again.")
        }
    }
    
    /**
     * Tests the login functionality with the current configuration.
     */
    private fun testLogin() {
        println("\n=== Test Login ===")
        
        val config = configManager.loadConfig()
        
        if (config.username.isBlank() || config.password.isBlank()) {
            println("Username or password not configured. Please set credentials first.")
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
     * Reads a line from the standard input.
     */
    private fun readLine(): String {
        return scanner.nextLine().trim()
    }
}