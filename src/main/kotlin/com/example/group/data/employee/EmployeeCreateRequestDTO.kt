package com.example.group.data.employee

import com.example.group.enums.Position

data class EmployeeCreateRequestDTO(val name: String,
                                    val position: Position?,
                                    val nickname: String?,
                                    val phoneNumber:String?,
                                    val inUse:Boolean,
                                    val responsibilities:String?,
                                    val groupId: Long?)
