package com.example.group.service

import com.example.group.data.employee.EmployeeCreateRequestDTO
import com.example.group.domain.Employee
import com.example.group.domain.Group
import com.example.group.domain.GroupEmployee
import com.example.group.enums.ErrorCode
import com.example.group.exception.CustomException
import com.example.group.mapper.EmployeeMapper
import com.example.group.mapper.EmployeeMapperImpl
import com.example.group.repository.EmployeeRepository
import com.example.group.repository.GroupEmployeeRepository
import com.example.group.repository.GroupRepository
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull

@ExtendWith(MockKExtension::class)
internal class EmployeeServiceTest {

    private lateinit var employeeMapper: EmployeeMapper

    @BeforeEach
    fun setUp() {
        employeeMapper = EmployeeMapperImpl()
    }

    @Test
    fun `joinEmployee test - EmployeeCreateRequestDTO에서 groupId가 존재하는데, groupRepository로 조회했을시, 그룹이 존재하지 않으면 익셉션이발생합니다`(
        @RelaxedMockK employeeRepository: EmployeeRepository,
        @RelaxedMockK groupEmployeeRepository: GroupEmployeeRepository,
        @MockK groupRepository: GroupRepository,
    ) {
        val employeeService =
            EmployeeService(employeeRepository, groupEmployeeRepository, groupRepository, employeeMapper)
        val employeeCreateRequestDTO = EmployeeCreateRequestDTO(name = "JungHyeok", groupId = 1L)

        every { groupRepository.findByIdOrNull(any()) } returns null

        val assertThrows = assertThrows<CustomException> { employeeService.joinEmployee(employeeCreateRequestDTO) }
        assertThat(assertThrows.message).isEqualTo(ErrorCode.GROUP_NOT_EXIST_MESSAGE.message)
    }

    @Test
    fun `joinEmployee test - EmployeeCreateRequestDTO에서 groupId가 존재하지 않을시, groupEmployeeRepository의 save호출없이, employeeRepository에만 저장하고 mapstruct로 반환합니다`(
        @MockK employeeRepository: EmployeeRepository,
        @RelaxedMockK groupEmployeeRepository: GroupEmployeeRepository,
        @RelaxedMockK groupRepository: GroupRepository,
    ) {

        val groupService = EmployeeService(employeeRepository, groupEmployeeRepository, groupRepository, employeeMapper)
        val employeeCreateRequestDTO = EmployeeCreateRequestDTO(name = "JungHyeok")

        val savedEmployee = Employee(employeeCreateRequestDTO.name, id = 1L)
        every { employeeRepository.save(any()) } returns savedEmployee

        val joinEmployee = groupService.joinEmployee(employeeCreateRequestDTO)

        joinEmployee.run {
            assertThat(id).isEqualTo(1L)
            assertThat(name).isEqualTo(employeeCreateRequestDTO.name)
            assertThat(inUse).isEqualTo(true)
            assertThat(position).isNull()
            assertThat(phoneNumber).isNull()
            assertThat(responsibilities).isNull()
            assertThat(groupId).isNull()
        }

        verify { groupRepository.findByIdOrNull(employeeCreateRequestDTO.groupId)?.wasNot(Called) }
        verify { groupEmployeeRepository.save(any()) wasNot Called }
    }

    @Test
    fun `joinEmployee test - EmployeeCreateRequestDTO에서 groupId가 존재하고, group table에 엔티티가 존재할경우, 안전한호출이 모두 정상적으로 호출됩니다`(
        @MockK employeeRepository: EmployeeRepository,
        @MockK groupEmployeeRepository: GroupEmployeeRepository,
        @MockK groupRepository: GroupRepository,
    ) {
        val groupService = EmployeeService(employeeRepository, groupEmployeeRepository, groupRepository, employeeMapper)
        val employeeCreateRequestDTO = EmployeeCreateRequestDTO(name = "JungHyeok", groupId = 1L)

        val findGroup = Group("조직도", id = 1L)
        val savedEmployee = Employee(employeeCreateRequestDTO.name, id = 1L)
        val savedGroupEmployee = GroupEmployee(findGroup, savedEmployee, 1L)

        every { groupRepository.findByIdOrNull(employeeCreateRequestDTO.groupId) }returns findGroup
        every { employeeRepository.save(any()) } returns savedEmployee
        every { groupEmployeeRepository.save(any()) } returns savedGroupEmployee

        val joinEmployee = groupService.joinEmployee(employeeCreateRequestDTO)

        joinEmployee.run {
            assertThat(id).isEqualTo(1L)
            assertThat(name).isEqualTo(employeeCreateRequestDTO.name)
            assertThat(inUse).isEqualTo(true)
            assertThat(position).isNull()
            assertThat(phoneNumber).isNull()
            assertThat(responsibilities).isNull()
            assertThat(groupId).isEqualTo(1L)
        }

    }
}