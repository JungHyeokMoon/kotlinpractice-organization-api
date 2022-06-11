package com.example.organization.configuration

import com.example.organization.controller.OrganizationController
import com.example.organization.enums.ErrorCode
import com.example.organization.exception.CustomException
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Matcher
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.aspectj.lang.annotation.Before
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ExtendWith(MockKExtension::class)
internal class RestControllerAdviceConfigurationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var organizationController: OrganizationController

    @Test
    fun `exceptionHandlerTest - CustomException`(){
        every { organizationController.getOrganizationHierarchy() } throws CustomException(ErrorCode.PARENT_ORGANIZATION_NOT_EXIST_MESSAGE)
        mockMvc.get("/organization-hierarchy")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.success", Matchers.`is`(false))
                jsonPath("$.failMessage", Matchers.`is`(ErrorCode.PARENT_ORGANIZATION_NOT_EXIST_MESSAGE.message))
                jsonPath("$.responseBody", Matchers.nullValue())
            }
    }

    @Test
    fun `exceptionHandlerTest - ExceptionTest`(){
        every { organizationController.getOrganizationHierarchy() } throws java.lang.Exception()
        mockMvc.get("/organization-hierarchy")
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                }
                jsonPath("$.success", Matchers.`is`(false))
                jsonPath("$.failMessage", Matchers.`is`("요청 처리과정중에 오류가 발생했습니다."))
                jsonPath("$.responseBody", Matchers.nullValue())
            }
    }
}