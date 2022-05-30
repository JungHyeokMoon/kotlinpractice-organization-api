package com.example.group.domain

import javax.persistence.*

@Entity
class GroupEmployee(
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id")val group: Group,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employee_id")val employee: Employee,
    var inUse: Boolean=true,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
): BaseEntity()