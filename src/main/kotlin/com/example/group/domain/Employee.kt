package com.example.group.domain

import com.example.group.enums.Position
import javax.persistence.*

@Entity
class Employee(
    var name: String,
    @Enumerated(value = EnumType.STRING) var position: Position,
    var nickname: String?,
    var phoneNumber: String?,
    var responsibilities: String?,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
) : BaseEntity(){
    @OneToMany(mappedBy = "employee") var groupEmployee: MutableSet<GroupEmployee> = HashSet()
}