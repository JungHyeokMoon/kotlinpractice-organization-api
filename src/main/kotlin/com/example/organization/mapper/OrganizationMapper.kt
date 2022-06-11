package com.example.organization.mapper

import com.example.organization.data.organization.*
import com.example.organization.domain.Organization
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = [OrganizationEmployeeMapper::class])
interface OrganizationMapper {

    fun toEntity(organizationCreateRequestDTO: OrganizationCreateRequestDTO): Organization

    @Mappings(
        Mapping(target = "organizationId", source = "id")
    )
    fun toOrganizationCreateResponseDTO(organization: Organization): OrganizationCreateResponseDTO

    @Mappings(
        Mapping(target = "organizationId", source = "id"),
        Mapping(target = "parentId", source = "parentOrganization.id")
    )
    fun toOrganizationParentChangeResponseDTO(organization: Organization): OrganizationParentChangeResponseDTO

    @Mappings(
        Mapping(target = "organizationId", source = "id")
    )
    fun toOrganizationHierarchyViewDTO(organization: Organization?): OrganizationHierarchyViewDTO

    @Mappings(
        Mapping(target = "organizationId", source = "id"),
    )
    fun toOrganizationWithEmployeeResponseDTO(organization: Organization?): OrganizationWithEmployeeResponseDTO?
}