package com.example.group.data.employee

import com.example.group.enums.Position

data class EmployeeCreateResponseDTO(
    val id: Long,
    val name: String,
    val position: String?,
    val phoneNumber: String?,
    val inUse: Boolean,
    val responsibilities: String?,
    val groupId: Long?
)
