package com.example.group.domain

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(

    var groupName: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") var parentGroup: Group? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {
    @OneToMany(mappedBy = "parentGroup")
    val childGroup: MutableSet<Group> = HashSet()

    @OneToMany(mappedBy = "group")
    val groupEmployee: MutableSet<GroupEmployee> = HashSet()

    fun changeParentGroup(parentGroup: Group?) {
        parentGroup?.childGroup?.add(this)
        this.parentGroup = parentGroup
    }

    fun removeParentGroup() {
        this.parentGroup?.childGroup?.remove(this)
        this.parentGroup = null
    }
}
