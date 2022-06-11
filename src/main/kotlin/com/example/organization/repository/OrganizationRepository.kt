package com.example.organization.repository

import com.example.organization.domain.Organization
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrganizationRepository : JpaRepository<Organization, Long> {

    fun findByOrganizationName(organizationName: String): Organization?

    @Query(
        "select o from Organization o " +
                "join fetch o.organizationEmployees oe " +
//                "join fetch oe.employee e " + 회원 softDelete시 organizationEmeployees의 endDate가 null이 아닌것을 처리할 것
                "where o.id = :organizationId"
    )
    fun findOrganizationWithEmployee(
        @Param("organizationId") organizationId: Long,
    ): Organization?
}