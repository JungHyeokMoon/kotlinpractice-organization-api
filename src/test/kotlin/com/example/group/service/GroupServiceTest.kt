package com.example.group.service

import com.example.group.data.group.GroupCreateRequestDTO
import com.example.group.data.group.GroupCreateResponseDTO
import com.example.group.data.group.GroupParentChangeRequestDTO
import com.example.group.data.group.RemoveParentGroupRequest
import com.example.group.domain.Group
import com.example.group.enums.ErrorCode
import com.example.group.exception.CustomException
import com.example.group.mapper.GroupMapper
import com.example.group.mapper.GroupMapperImpl
import com.example.group.repository.GroupRepository
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
internal class GroupServiceTest {

    @MockK
    private lateinit var groupRepository: GroupRepository

    private lateinit var groupMapper: GroupMapper

    private lateinit var groupService: GroupService

    @Test
    fun `group createTest - group Entity가 같은 이름을 가졌을경우, 익셉션을 발생시킵니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val groupCreateRequestDTO = GroupCreateRequestDTO("testGroup")
        every {  groupRepository.findByGroupName(any())} returns Group("testGroup")

        val assertThrows = assertThrows<CustomException> { groupService.createGroup(groupCreateRequestDTO) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.GROUP_NAME_AlREADY_EXIST.message)
    }

    @Test
    fun `group createTest`() {
        this.groupMapper = mockk()
        this.groupService = GroupService(groupRepository, groupMapper)

        val groupCreateRequestDTO = GroupCreateRequestDTO("testGroup")
        val createdGroup = Group(groupCreateRequestDTO.groupName)
        val savedGroup = Group(groupCreateRequestDTO.groupName, id = 1L)
        val groupCreateResponseDTO =
            GroupCreateResponseDTO(
                groupCreateRequestDTO.groupName,
                true,
                savedGroup.id!!
            ) //이거 스터빙을 안하고싶은데 partial Mocking이안됨

        every { groupMapper.toEntity(groupCreateRequestDTO) } returns createdGroup
        every { groupRepository.save(createdGroup) } returns savedGroup
        every { groupRepository.findByGroupName(any()) } returns null
        every { groupMapper.toGroupCreateResponseDTO(savedGroup) } answers { groupCreateResponseDTO }

        groupService.createGroup(groupCreateRequestDTO)

        verify { groupMapper.toEntity(groupCreateRequestDTO) }
        verify { groupMapper.toGroupCreateResponseDTO(savedGroup) }

        groupService.createGroup(groupCreateRequestDTO)
            .run {
                assertThat(groupId).isEqualTo(savedGroup.id)
                assertThat(groupName).isEqualTo(groupCreateRequestDTO.groupName)
            }
    }

    @Test
    fun `changeParent Test - failed(바꾸려는 그룹의 id가 존재하지 않을경우 실패합니다)`() {
        groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        val groupParentChangeRequestDTO = GroupParentChangeRequestDTO(1L, 2L)
        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.groupId) } returns null

        val assertThrows = assertThrows<CustomException> { groupService.changeParent(groupParentChangeRequestDTO) }
        Assertions.assertEquals(assertThrows.message, ErrorCode.GROUP_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `changeParent Test - fail( parentGroup을 찾았을경우 존재하지 않을시 실패합니다)`() {
        groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        val groupParentChangeRequestDTO = GroupParentChangeRequestDTO(1L, 2L)
        val group = Group("컴퓨터공학과", id = groupParentChangeRequestDTO.groupId)
        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.groupId) } returns group
        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.parentId) } returns null

        val assertThrows = assertThrows<CustomException> { groupService.changeParent(groupParentChangeRequestDTO) }
        Assertions.assertEquals(assertThrows.message, ErrorCode.PARENT_GROUP_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `changeParent Test - success(group에 기존 parent가 존재할경우)`() {
        groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val originalParentGroup = Group("사범대학", id = 3L)
        val groupParentChangeRequestDTO = GroupParentChangeRequestDTO(1L, 2L)
        val group = Group("컴퓨터공학과", id = groupParentChangeRequestDTO.groupId)
        val changedGroup = Group("공과대학", id = 1L)

        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.groupId) } returns group
        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.parentId) } returns changedGroup

        groupService.changeParent(groupParentChangeRequestDTO).run {
            assertThat(groupId).isEqualTo(group.id)
            assertThat(parentId).isEqualTo(changedGroup.id)
        }

        assertThat(group.parentGroup).isEqualTo(changedGroup)
        assertThat(changedGroup.childrenGroup.contains(group)).isEqualTo(true)
    }

    @Test
    fun `changeParent Test - success(group에 기존 parent가 존재하지 않을경우)`() {

        groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val groupParentChangeRequestDTO = GroupParentChangeRequestDTO(1L, 2L)
        val group = Group("컴퓨터공학과", id = groupParentChangeRequestDTO.groupId)
        val parentGroup = Group("공과대학", id = 1L)
        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.groupId) } returns group
        every { groupRepository.findByIdOrNull(groupParentChangeRequestDTO.parentId) } returns parentGroup

        groupService.changeParent(groupParentChangeRequestDTO).run {
            assertThat(groupId).isEqualTo(group.id)
            assertThat(parentId).isEqualTo(parentGroup.id)
        }

        assertThat(group.parentGroup).isEqualTo(parentGroup)
        assertThat(parentGroup.childrenGroup.contains(group)).isEqualTo(true)
    }

    @Test
    fun `removeParentGroup test - fail(group이 존재하지 않을경우 실패합니다)`() {

        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val removeParentGroupRequest = RemoveParentGroupRequest(1L)
        every { groupRepository.findByIdOrNull(removeParentGroupRequest.groupId) } returns null
        val assertThrows = assertThrows<CustomException> { groupService.removeParentGroup(removeParentGroupRequest) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.GROUP_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `removeParentGroup test - success(group의 parentGroup이 존재할경우, parentGroup의 child에서 group을 지우고, group의 parentGroup을 null처리합니다)`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val removeParentGroupRequest = RemoveParentGroupRequest(2L)
        val parentGroup = Group("공과대학", id = 1L)
        val group = Group("컴퓨터공학과", parentGroup = parentGroup, id = 2L)
        parentGroup.childrenGroup.add(group)

        every { groupRepository.findByIdOrNull(removeParentGroupRequest.groupId) } returns group

        groupService.removeParentGroup(removeParentGroupRequest)

        assertThat(group.parentGroup).isNull()
        assertThat(parentGroup.childrenGroup.contains(group)).isFalse
    }

    @Test
    fun `removeParentGroup test - success(group의 parentGroup이 없다 하더라도, 익셉션발생없이 Group엔티티의 removeParentGroup메서드가 정상동작합니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val removeParentGroupRequest = RemoveParentGroupRequest(2L)
        val group = Group("컴퓨터공학과", id = 2L)

        every { groupRepository.findByIdOrNull(removeParentGroupRequest.groupId) } returns group

        groupService.removeParentGroup(removeParentGroupRequest)

        assertThat(group.parentGroup).isNull()
    }

