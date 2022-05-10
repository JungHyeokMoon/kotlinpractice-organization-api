package com.example.group.domain

import javax.persistence.*

@Entity
@Table(name = "groups")
class Group(

    @Column(unique = true)
    var groupName: String,

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") var parentGroup: Group? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
) : BaseEntity() {
    @OneToMany(mappedBy = "parentGroup")
    val childrenGroup: MutableSet<Group> = HashSet()

    @OneToMany(mappedBy = "group")
    val groupEmployee: MutableSet<GroupEmployee> = HashSet()

    fun changeParentGroup(parentGroup: Group?) {
        parentGroup?.childrenGroup?.add(this)
        this.parentGroup = parentGroup
    }

    fun removeParentGroup() {
        this.parentGroup?.childrenGroup?.remove(this)
        this.parentGroup = null
    }
}
