package com.example.group.service

import com.example.group.mapper.EmployeeMapper
import com.example.group.repository.EmployeeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MemberService(
    private val employeeRepository: EmployeeRepository,
    private val employeeMapper: EmployeeMapper
) {

}