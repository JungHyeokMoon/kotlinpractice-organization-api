package com.example.group.domain

import com.example.group.enums.Position
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
/*
Employee쪽에서 groupEmployee를 참조할 상황이 별로 없기때문에, 없애는게 맞다고 판단됨..
{
    @OneToMany(mappedBy = "employee")
    var groupEmployee: MutableSet<GroupEmployee> = HashSet()
}
 */