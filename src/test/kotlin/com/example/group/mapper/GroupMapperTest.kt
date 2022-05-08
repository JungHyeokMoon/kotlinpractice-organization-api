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
            assertThat(childGroup).isEmpty()
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
}