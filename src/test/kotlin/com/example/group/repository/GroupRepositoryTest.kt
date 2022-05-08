package com.example.group.repository

import com.example.group.domain.Group
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
internal class GroupRepositoryTest @Autowired constructor(
    val groupRepository: GroupRepository,
    val testEntityManager: TestEntityManager
) {

    @Test
    fun `Group insertTest`() {
        val group = Group("조직도")
        groupRepository.save(group)

        val findGroup = groupRepository.findByIdOrNull(group.id!!)

        findGroup!!.run {
            assertThat(groupName).isEqualTo("조직도")
            assertThat(parentGroup).isNull()
            assertThat(childGroup.size).isEqualTo(0)
            assertThat(groupEmployee.size).isEqualTo(0)
        }

        val findByGroupName = groupRepository.findByGroupName("조직도")
        assertThat(findGroup).isSameAs(findByGroupName)
    }

    @Test
    fun `그룹 계층구조 테스트`() {

        val cloudDevelopCenter = Group("클라우드개발센터")
        groupRepository.save(cloudDevelopCenter)

        val commonDevelopLab = Group("공통개발랩", cloudDevelopCenter)
        groupRepository.save(commonDevelopLab)

        val cloudCommonDevelopLab = Group("클라우드공통개발랩", cloudDevelopCenter)
        groupRepository.save(cloudCommonDevelopLab)

        val cloudDataPlatformDevelopLab = Group("클라우드데이터플랫폼개발랩", cloudDevelopCenter)
        groupRepository.save(cloudDataPlatformDevelopLab)

        val cloudInfraDevelopLab = Group("클라우드인프라개발랩", cloudDevelopCenter)
        groupRepository.save(cloudInfraDevelopLab)

        val cloudPlatformDevelopLab = Group("클라우드플랫폼개발랩", cloudDevelopCenter)
        groupRepository.save(cloudPlatformDevelopLab)

        val messagingPlatFormDevelopTeam = Group("메세징플랫폼개발팀", cloudPlatformDevelopLab)
        groupRepository.save(messagingPlatFormDevelopTeam)

        val monitoringPlatformDevelopTeam = Group("모니터링플랫폼개발팀", cloudPlatformDevelopLab)
        groupRepository.save(monitoringPlatformDevelopTeam)

        val infraPlatformDevelopTeam = Group("인프라플랫폼개발팀", cloudPlatformDevelopLab)
        groupRepository.save(infraPlatformDevelopTeam)

        testEntityManager.clear()

        println("영속성컨텍스트를 초기화합니다.")

        val findCloudDevelopCenter = groupRepository.findByIdOrNull(cloudDevelopCenter.id)
        findCloudDevelopCenter!!.run {
            assertThat(groupName).isEqualTo("클라우드개발센터")
            println("Lazy loading이라 추가적인 쿼리가 발생합니다.")
            assertThat(childGroup.size).isEqualTo(5)

            childGroup.forEach { group ->
                println(group.groupName)
                assertThat(group.parentGroup).isSameAs(findCloudDevelopCenter)
                if (group.groupName == "클라우드플랫폼개발랩") {
                    println("하위 그룹들을 가져올때 lazy loading이기 때문에 한번더 쿼리가 실행됩니다.")
                    assertThat(group.childGroup.size).isEqualTo(3)
                }
            }
        }


    }

}