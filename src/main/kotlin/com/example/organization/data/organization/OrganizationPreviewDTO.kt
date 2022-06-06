package com.example.organization.data.organization

data class OrganizationPreviewDTO(
    val groupName: String,
    val parentGroupId: Long? = null,
    val childOrganization: HashSet<OrganizationPreviewDTO>,
    val groupId: Long
)
