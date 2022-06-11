package com.example.organization.data.organization

data class OrganizationPreviewDTO(
    val organizationName: String,
    val parentOrganizationId: Long? = null,
    val childOrganization: HashSet<OrganizationPreviewDTO>,
    val organizationId: Long
)