    @Test
    fun `allGroupView test - 조직도 이름이 존재하지 않을시 null반환`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        every { groupRepository.findAll() } returns emptyList<Group>()
        assertThat(groupService.allGroupView()).isNull()
    }

    @Test
    fun `allGroupView test - 조직도 이름이 존재할때, 그룹계층구조를 반환합니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        val rootGroup = Group("조직도", id = 1L)
        val tech = Group("공과대학", parentGroup = rootGroup, id = 2L)
        val liberal = Group("문과대학", parentGroup = rootGroup, id = 3L)
        rootGroup.childrenGroup.add(tech)
        rootGroup.childrenGroup.add(liberal)
        val computer = Group("컴퓨터공학과", parentGroup = tech, id = 4L)
        tech.childrenGroup.add(computer)
        every { groupRepository.findAll() } returns listOf(rootGroup, tech, liberal, computer)

        val allGroupView = groupService.allGroupView()
        allGroupView.run {
            assertThat(groupName).isEqualTo("조직도")
            assertThat(groupId).isEqualTo(1L)
            assertThat(childrenGroup.size).isEqualTo(2)
        }
        allGroupView.childrenGroup.toList().run {
            assertThat(component1().groupName).isEqualTo("공과대학")
            assertThat(component1().groupId).isEqualTo(2L)
            assertThat(component1().childrenGroup.size).isEqualTo(1)
            assertThat(component1().childrenGroup.toList()[0].groupName).isEqualTo("컴퓨터공학과")
            assertThat(component2().groupName).isEqualTo("문과대학")
            assertThat(component2().groupId).isEqualTo(3L)
            assertThat(component2().childrenGroup.size).isEqualTo(0)
        }
    }

    @Test
    fun `allGroupView Test - 조직 전체조회시에, inUse가 false인것을 포함한다면, service의 결과로는 포함하지않도록 합니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        val rootGroup = Group("조직도", id = 1L)
        val tech = Group("공과대학", inUse = false, parentGroup = rootGroup, id = 2L)
        val liberal = Group("문과대학", parentGroup = rootGroup, id = 3L)
        rootGroup.childrenGroup.add(tech)
        rootGroup.childrenGroup.add(liberal)

        val management = Group("경영대학", inUse = true, parentGroup = liberal, id = 4L)
        val korean = Group("국문학과", inUse = false, parentGroup = liberal, id = 5L)
        liberal.childrenGroup.add(management)
        liberal.childrenGroup.add(korean)

        every { groupRepository.findAll() } returns listOf(rootGroup, tech, liberal)

        val allGroupView = groupService.allGroupView()

        allGroupView.run {
            assertThat(childrenGroup.size).isEqualTo(1)
        }

        allGroupView.childrenGroup.toList().run {
            assertThat(component1().groupName).isEqualTo("문과대학")
            assertThat(component1().groupId).isEqualTo(3L)

            assertThat(component1().childrenGroup.size).isEqualTo(1)
        }
    }

    @Test
    fun `softDeleteGroup Test - groupId로 조회했을시, 존재하지 않을경우, 익셉션을 발생시킵니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        every { groupRepository.findByIdOrNull(1L) } returns null

        val assertThrows = assertThrows<CustomException> { groupService.softDeleteGroup(1L) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.GROUP_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `softDeleteGroup Test - group을 조회했는데, 하위 그룹이 존재할경우, 익셉션을 발생시킵니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)

        val findGroup = Group("그룹!!", true, null, 1L)
        findGroup.childrenGroup.add(Group("하위그룹", true, findGroup, 2L))
        every { groupRepository.findByIdOrNull(1L) } returns findGroup

        val assertThrows = assertThrows<CustomException> { groupService.softDeleteGroup(1L) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.GROUP_HAS_CHILDREN_GROUP.message)
    }

    @Test
    fun `softDeleteGroup test - 그룹을 조회했는데, 조직의 이름이 최상위 그룹의 이름이면, 그룹을 삭제할수 없습니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        val findGroup = Group("조직도", true, null, 1L)
        every { groupRepository.findByIdOrNull(1L) } returns findGroup

        val assertThrows = assertThrows<CustomException> { groupService.softDeleteGroup(1L) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.WANT_TO_DELETE_GROUP_IS_ROOT.message)
    }

    @Test
    fun `softDeleteGroup Test - groupId로도 조회가되고, 하위그룹이 존재하지 않을경우 정상동작합니다`() {
        this.groupMapper = GroupMapperImpl()
        this.groupService = GroupService(groupRepository, groupMapper)
        val findGroup = Group("조직도의 하위그룹", true, null, 2L)
        every { groupRepository.findByIdOrNull(2L) } returns findGroup

        groupService.softDeleteGroup(2L)

        assertThat(findGroup.inUse).isEqualTo(false)
    }
}