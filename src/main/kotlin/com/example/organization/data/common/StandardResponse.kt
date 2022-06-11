package com.example.organization.data.common

data class StandardResponse<T>(val success: Boolean = true, val failMessage: String? = null, val responseBody: T?)
