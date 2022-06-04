package com.example.group.domain

import javax.persistence.*

@Entity
class GroupEmployee(
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "group_id")val group: Group,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "employee_id")val employee: Employee,
//    var inUse: Boolean=true, 데이터 정합성 때문에 employee의 inUse로 체크하는게 나을듯
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
): BaseEntity()