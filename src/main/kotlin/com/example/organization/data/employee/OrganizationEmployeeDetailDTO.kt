package com.example.organization.data.employee

import com.example.organization.domain.WorkPeriod

data class OrganizationEmployeeDetailDTO(
    val organizationEmployeeId: Long,
    val employeeId: Long,
    val organizationId: Long,
    val name: String? = null,
    val phoneNumber: String? = null,
    val inUse: Boolean,
    val responsibilities: String? = null,
    val position: String,
    val workPeriod: WorkPeriod
)
