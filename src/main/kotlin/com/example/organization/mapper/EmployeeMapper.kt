package com.example.organization.mapper

import com.example.organization.data.employee.EmployeeCreateRequestDTO
import com.example.organization.data.employee.EmployeeCreateResponseDTO
import com.example.organization.domain.Employee
import com.example.organization.domain.Organization
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface EmployeeMapper {

    fun toEntity(employeeCreateRequest: EmployeeCreateRequestDTO): Employee

    @Mappings(
        Mapping(
            target = "position",
            expression = "java(employee.getPosition().toString())"
        ),
        Mapping(target = "organizationId", source = "organization.id"),
        Mapping(target = "id", source = "employee.id"),
        Mapping(target = "inUse", source = "employee.inUse")
    )
    fun toEmployeeCreateResponseDTO(employee: Employee, organization: Organization? = null): EmployeeCreateResponseDTO
}