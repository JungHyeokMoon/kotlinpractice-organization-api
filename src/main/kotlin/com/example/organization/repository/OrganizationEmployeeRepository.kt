package com.example.organization.repository

import com.example.organization.domain.OrganizationEmployee
import org.springframework.data.jpa.repository.JpaRepository

interface OrganizationEmployeeRepository : JpaRepository<OrganizationEmployee, Long> {
}