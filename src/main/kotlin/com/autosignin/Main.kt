package com.autosignin

import com.autosignin.cli.CommandLineInterface
import com.autosignin.config.ConfigManager
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * Main entry point for the Campus Network Auto Sign-In application.
 * This application automatically logs into a campus network using stored credentials.
 * 
 * The application can be used in several modes:
 * 1. Interactive mode: Run without arguments to start the command-line interface
 * 2. Auto-login mode: Run with the --auto-login argument to attempt login immediately
 * 3. Auto-login with specific account: Run with --auto-login --account=<index> to login with a specific account
 * 4. Auto-logout mode: Run with --auto-logout to logout a device with MAC 111111111111
 * 5. List accounts: Run with --list-accounts to display all available accounts
 */
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")
    logger.info("Starting Campus Network Auto Sign-In")

    try {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "--auto-login" -> {
                    // Check if an account index is specified
                    val accountIndex = args.getAccountIndex()

                    // Auto-login mode
                    if (accountIndex != null) {
                        logger.info("Auto-login with account index: $accountIndex")
                        AutoLoginRunner().run(accountIndex)
                    } else {
                        logger.info("Auto-login with default account")
                        AutoLoginRunner().run()
                    }
                }
                "--auto-logout" -> {
                    // Auto-logout mode for device with MAC 111111111111
                    AutoLoginRunner().runLogout()
                }
                "--list-accounts" -> {
                    // List all available accounts
                    val configManager = ConfigManager()
                    val accounts = configManager.getAvailableAccounts()

                    println("Available accounts:")
                    accounts.forEach { (index, username, name) ->
                        println("${index}. $name (username: $username)")
                    }
                }
                else -> {
                    println("Unknown command: ${args[0]}")
                    println("Available commands: --auto-login [--account=<index>], --auto-logout, --list-accounts")
                    exitProcess(1)
                }
            }
        } else {
            // Interactive mode
            val cli = CommandLineInterface()
            cli.start()
        }
    } catch (e: Exception) {
        logger.error("An error occurred", e)
        println("An error occurred: ${e.message}")
        exitProcess(1)
    }
}

/**
 * Helper function to extract the account index from command-line arguments.
 * Looks for an argument in the format --account=<index>.
 *
 * @return The account index as an Int, or null if not specified or invalid
 */
private fun Array<String>.getAccountIndex(): Int? {
    val accountArg = this.firstOrNull { it.startsWith("--account=") }
    return accountArg?.substringAfter("=")?.toIntOrNull()
}
