package com.example.organization.service


import com.example.organization.data.organization.OrganizationCreateRequestDTO
import com.example.organization.data.organization.OrganizationCreateResponseDTO
import com.example.organization.data.organization.OrganizationParentChangeRequestDTO
import com.example.organization.data.organization.RemoveParentOrganizationRequestDTO
import com.example.organization.domain.Organization
import com.example.organization.enums.ErrorCode
import com.example.organization.exception.CustomException
import com.example.organization.mapper.OrganizationEmployeeMapperImpl
import com.example.organization.mapper.OrganizationMapper
import com.example.organization.mapper.OrganizationMapperImpl

import com.example.organization.repository.OrganizationRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
internal class OrganizationServiceTest {

    @MockK
    private lateinit var organizationRepository: OrganizationRepository

    private lateinit var organizationMapper: OrganizationMapper

    private lateinit var organizationService: OrganizationService

    @Test
    fun `organization createTest - organization Entity가 같은 이름을 가졌을경우, 익셉션을 발생시킵니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val organizationCreateRequestDTO = OrganizationCreateRequestDTO("testorganization")
        every {  organizationRepository.findByOrganizationName(any())} returns Organization("testorganization")

        val assertThrows = assertThrows<CustomException> { organizationService.createOrganization(organizationCreateRequestDTO) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.ORGANIZATION_NAME_AlREADY_EXIST.message)
    }

    @Test
    fun `organization createTest`() {
        this.organizationMapper = mockk()
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val organizationCreateRequestDTO = OrganizationCreateRequestDTO("testorganization")
        val createdOrganization = Organization(organizationCreateRequestDTO.organizationName)
        val savedOrganization = Organization(organizationCreateRequestDTO.organizationName, id = 1L)
        val organizationCreateResponseDTO =
            OrganizationCreateResponseDTO(
                organizationCreateRequestDTO.organizationName,
                true,
                savedOrganization.id!!
            ) //이거 스터빙을 안하고싶은데 partial Mocking이안됨

        every { organizationMapper.toEntity(organizationCreateRequestDTO) } returns createdOrganization
        every { organizationRepository.save(createdOrganization) } returns savedOrganization
        every { organizationRepository.findByOrganizationName(any()) } returns null
        every { organizationMapper.toOrganizationCreateResponseDTO(savedOrganization) } answers { organizationCreateResponseDTO }

        organizationService.createOrganization(organizationCreateRequestDTO)

        verify { organizationMapper.toEntity(organizationCreateRequestDTO) }
        verify { organizationMapper.toOrganizationCreateResponseDTO(savedOrganization) }

        organizationService.createOrganization(organizationCreateRequestDTO)
            .run {
                assertThat(organizationId).isEqualTo(savedOrganization.id)
                assertThat(organizationName).isEqualTo(organizationCreateRequestDTO.organizationName)
            }
    }

    @Test
    fun `changeParent Test - failed(바꾸려는 그룹의 id가 존재하지 않을경우 실패합니다)`() {
        organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        val organizationParentChangeRequestDTO = OrganizationParentChangeRequestDTO(1L, 2L)
        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.organizationId) } returns null

        val assertThrows = assertThrows<CustomException> { organizationService.changeParent(organizationParentChangeRequestDTO) }
        Assertions.assertEquals(assertThrows.message, ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `changeParent Test - fail( parentorganization을 찾았을경우 존재하지 않을시 실패합니다)`() {
        organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        val organizationParentChangeRequestDTO = OrganizationParentChangeRequestDTO(1L, 2L)
        val organization = Organization("컴퓨터공학과", id = organizationParentChangeRequestDTO.organizationId)
        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.organizationId) } returns organization
        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.parentId) } returns null

        val assertThrows = assertThrows<CustomException> { organizationService.changeParent(organizationParentChangeRequestDTO) }
        Assertions.assertEquals(assertThrows.message, ErrorCode.PARENT_ORGANIZATION_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `changeParent Test - success(organization에 기존 parent가 존재할경우)`() {
        organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val originalParentOrganization = Organization("사범대학", id = 3L)
        val organizationParentChangeRequestDTO = OrganizationParentChangeRequestDTO(1L, 2L)
        val organization = Organization("컴퓨터공학과", id = organizationParentChangeRequestDTO.organizationId)
        val changedOrganization = Organization("공과대학", id = 1L)

        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.organizationId) } returns organization
        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.parentId) } returns changedOrganization

        organizationService.changeParent(organizationParentChangeRequestDTO).run {
            assertThat(organizationId).isEqualTo(organization.id)
            assertThat(parentId).isEqualTo(changedOrganization.id)
        }

        assertThat(organization.parentOrganization).isEqualTo(changedOrganization)
        assertThat(changedOrganization.childrenOrganization.contains(organization)).isEqualTo(true)
    }

    @Test
    fun `changeParent Test - success(organization에 기존 parent가 존재하지 않을경우)`() {

        organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val organizationParentChangeRequestDTO = OrganizationParentChangeRequestDTO(1L, 2L)
        val organization = Organization("컴퓨터공학과", id = organizationParentChangeRequestDTO.organizationId)
        val parentOrganization = Organization("공과대학", id = 1L)
        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.organizationId) } returns organization
        every { organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.parentId) } returns parentOrganization

        organizationService.changeParent(organizationParentChangeRequestDTO).run {
            assertThat(organizationId).isEqualTo(organization.id)
            assertThat(parentId).isEqualTo(parentOrganization.id)
        }

        assertThat(organization.parentOrganization).isEqualTo(parentOrganization)
        assertThat(parentOrganization.childrenOrganization.contains(organization)).isEqualTo(true)
    }

    @Test
    fun `removeParentorganization test - fail(organization이 존재하지 않을경우 실패합니다)`() {

        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val removeParentOrganizationRequest = RemoveParentOrganizationRequestDTO(1L)
        every { organizationRepository.findByIdOrNull(removeParentOrganizationRequest.organizationId) } returns null
        val assertThrows = assertThrows<CustomException> { organizationService.removeParentOrganization(removeParentOrganizationRequest) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `removeParentorganization test - success(organization의 parentorganization이 존재할경우, parentorganization의 child에서 organization을 지우고, organization의 parentorganization을 null처리합니다)`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val removeParentOrganizationRequest = RemoveParentOrganizationRequestDTO(2L)
        val parentOrganization = Organization("공과대학", id = 1L)
        val organization = Organization("컴퓨터공학과", parentOrganization = parentOrganization, id = 2L)
        parentOrganization.childrenOrganization.add(organization)

        every { organizationRepository.findByIdOrNull(removeParentOrganizationRequest.organizationId) } returns organization

        organizationService.removeParentOrganization(removeParentOrganizationRequest)

        assertThat(organization.parentOrganization).isNull()
        assertThat(parentOrganization.childrenOrganization.contains(organization)).isFalse
    }

    @Test
    fun `removeParentorganization test - success(organization의 parentorganization이 없다 하더라도, 익셉션발생없이 organization엔티티의 removeParentorganization메서드가 정상동작합니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val removeParentOrganizationRequest = RemoveParentOrganizationRequestDTO(2L)
        val organization = Organization("컴퓨터공학과", id = 2L)

        every { organizationRepository.findByIdOrNull(removeParentOrganizationRequest.organizationId) } returns organization

        organizationService.removeParentOrganization(removeParentOrganizationRequest)

        assertThat(organization.parentOrganization).isNull()
    }

