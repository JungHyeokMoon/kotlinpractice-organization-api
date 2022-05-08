package com.example.group.service

import com.example.group.data.*
import com.example.group.exception.CustomException
import com.example.group.mapper.GroupMapper
import com.example.group.repository.GroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GroupService(
    private val groupRepository: GroupRepository,
    private val groupMapper: GroupMapper) {

    private val GROUP_NOT_EXIST_MESSAGE = "그룹이 존재하지 않습니다."
    private val PARENT_GROUP_NOT_EXIST_MESSAGE="부모그룹이 존재하지 않습니다."

    @Transactional
    fun createGroup(createRequestDTO: GroupCreateRequestDTO): GroupCreateResponseDTO {
        val group = groupMapper.toEntity(createRequestDTO)
        return groupMapper.toGroupCreateResponseDTO(group)
    }

    @Transactional
    fun changeParent(groupParentChangeRequestDTO: GroupParentChangeRequestDTO): GroupParentChangeResponseDTO{
        val group = groupRepository.findByIdOrNull(groupParentChangeRequestDTO.groupId) ?: throw CustomException(GROUP_NOT_EXIST_MESSAGE)

        val parentGroup = groupRepository.findByIdOrNull(groupParentChangeRequestDTO.parentId) ?: throw CustomException(PARENT_GROUP_NOT_EXIST_MESSAGE)

        group.changeParentGroup(parentGroup)
        return groupMapper.toGroupParentChangeResponseDTO(group)
    }

    @Transactional
    fun removeParentGroup(removeParentGroupRequest: RemoveParentGroupRequest){
        val group = groupRepository.findByIdOrNull(removeParentGroupRequest.groupId) ?: throw CustomException(GROUP_NOT_EXIST_MESSAGE)

        group.removeParentGroup()
    }
}