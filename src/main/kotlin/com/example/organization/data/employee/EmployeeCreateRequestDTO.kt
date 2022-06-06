package com.example.organization.data.employee

import com.example.organization.enums.Position

data class EmployeeCreateRequestDTO(
    val name: String,
    val position: Position? = null,
    val nickname: String? = null,
    val phoneNumber: String? = null,
    val inUse: Boolean = true,
    val responsibilities: String? = null,
    val organizationId: Long? = null
)
