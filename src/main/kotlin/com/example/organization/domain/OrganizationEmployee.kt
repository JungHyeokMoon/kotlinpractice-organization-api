package com.example.organization.domain

import javax.persistence.*

@Entity
class OrganizationEmployee(
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "organization_id", foreignKey = ForeignKey(name = "fk_organizationEmployee_organization"))val organization: Organization,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employee_id", foreignKey = ForeignKey(name = "fk_organizationEmployee_employee"))val employee: Employee,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
): BaseEntity()