    @Test
    fun `allorganizationView test - 조직도 이름이 존재하지 않을시 null반환`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        every { organizationRepository.findAll() } returns emptyList<Organization>()
        assertThat(organizationService.allOrganizationView()).isNull()
    }

    @Test
    fun `allorganizationView test - 조직도 이름이 존재할때, 그룹계층구조를 반환합니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        val rootOrganization = Organization("조직도", id = 1L)
        val tech = Organization("공과대학", parentOrganization = rootOrganization, id = 2L)
        val liberal = Organization("문과대학", parentOrganization = rootOrganization, id = 3L)
        rootOrganization.childrenOrganization.add(tech)
        rootOrganization.childrenOrganization.add(liberal)
        val computer = Organization("컴퓨터공학과", parentOrganization = tech, id = 4L)
        tech.childrenOrganization.add(computer)
        every { organizationRepository.findAll() } returns listOf(rootOrganization, tech, liberal, computer)

        val allorganizationView = organizationService.allOrganizationView()
        allorganizationView.run {
            assertThat(organizationName).isEqualTo("조직도")
            assertThat(organizationId).isEqualTo(1L)
            assertThat(childrenOrganization.size).isEqualTo(2)
        }
        allorganizationView.childrenOrganization.toList().run {
            assertThat(component1().organizationName).isEqualTo("공과대학")
            assertThat(component1().organizationId).isEqualTo(2L)
            assertThat(component1().childrenOrganization.size).isEqualTo(1)
            assertThat(component1().childrenOrganization.toList()[0].organizationName).isEqualTo("컴퓨터공학과")
            assertThat(component2().organizationName).isEqualTo("문과대학")
            assertThat(component2().organizationId).isEqualTo(3L)
            assertThat(component2().childrenOrganization.size).isEqualTo(0)
        }
    }

    @Test
    fun `allorganizationView Test - 조직 전체조회시에, inUse가 false인것을 포함한다면, service의 결과로는 포함하지않도록 합니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        val rootOrganization = Organization("조직도", id = 1L)
        val tech = Organization("공과대학", inUse = false, parentOrganization = rootOrganization, id = 2L)
        val liberal = Organization("문과대학", parentOrganization = rootOrganization, id = 3L)
        rootOrganization.childrenOrganization.add(tech)
        rootOrganization.childrenOrganization.add(liberal)

        val management = Organization("경영대학", inUse = true, parentOrganization = liberal, id = 4L)
        val korean = Organization("국문학과", inUse = false, parentOrganization = liberal, id = 5L)
        liberal.childrenOrganization.add(management)
        liberal.childrenOrganization.add(korean)

        every { organizationRepository.findAll() } returns listOf(rootOrganization, tech, liberal)

        val allorganizationView = organizationService.allOrganizationView()

        allorganizationView.run {
            assertThat(childrenOrganization.size).isEqualTo(1)
        }

        allorganizationView.childrenOrganization.toList().run {
            assertThat(component1().organizationName).isEqualTo("문과대학")
            assertThat(component1().organizationId).isEqualTo(3L)

            assertThat(component1().childrenOrganization.size).isEqualTo(1)
        }
    }

    @Test
    fun `softDeleteorganization Test - organizationId로 조회했을시, 존재하지 않을경우, 익셉션을 발생시킵니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        every { organizationRepository.findByIdOrNull(1L) } returns null

        val assertThrows = assertThrows<CustomException> { organizationService.softDeleteOrganization(1L) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `softDeleteorganization Test - organization을 조회했는데, 하위 그룹이 존재할경우, 익셉션을 발생시킵니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)

        val findOrganization = Organization("그룹!!", true, null, 1L)
        findOrganization.childrenOrganization.add(Organization("하위그룹", true, findOrganization, 2L))
        every { organizationRepository.findByIdOrNull(1L) } returns findOrganization

        val assertThrows = assertThrows<CustomException> { organizationService.softDeleteOrganization(1L) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.ORGANIZATION_HAS_CHILDREN_ORGANIZATION.message)
    }

    @Test
    fun `softDeleteorganization test - 그룹을 조회했는데, 조직의 이름이 최상위 그룹의 이름이면, 그룹을 삭제할수 없습니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        val findorganization = Organization("조직도", true, null, 1L)
        every { organizationRepository.findByIdOrNull(1L) } returns findorganization

        val assertThrows = assertThrows<CustomException> { organizationService.softDeleteOrganization(1L) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.WANT_TO_DELETE_ORGANIZATION_IS_ROOT.message)
    }

    @Test
    fun `softDeleteorganization Test - organizationId로도 조회가되고, 하위그룹이 존재하지 않을경우 정상동작합니다`() {
        this.organizationMapper = OrganizationMapperImpl(OrganizationEmployeeMapperImpl())
        this.organizationService = OrganizationService(organizationRepository, organizationMapper)
        val findOrganization = Organization("조직도의 하위그룹", true, null, 2L)
        every { organizationRepository.findByIdOrNull(2L) } returns findOrganization

        organizationService.softDeleteOrganization(2L)

        assertThat(findOrganization.inUse).isEqualTo(false)
    }
}