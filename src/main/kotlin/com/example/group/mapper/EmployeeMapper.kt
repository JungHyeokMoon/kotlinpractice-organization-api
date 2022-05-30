package com.example.group.mapper

import com.example.group.data.employee.EmployeeCreateRequestDTO
import com.example.group.domain.Employee
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface EmployeeMapper {

    fun toEntity(employeeCreateRequest : EmployeeCreateRequestDTO): Employee
}