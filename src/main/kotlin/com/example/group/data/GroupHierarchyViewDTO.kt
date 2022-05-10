package com.example.group.data

data class GroupHierarchyViewDTO(
    val groupId: Long,
    val groupName: String,
    val childrenGroup: Set<GroupHierarchyViewDTO>
)
