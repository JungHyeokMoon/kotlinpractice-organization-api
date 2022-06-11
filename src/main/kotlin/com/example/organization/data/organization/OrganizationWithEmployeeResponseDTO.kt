package com.example.organization.data.organization

import com.example.organization.data.employee.OrganizationEmployeeDetailDTO

data class OrganizationWithEmployeeResponseDTO(
    val organizationId : Long,
    val organizationName: String,
    val organizationEmployees: MutableSet<OrganizationEmployeeDetailDTO>
)
