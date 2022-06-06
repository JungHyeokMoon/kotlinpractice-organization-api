package com.example.organization.repository

import com.example.organization.domain.Organization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrganizationRepository : JpaRepository<Organization, Long> {

    fun findByOrganizationName(organizationName: String): Organization?

    @Query(
        "select o from Organization o " +
                "join fetch o.organizationEmployee oe " +
                "join fetch oe.employee e " +
                "where o.id = :organizationId and e.inUse = :inUse"
    )
    fun findOrganizationWithEmployee(@Param("organizationId") organizationId: Long, @Param("inUse") inUse: Boolean): Organization?
}