package com.example.organization.controller

import com.example.organization.service.OrganizationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "organizationController", description = "조직도에 관한 API 리스트")
@RestController
class OrganizationController(
    private val organizationService: OrganizationService
) {

    @Operation(method = "GET", description = "조직도를 전체조회합니다.")
    @GetMapping("/group-hierarchy")
    fun getGroupHierarchy() = organizationService.allOrganizationView()

}