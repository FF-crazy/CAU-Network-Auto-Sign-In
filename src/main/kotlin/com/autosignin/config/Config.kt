package com.autosignin.config

/**
 * Data class representing an account for network authentication.
 *
 * @property username The username for network authentication
 * @property password The password for network authentication
 * @property name Optional friendly name for the account
 */
data class Account(
    val username: String,
    val password: String,
    val name: String = ""
)

/**
 * Data class representing the configuration for campus network login.
 *
 * @property accounts List of available accounts for network authentication
 * @property selectedAccountIndex Index of the currently selected account
 * @property loginUrl The URL endpoint for the login request
 * @property autoRetry Whether to automatically retry login on failure
 * @property maxRetries Maximum number of retry attempts
 * @property retryDelayMs Delay between retry attempts in milliseconds
 */
data class Config(
    val accounts: List<Account> = listOf(
        Account("", ""),
        Account("student1", "password1", "Student Account"),
        Account("teacher1", "password1", "Teacher Account")
    ),
    val selectedAccountIndex: Int = 0,
    val loginUrl: String = "http://10.3.38.8/",
    val autoRetry: Boolean = true,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 2000
) {
    // Convenience properties to access the selected account
    val username: String
        get() = if (accounts.isNotEmpty() && selectedAccountIndex < accounts.size) 
                    accounts[selectedAccountIndex].username 
                else ""

    val password: String
        get() = if (accounts.isNotEmpty() && selectedAccountIndex < accounts.size) 
                    accounts[selectedAccountIndex].password 
                else ""
}
