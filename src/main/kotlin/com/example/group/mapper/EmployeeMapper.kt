package com.example.group.mapper

import com.example.group.data.employee.EmployeeCreateRequestDTO
import com.example.group.data.employee.EmployeeCreateResponseDTO
import com.example.group.domain.Employee
import com.example.group.domain.Group
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface EmployeeMapper {

    fun toEntity(employeeCreateRequest: EmployeeCreateRequestDTO): Employee

    @Mappings(
        Mapping(target = "position", expression = "java(employee.getPosition()==null? null: employee.getPosition().toString())"),
        Mapping(target = "groupId", source = "group.id"),
        Mapping(target = "id", source = "employee.id"),
        Mapping(target = "inUse", source = "employee.inUse")
    )
    fun toEmployeeCreateResponseDTO(employee: Employee, group: Group? = null): EmployeeCreateResponseDTO
}