package com.example.group.service

import com.example.group.data.employee.EmployeeCreateRequestDTO
import com.example.group.data.employee.EmployeeCreateResponseDTO
import com.example.group.domain.Group
import com.example.group.domain.GroupEmployee
import com.example.group.enums.ErrorCode
import com.example.group.enums.Position
import com.example.group.exception.CustomException
import com.example.group.mapper.EmployeeMapper
import com.example.group.repository.EmployeeRepository
import com.example.group.repository.GroupEmployeeRepository
import com.example.group.repository.GroupRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val groupEmployeeRepository: GroupEmployeeRepository,
    private val groupRepository: GroupRepository,
    private val employeeMapper: EmployeeMapper
) {

    @Transactional
    fun joinEmployee(employeeCreateRequestDTO: EmployeeCreateRequestDTO): EmployeeCreateResponseDTO {

        var findGroup: Group? = null
        employeeCreateRequestDTO.groupId?.let {
            findGroup = groupRepository.findByIdOrNull(it)
                ?: throw CustomException(ErrorCode.GROUP_NOT_EXIST_MESSAGE) //request에 groupId가 있는데, 조회시 없으면 익셉션발생
        }

        val employee = employeeRepository.save(employeeMapper.toEntity(employeeCreateRequestDTO))

        var groupEmployee: GroupEmployee? = null
        findGroup?.let {
            groupEmployee = groupEmployeeRepository.save(GroupEmployee(it, employee))
        }

        return employeeMapper.toEmployeeCreateResponseDTO(employee, groupEmployee?.group)
    }
}