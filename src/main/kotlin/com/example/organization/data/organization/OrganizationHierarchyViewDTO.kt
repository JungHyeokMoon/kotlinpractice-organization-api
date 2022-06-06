package com.example.organization.data.organization

data class OrganizationHierarchyViewDTO(
    val organizationId: Long,
    val organizationName: String,
    val childrenOrganization: Set<OrganizationHierarchyViewDTO>
)
