package com.example.organization.domain

import javax.persistence.*

@Entity
@Table(
    uniqueConstraints =
    [UniqueConstraint(name = "uq_organizationName", columnNames = ["organizationName"])]
)
class Organization(

    var organizationName: String,

    var inUse: Boolean = true,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(
        name = "parent_id",
        foreignKey = ForeignKey(name = "fk_organization_parentOrganization")
    ) var parentOrganization: Organization? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {
    @OneToMany(mappedBy = "parentOrganization")
    val childrenOrganization: MutableSet<Organization> = HashSet()

    @OneToMany(mappedBy = "organization")
    val organizationEmployee: MutableSet<OrganizationEmployee> = HashSet()

    fun changeParentOrganization(parentOrganization: Organization?) {
        parentOrganization?.childrenOrganization?.add(this)
        this.parentOrganization = parentOrganization
    }

    fun removeParentOrganization() {
        this.parentOrganization?.childrenOrganization?.remove(this)
        this.parentOrganization = null
    }
}
