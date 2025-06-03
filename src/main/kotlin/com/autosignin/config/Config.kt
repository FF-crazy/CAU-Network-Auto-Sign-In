package com.autosignin.config

/**
 * Data class representing the configuration for campus network login.
 *
 * @property username The username for network authentication
 * @property password The password for network authentication
 * @property loginUrl The URL endpoint for the login request
 * @property autoRetry Whether to automatically retry login on failure
 * @property maxRetries Maximum number of retry attempts
 * @property retryDelayMs Delay between retry attempts in milliseconds
 */
data class Config(
    val username: String = "Your-school-username-here",
    val password: String = "Your-school-password-here",
    val loginUrl: String = "http://10.3.38.8/",
    val autoRetry: Boolean = true,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 2000
)