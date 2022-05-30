package com.example.group.mapper

import com.example.group.data.group.GroupCreateRequestDTO
import com.example.group.data.group.GroupCreateResponseDTO
import com.example.group.data.group.GroupHierarchyViewDTO
import com.example.group.data.group.GroupParentChangeResponseDTO
import com.example.group.domain.Group
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring")
interface GroupMapper {

    fun toEntity(groupCreateRequestDTO: GroupCreateRequestDTO): Group

    @Mappings(
        Mapping(target = "groupId", source = "id")
    )
    fun toGroupCreateResponseDTO(group: Group): GroupCreateResponseDTO

    @Mappings(
        Mapping(target = "groupId", source = "id"),
        Mapping(target = "parentId", source = "parentGroup.id")
    )
    fun toGroupParentChangeResponseDTO(group: Group): GroupParentChangeResponseDTO

    @Mappings(
        Mapping(target = "groupId", source = "id"),
    )
    fun toGroupHierarchyViewDTO(group: Group?): GroupHierarchyViewDTO
}