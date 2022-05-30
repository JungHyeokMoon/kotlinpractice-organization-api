package com.example.group.data.group

data class GroupHierarchyViewDTO(
    val groupId: Long,
    val groupName: String,
    val childrenGroup: Set<GroupHierarchyViewDTO>
)
