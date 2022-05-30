package com.example.group.service

import com.example.group.data.group.*
import com.example.group.domain.Group
import com.example.group.exception.CustomException
import com.example.group.mapper.GroupMapper
import com.example.group.repository.GroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

fun Group.filteringInUsePropertyEqualToTrue(){
    val iterator = childrenGroup.iterator()
    while (iterator.hasNext()){
        val childGroup = iterator.next()

        if(!childGroup.inUse){
            iterator.remove()
        }else{
            childGroup.filteringInUsePropertyEqualToTrue()
        }
    }
}

@Service
@Transactional(readOnly = true)
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMapper: GroupMapper
) {

    private val GROUP_NOT_EXIST_MESSAGE = "그룹이 존재하지 않습니다."
    private val PARENT_GROUP_NOT_EXIST_MESSAGE = "부모그룹이 존재하지 않습니다."

    private val ROOT_GROUP="조직도"

    @Transactional
    fun createGroup(createRequestDTO: GroupCreateRequestDTO): GroupCreateResponseDTO {
        val group = groupMapper.toEntity(createRequestDTO)
        return groupMapper.toGroupCreateResponseDTO(group)
    }

    @Transactional
    fun changeParent(groupParentChangeRequestDTO: GroupParentChangeRequestDTO): GroupParentChangeResponseDTO {
        val group = groupRepository.findByIdOrNull(groupParentChangeRequestDTO.groupId) ?: throw CustomException(
            GROUP_NOT_EXIST_MESSAGE
        )

        val parentGroup = groupRepository.findByIdOrNull(groupParentChangeRequestDTO.parentId) ?: throw CustomException(
            PARENT_GROUP_NOT_EXIST_MESSAGE
        )

        group.changeParentGroup(parentGroup)
        return groupMapper.toGroupParentChangeResponseDTO(group)
    }

    @Transactional
    fun removeParentGroup(removeParentGroupRequest: RemoveParentGroupRequest) {
        val group = groupRepository.findByIdOrNull(removeParentGroupRequest.groupId) ?: throw CustomException(
            GROUP_NOT_EXIST_MESSAGE
        )

        group.removeParentGroup()
    }

    /**
     * 조직도 계층구조를 효율적으로 조회하기 위해, 조직을 모두 조회한 후, 루트그룹을 변환시켜서 내보냅니다.
     * inUse가 true인 group만 내보냅니다.
     */
    fun allGroupView(): GroupHierarchyViewDTO {
        val rootGroup= groupRepository.findAll()
            .singleOrNull { group -> group.groupName == ROOT_GROUP }

        rootGroup?.filteringInUsePropertyEqualToTrue()

        return groupMapper.toGroupHierarchyViewDTO(rootGroup)
    }
}