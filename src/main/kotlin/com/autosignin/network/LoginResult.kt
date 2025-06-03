package com.autosignin.network

/**
 * Represents the result of a login attempt to the campus network.
 *
 * @property success Whether the login attempt was successful
 * @property message A message describing the result of the login attempt
 * @property statusCode The HTTP status code of the response (if applicable)
 */
data class LoginResult(
    val success: Boolean,
    val message: String,
    val statusCode: Int? = null
)