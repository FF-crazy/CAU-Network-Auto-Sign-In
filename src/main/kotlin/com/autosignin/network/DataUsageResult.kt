package com.autosignin.network

/**
 * Represents the result of a data usage query.
 *
 * @property success Whether the query was successful
 * @property message A message describing the result of the query
 * @property usedBytes The amount of data used in bytes (if available)
 * @property usedMegabytes The amount of data used in megabytes (if available)
 * @property totalBytes The total amount of data available in bytes (if available)
 * @property totalMegabytes The total amount of data available in megabytes (if available)
 * @property remainingBytes The amount of data remaining in bytes (if available)
 * @property remainingMegabytes The amount of data remaining in megabytes (if available)
 * @property rawUseflow The raw "useflow" value from the JSON response (if available)
 * @property rawUserflow The raw "userflow" value from the JSON response (if available)
 * @property statusCode The HTTP status code of the response (if applicable)
 */
data class DataUsageResult(
    val success: Boolean,
    val message: String,
    val usedBytes: Long? = null,
    val usedMegabytes: Double? = null,
    val totalBytes: Long? = null,
    val totalMegabytes: Double? = null,
    val remainingBytes: Long? = null,
    val remainingMegabytes: Double? = null,
    val rawUseflow: Long? = null,
    val rawUserflow: Long? = null,
    val statusCode: Int? = null
)
