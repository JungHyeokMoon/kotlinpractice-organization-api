package com.example.organization.domain

import com.example.organization.enums.Position
import javax.persistence.*

@Entity
class Employee(
    var name: String,
    @Enumerated(value = EnumType.STRING) var position: Position? = null,
    var nickname: String? = null,
    var phoneNumber: String? = null,
    var inUse: Boolean = true,
    var responsibilities: String? = null, //담당업무
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
) : BaseEntity()