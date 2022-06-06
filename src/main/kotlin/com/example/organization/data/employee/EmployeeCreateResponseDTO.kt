package com.example.organization.data.employee

data class EmployeeCreateResponseDTO(
    val id: Long,
    val name: String,
    val position: String?,
    val phoneNumber: String?,
    val inUse: Boolean,
    val responsibilities: String?,
    val organizationId: Long?
)
