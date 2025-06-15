package com.autosignin.config

import org.slf4j.LoggerFactory
import java.io.File

/**
 * Manages the configuration for the campus network auto sign-in application.
 * Provides access to the configuration defined in account.ini and allows
 * selecting which account to use for login.
 */
class ConfigManager {
    private val logger = LoggerFactory.getLogger(ConfigManager::class.java)
    private var selectedAccountIndex = 0

    /**
     * Loads accounts from account.ini file located in the working directory.
     * The ini file should contain sections in the form:
     * [account1]
     * username=foo
     * password=bar
     * name=Optional Name
     */
    private fun loadAccountsFromIni(): List<Account> {
        val file = File("account.ini")
        if (!file.exists()) {
            logger.warn("account.ini not found, using default accounts from Config.kt")
            return Config().accounts
        }

        val accounts = mutableListOf<Account>()
        var currentUsername = ""
        var currentPassword = ""
        var currentName = ""
        file.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) return@forEachLine

            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                if (currentUsername.isNotEmpty() || currentPassword.isNotEmpty() || currentName.isNotEmpty()) {
                    accounts.add(Account(currentUsername, currentPassword, currentName))
                }
                currentUsername = ""
                currentPassword = ""
                currentName = trimmed.substring(1, trimmed.length - 1)
            } else {
                val parts = trimmed.split('=', limit = 2)
                if (parts.size == 2) {
                    when (parts[0].trim()) {
                        "username" -> currentUsername = parts[1].trim()
                        "password" -> currentPassword = parts[1].trim()
                        "name" -> currentName = parts[1].trim()
                    }
                }
            }
        }
        if (currentUsername.isNotEmpty() || currentPassword.isNotEmpty() || currentName.isNotEmpty()) {
            accounts.add(Account(currentUsername, currentPassword, currentName))
        }

        return accounts
    }

    /**
     * Returns the configuration loaded from account.ini with the currently selected account.
     *
     * @return The configuration with the selected account
     */
    fun loadConfig(): Config {
        val accounts = loadAccountsFromIni()
        if (selectedAccountIndex < 0 || selectedAccountIndex >= accounts.size) {
            selectedAccountIndex = 0
        }
        logger.info("Using configuration from account.ini with account index: $selectedAccountIndex")
        return Config(accounts = accounts, selectedAccountIndex = selectedAccountIndex)
    }

    /**
     * Returns a list of all available accounts.
     *
     * @return List of accounts with their index, username, and name
     */
    fun getAvailableAccounts(): List<Triple<Int, String, String>> {
        val accounts = loadAccountsFromIni()
        return accounts.mapIndexed { index, account ->
            Triple(
                index,
                account.username,
                if (account.name.isNotEmpty()) account.name else "Account ${index + 1}"
            )
        }
    }

    /**
     * Selects an account to use for login by its index.
     *
     * @param index The index of the account to select
     * @return true if the account was selected successfully, false otherwise
     */
    fun selectAccount(index: Int): Boolean {
        val accounts = loadAccountsFromIni()
        if (index < 0 || index >= accounts.size) {
            logger.error("Invalid account index: $index")
            return false
        }

        selectedAccountIndex = index
        logger.info("Selected account at index $index: ${accounts[index].username}")
        return true
    }
}
