package com.example.group.mapper

import com.example.group.data.GroupCreateRequestDTO
import com.example.group.domain.Group

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

internal class GroupMapperTest {

    lateinit var mapper: GroupMapper;

    @BeforeEach
    fun setUp() {
        mapper = Mappers.getMapper(GroupMapper::class.java)
    }


    @Test
    fun `GroupMapper CreateRequestDTO to Entity`() {

        val createRequestDTO = GroupCreateRequestDTO("클라우드개발센터")
        val group = mapper.toEntity(createRequestDTO)
        group.run {
            assertThat(groupName).isEqualTo("클라우드개발센터")
            assertThat(parentGroup).isNull()
            assertThat(childrenGroup).isEmpty()
            assertThat(groupEmployee).isEmpty()
        }
    }

    @Test
    fun `GroupMapper entity to CreateResponeDTO, parentGroup이 존재할때`() {
        val parentGroup = Group("NHN Cloud", id = 1L)
        val group = Group("클라우드개발센터", parentGroup, 2L)

        val mappedCreateResponseDTO = mapper.toGroupCreateResponseDTO(group)
        mappedCreateResponseDTO.run {
            assertThat(groupName).isEqualTo("클라우드개발센터")
            assertThat(groupId).isEqualTo(group.id)
        }
    }

    @Test
    fun `GroupMapper entity to CreateResponseDTO, parentGroup이 존재하지 않을때`(){
        val group = Group("클라우드개발센터",id=1L)
        val mappedCreateResponseDTO=mapper.toGroupCreateResponseDTO(group)
        mappedCreateResponseDTO.run {
            assertThat(groupName).isEqualTo("클라우드개발센터")
            assertThat(groupId).isEqualTo(1L)
        }
    }

    @Test
    fun `GroupMapper entity to GroupHierarchyViewDTO`(){
        val rootGroup =Group("홍익대학교", id=1L)
        val tech = Group("공과대학", rootGroup, 2L)
        val liberal = Group("문과대학", rootGroup, 3L)
        rootGroup.childrenGroup.add(tech)
        rootGroup.childrenGroup.add(liberal)
        val computer=Group("컴퓨터공학과",tech,4L)
        tech.childrenGroup.add(computer)

        val toGroupHierarchyViewDTO = mapper.toGroupHierarchyViewDTO(rootGroup)
        toGroupHierarchyViewDTO.run {
            assertThat(groupName).isEqualTo("홍익대학교")
            assertThat(groupId).isEqualTo(1L)
            assertThat(childrenGroup.size).isEqualTo(2)
        }
        toGroupHierarchyViewDTO.childrenGroup.toList().run {
            assertThat( component1().groupName).isEqualTo("공과대학")
            assertThat(component1().groupId).isEqualTo(2L)
            assertThat(component1().childrenGroup.size).isEqualTo(1)
            assertThat( component1().childrenGroup.toList()[0].groupName).isEqualTo("컴퓨터공학과")
            assertThat(component2().groupName).isEqualTo("문과대학")
            assertThat(component2().groupId).isEqualTo(3L)
            assertThat(component2().childrenGroup.size).isEqualTo(0)
        }
    }
}