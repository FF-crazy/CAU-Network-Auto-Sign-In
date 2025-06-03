package com.autosignin.config

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Properties

/**
 * Manages the configuration for the campus network auto sign-in application.
 * Handles loading and saving configuration from/to a properties file.
 */
class ConfigManager(
    private val configFilePath: String = System.getProperty("user.home") + File.separator + ".campus-autologin.properties"
) {
    private val logger = LoggerFactory.getLogger(ConfigManager::class.java)
    
    /**
     * Loads the configuration from the properties file.
     * If the file doesn't exist, returns a default configuration.
     *
     * @return The loaded configuration
     */
    fun loadConfig(): Config {
        val properties = Properties()
        val configFile = File(configFilePath)
        
        if (configFile.exists()) {
            try {
                FileInputStream(configFile).use { fis ->
                    properties.load(fis)
                    logger.info("Configuration loaded from: $configFilePath")
                }
            } catch (e: Exception) {
                logger.error("Failed to load configuration", e)
                return Config()
            }
            
            return Config(
                username = properties.getProperty("username", ""),
                password = properties.getProperty("password", ""),
                loginUrl = properties.getProperty("loginUrl", "https://campus-network-login.example.com/login"),
                autoRetry = properties.getProperty("autoRetry", "true").toBoolean(),
                maxRetries = properties.getProperty("maxRetries", "3").toInt(),
                retryDelayMs = properties.getProperty("retryDelayMs", "2000").toLong()
            )
        } else {
            logger.info("Configuration file not found, using default configuration")
            return Config()
        }
    }
    
    /**
     * Saves the configuration to the properties file.
     *
     * @param config The configuration to save
     * @return true if the save was successful, false otherwise
     */
    fun saveConfig(config: Config): Boolean {
        val properties = Properties()
        properties.setProperty("username", config.username)
        properties.setProperty("password", config.password)
        properties.setProperty("loginUrl", config.loginUrl)
        properties.setProperty("autoRetry", config.autoRetry.toString())
        properties.setProperty("maxRetries", config.maxRetries.toString())
        properties.setProperty("retryDelayMs", config.retryDelayMs.toString())
        
        try {
            val configFile = File(configFilePath)
            FileOutputStream(configFile).use { fos ->
                properties.store(fos, "Campus Network Auto Sign-In Configuration")
                logger.info("Configuration saved to: $configFilePath")
            }
            return true
        } catch (e: Exception) {
            logger.error("Failed to save configuration", e)
            return false
        }
    }
    
    /**
     * Creates a new configuration with the provided credentials.
     *
     * @param username The username for network authentication
     * @param password The password for network authentication
     * @param loginUrl Optional login URL
     * @return true if the configuration was saved successfully, false otherwise
     */
    fun createConfig(username: String, password: String, loginUrl: String? = null): Boolean {
        val config = Config(
            username = username,
            password = password,
            loginUrl = loginUrl ?: "https://campus-network-login.example.com/login"
        )
        return saveConfig(config)
    }
}