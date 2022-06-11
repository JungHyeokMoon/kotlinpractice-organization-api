package com.example.organization.service

import com.example.organization.data.organization.*
import com.example.organization.domain.Organization
import com.example.organization.enums.ErrorCode
import com.example.organization.exception.CustomException
import com.example.organization.mapper.OrganizationMapper
import com.example.organization.repository.OrganizationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private fun Organization.filteringInUsePropertyEqualToTrue() {
    val iterator = childrenOrganization.iterator()
    while (iterator.hasNext()) {
        val childOrganization = iterator.next()

        if (!childOrganization.inUse) {
            iterator.remove()
        } else {
            childOrganization.filteringInUsePropertyEqualToTrue()
        }
    }
} /* 필터링을 하는 좀 더 좋은 방법이 있는지 생각을 해봐야함. var이 아닌 val로선언했기때문에, 이렇게 필터링을 하고있는데, 그냥 함수형 프로그래밍처럼 필터링해서 갈아끼우는 방식으로 해도 괜찮을지 좀 더 고려를해봐야한다.*/

@Service
@Transactional(readOnly = true)
class OrganizationService(
    private val organizationRepository: OrganizationRepository,
    private val organizationMapper: OrganizationMapper
) {

    private val ROOT_ORGANIZATION = "조직도"

    @Transactional
    fun createOrganization(createRequestDTO: OrganizationCreateRequestDTO): OrganizationCreateResponseDTO {

        if (organizationRepository.findByOrganizationName(createRequestDTO.organizationName) != null) {
            throw CustomException(ErrorCode.ORGANIZATION_NAME_AlREADY_EXIST)
        }

        val organization = organizationMapper.toEntity(createRequestDTO)
        return organizationMapper.toOrganizationCreateResponseDTO(organizationRepository.save(organization))
    }

    @Transactional
    fun changeParent(organizationParentChangeRequestDTO: OrganizationParentChangeRequestDTO): OrganizationParentChangeResponseDTO {
        val organization = organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.organizationId)
            ?: throw CustomException(
                ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE
            )

        val parentOrganization =
            organizationRepository.findByIdOrNull(organizationParentChangeRequestDTO.parentId) ?: throw CustomException(
                ErrorCode.PARENT_ORGANIZATION_NOT_EXIST_MESSAGE
            )

        organization.changeParentOrganization(parentOrganization)
        return organizationMapper.toOrganizationParentChangeResponseDTO(organization)
    }

    @Transactional
    fun removeParentOrganization(removeParentOrganizationRequest: RemoveParentOrganizationRequestDTO) {
        val organization = organizationRepository.findByIdOrNull(removeParentOrganizationRequest.organizationId)
            ?: throw CustomException(
                ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE
            )

        organization.removeParentOrganization()
    }

    /**
     * 조직도 계층구조를 효율적으로 조회하기 위해, 조직을 모두 조회한 후, 루트그룹을 변환시켜서 내보냅니다.
     * inUse가 true인 Organization만 내보냅니다.
     */
    fun allOrganizationView(): OrganizationHierarchyViewDTO {
        val rootOrganization = organizationRepository.findAll()
            .singleOrNull { organization -> organization.organizationName == ROOT_ORGANIZATION }

        rootOrganization?.filteringInUsePropertyEqualToTrue()
        return organizationMapper.toOrganizationHierarchyViewDTO(rootOrganization)
    }

    @Transactional
    fun softDeleteOrganization(organizationId: Long) {
        val deletedOrganization = organizationRepository.findByIdOrNull(organizationId) ?: throw CustomException(
            ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE
        )

        if (deletedOrganization.childrenOrganization.size != 0) {
            throw CustomException(ErrorCode.ORGANIZATION_HAS_CHILDREN_ORGANIZATION)
        }

        if (deletedOrganization.organizationName == ROOT_ORGANIZATION) {
            throw CustomException(ErrorCode.WANT_TO_DELETE_ORGANIZATION_IS_ROOT)
        }

        deletedOrganization.inUse = false
    }

    fun getOrganizationWithEmployees(organizationId: Long): OrganizationWithEmployeeResponseDTO? {
        val findGroup = organizationRepository.findOrganizationWithEmployee(organizationId)
//        findGroup?.organizationEmployees
        //Todo : 필터링을 해야합니다.
        return organizationMapper.toOrganizationWithEmployeeResponseDTO(findGroup)
    }
}