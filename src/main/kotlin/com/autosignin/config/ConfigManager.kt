package com.autosignin.config

import org.slf4j.LoggerFactory

/**
 * Manages the configuration for the campus network auto sign-in application.
 * Provides access to the default configuration from Config.kt.
 */
class ConfigManager {
    private val logger = LoggerFactory.getLogger(ConfigManager::class.java)

    /**
     * Returns the default configuration from Config.kt.
     *
     * @return The default configuration
     */
    fun loadConfig(): Config {
        logger.info("Using default configuration from Config.kt")
        return Config()
    }

}
