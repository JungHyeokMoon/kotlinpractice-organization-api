package com.example.organization.mapper

import com.example.organization.data.organization.OrganizationCreateRequestDTO
import com.example.organization.data.organization.OrganizationCreateResponseDTO
import com.example.organization.data.organization.OrganizationHierarchyViewDTO
import com.example.organization.data.organization.OrganizationParentChangeResponseDTO
import com.example.organization.domain.Organization
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
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
}