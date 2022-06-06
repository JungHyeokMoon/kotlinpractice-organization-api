package com.example.organization.domain

import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OrganizationTest {

    @Test
    fun `group primary constructor Test`(){
        val organization = Organization("조직도")
        organization.run {
            assertThat(organizationName).isEqualTo("조직도")
            assertThat(parentOrganization).isNull()
            assertThat(childrenOrganization.isEmpty())
            assertThat(organizationEmployee.isEmpty())
        }
    }
}