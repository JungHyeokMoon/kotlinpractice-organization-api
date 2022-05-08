package com.example.group.data

data class GroupPreviewDTO(
    val groupName: String,
    val parentGroupId: Long? = null,
    val childGroup: HashSet<GroupPreviewDTO>,
    val groupId: Long
)
