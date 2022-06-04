package com.example.group.repository

import com.example.group.domain.GroupEmployee
import org.springframework.data.jpa.repository.JpaRepository

interface GroupEmployeeRepository : JpaRepository<GroupEmployee, Long> {
}