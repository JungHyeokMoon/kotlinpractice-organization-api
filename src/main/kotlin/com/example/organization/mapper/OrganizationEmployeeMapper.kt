package com.example.organization.mapper

import com.example.organization.data.employee.OrganizationEmployeeDetailDTO
import com.example.organization.domain.OrganizationEmployee
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface OrganizationEmployeeMapper {

    @Mappings(
        Mapping(target = "organizationEmployeeId", source = "id"),
        Mapping(target = "employeeId", source = "employee.id"),
        Mapping(target = "organizationId", source = "organization.id"),
        Mapping(target = "name", source = "employee.name"),
        Mapping(target = "phoneNumber", source = "employee.phoneNumber"),
        Mapping(target = "inUse", source = "employee.inUse"),
        Mapping(target = "responsibilities", source = "employee.responsibilities"),
        Mapping(target = "position", expression = "java(organizationEmployee.getEmployee().getPosition().toString())"),
    )
    fun toOrganizationEmployeeDetailDTO(organizationEmployee: OrganizationEmployee): OrganizationEmployeeDetailDTO
}