package com.example.group.repository

import com.example.group.domain.Group
import org.springframework.data.jpa.repository.JpaRepository

interface GroupRepository : JpaRepository<Group, Long> {

    fun findByGroupName(groupName: String): Group?
}