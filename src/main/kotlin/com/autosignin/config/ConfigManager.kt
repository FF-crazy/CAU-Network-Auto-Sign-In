package com.autosignin.config

import org.slf4j.LoggerFactory

/**
 * Manages the configuration for the campus network auto sign-in application.
 * Provides access to the default configuration from Config.kt and allows
 * selecting which account to use for login.
 */
class ConfigManager {
    private val logger = LoggerFactory.getLogger(ConfigManager::class.java)
    private var selectedAccountIndex = 0

    /**
     * Returns the configuration from Config.kt with the currently selected account.
     *
     * @return The configuration with the selected account
     */
    fun loadConfig(): Config {
        logger.info("Using configuration from Config.kt with account index: $selectedAccountIndex")
        return Config(selectedAccountIndex = selectedAccountIndex)
    }

    /**
     * Returns a list of all available accounts.
     *
     * @return List of accounts with their index, username, and name
     */
    fun getAvailableAccounts(): List<Triple<Int, String, String>> {
        val config = Config()
        return config.accounts.mapIndexed { index, account ->
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
        val config = Config()
        if (index < 0 || index >= config.accounts.size) {
            logger.error("Invalid account index: $index")
            return false
        }

        selectedAccountIndex = index
        logger.info("Selected account at index $index: ${config.accounts[index].username}")
        return true
    }
}
