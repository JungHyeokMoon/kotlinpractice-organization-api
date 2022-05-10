package com.example.group.domain

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GroupTest {

    @Test
    fun `group primary constructor Test`(){
        val group = Group("조직도")
        group.run {
            assertThat(groupName).isEqualTo("조직도")
            assertThat(parentGroup).isNull()
            assertThat(childrenGroup.isEmpty())
            assertThat(groupEmployee.isEmpty())
        }
    }
}