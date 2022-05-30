package com.example.group.data.group

data class GroupPreviewDTO(
    val groupName: String,
    val parentGroupId: Long? = null,
    val childGroup: HashSet<GroupPreviewDTO>,
    val groupId: Long
)
