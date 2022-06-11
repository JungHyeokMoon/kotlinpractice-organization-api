package com.example.organization.mapper


import com.example.organization.data.organization.OrganizationCreateRequestDTO
import com.example.organization.domain.Organization
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers

internal class OrganizationMapperTest {

    lateinit var mapper: OrganizationMapper;

    @BeforeEach
    fun setUp() {
        mapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
    }


    @Test
    fun `GroupMapper CreateRequestDTO to Entity`() {

        val createRequestDTO = OrganizationCreateRequestDTO("클라우드개발센터")
        val group = mapper.toEntity(createRequestDTO)
        group.run {
            assertThat(organizationName).isEqualTo("클라우드개발센터")
            assertThat(parentOrganization).isNull()
            assertThat(childrenOrganization).isEmpty()
            assertThat(organizationEmployees).isEmpty()
        }
    }

    @Test
    fun `GroupMapper entity to CreateResponeDTO, parentGroup이 존재할때`() {
        val parentGroup = Organization("NHN Cloud", id = 1L)
        val group = Organization("클라우드개발센터", parentOrganization = parentGroup, id = 2L)

        val mappedCreateResponseDTO = mapper.toOrganizationCreateResponseDTO(group)
        mappedCreateResponseDTO.run {
            assertThat(organizationName).isEqualTo("클라우드개발센터")
            assertThat(organizationId).isEqualTo(group.id)
        }
    }

    @Test
    fun `GroupMapper entity to CreateResponseDTO, parentGroup이 존재하지 않을때`() {
        val group = Organization("클라우드개발센터", id = 1L)
        val mappedCreateResponseDTO = mapper.toOrganizationCreateResponseDTO(group)
        mappedCreateResponseDTO.run {
            assertThat(organizationName).isEqualTo("클라우드개발센터")
            assertThat(organizationId).isEqualTo(1L)
        }
    }

    @Test
    fun `GroupMapper entity to GroupHierarchyViewDTO`() {
        val rootGroup = Organization("홍익대학교", id = 1L)
        val tech = Organization("공과대학", parentOrganization = rootGroup, id = 2L)
        val liberal = Organization("문과대학", parentOrganization = rootGroup, id=3L)
        rootGroup.childrenOrganization.add(tech)
        rootGroup.childrenOrganization.add(liberal)
        val computer = Organization("컴퓨터공학과", parentOrganization = tech, id = 4L)
        tech.childrenOrganization.add(computer)

        val toGroupHierarchyViewDTO = mapper.toOrganizationHierarchyViewDTO(rootGroup)
        toGroupHierarchyViewDTO.run {
            assertThat(organizationName).isEqualTo("홍익대학교")
            assertThat(organizationId).isEqualTo(1L)
            assertThat(childrenOrganization.size).isEqualTo(2)
        }
        toGroupHierarchyViewDTO.childrenOrganization.toList().run {
            assertThat(component1().organizationName).isEqualTo("공과대학")
            assertThat(component1().organizationId).isEqualTo(2L)
            assertThat(component1().childrenOrganization.size).isEqualTo(1)
            assertThat(component1().childrenOrganization.toList()[0].organizationName).isEqualTo("컴퓨터공학과")
            assertThat(component2().organizationName).isEqualTo("문과대학")
            assertThat(component2().organizationId).isEqualTo(3L)
            assertThat(component2().childrenOrganization.size).isEqualTo(0)
        }
    }
}