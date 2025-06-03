package com.autosignin

import com.autosignin.cli.CommandLineInterface
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

/**
 * Main entry point for the Campus Network Auto Sign-In application.
 * This application automatically logs into a campus network using stored credentials.
 * 
 * The application can be used in two modes:
 * 1. Interactive mode: Run without arguments to start the command-line interface
 * 2. Auto-login mode: Run with the --auto-login argument to attempt login immediately
 */
fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("Main")
    logger.info("Starting Campus Network Auto Sign-In")

    try {
        if (args.isNotEmpty()) {
            when (args[0]) {
                "--auto-login" -> {
                    // Auto-login mode
                    AutoLoginRunner().run()
                }
                "--auto-logout" -> {
                    // Auto-logout mode for device with MAC 111111111111
                    AutoLoginRunner().runLogout()
                }
                else -> {
                    println("Unknown command: ${args[0]}")
                    println("Available commands: --auto-login, --auto-logout")
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
