package com.example.organization.service

import com.example.organization.data.employee.EmployeeCreateRequestDTO
import com.example.organization.data.employee.EmployeeCreateResponseDTO
import com.example.organization.domain.Organization
import com.example.organization.domain.OrganizationEmployee
import com.example.organization.domain.WorkPeriod
import com.example.organization.enums.ErrorCode
import com.example.organization.exception.CustomException
import com.example.organization.mapper.EmployeeMapper
import com.example.organization.repository.EmployeeRepository
import com.example.organization.repository.OrganizationEmployeeRepository
import com.example.organization.repository.OrganizationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class EmployeeService(
    private val employeeRepository: EmployeeRepository,
    private val organizationEmployeeRepository: OrganizationEmployeeRepository,
    private val groupRepository: OrganizationRepository,
    private val employeeMapper: EmployeeMapper
) {

    @Transactional
    fun joinEmployee(employeeCreateRequestDTO: EmployeeCreateRequestDTO): EmployeeCreateResponseDTO {

        var findGroup: Organization? = null
        employeeCreateRequestDTO.organizationId?.let {
            findGroup = groupRepository.findByIdOrNull(it)
                ?: throw CustomException(ErrorCode.ORGANIZATION_NOT_EXIST_MESSAGE) //request에 groupId가 있는데, 조회시 없으면 익셉션발생
        }

        val employee = employeeRepository.save(employeeMapper.toEntity(employeeCreateRequestDTO))

        var organizationEmployee: OrganizationEmployee? = null
        findGroup?.let {
            organizationEmployee = organizationEmployeeRepository.save(OrganizationEmployee(it, employee, WorkPeriod(
                LocalDateTime.now(), null)))
        }

        return employeeMapper.toEmployeeCreateResponseDTO(employee, organizationEmployee?.organization)
    }
}