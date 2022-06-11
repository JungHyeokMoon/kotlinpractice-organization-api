package com.example.organization.repository

import com.example.organization.domain.Employee
import com.example.organization.domain.Organization
import com.example.organization.domain.OrganizationEmployee
import com.example.organization.domain.WorkPeriod
import com.example.organization.enums.Position
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@DataJpaTest
internal class OrganizationRepositoryTest @Autowired constructor(
    val groupRepository: OrganizationRepository,
    val testEntityManager: TestEntityManager
) {

    @Test
    fun `Group insertTest`() {
        val group = Organization("조직도")
        groupRepository.save(group)

        val findGroup = groupRepository.findByIdOrNull(group.id!!)

        findGroup!!.run {
            assertThat(organizationName).isEqualTo("조직도")
            assertThat(parentOrganization).isNull()
            assertThat(childrenOrganization.size).isEqualTo(0)
            assertThat(organizationEmployees.size).isEqualTo(0)
        }

        val findByGroupName = groupRepository.findByOrganizationName("조직도")
        assertThat(findGroup).isSameAs(findByGroupName)
    }

    @Test
    fun `그룹 계층구조 테스트`() {

        val cloudDevelopCenter = Organization("클라우드개발센터")
        groupRepository.save(cloudDevelopCenter)

        val commonDevelopLab = Organization("공통개발랩", parentOrganization = cloudDevelopCenter)
        groupRepository.save(commonDevelopLab)

        val cloudCommonDevelopLab = Organization("클라우드공통개발랩", parentOrganization = cloudDevelopCenter)
        groupRepository.save(cloudCommonDevelopLab)

        val cloudDataPlatformDevelopLab = Organization("클라우드데이터플랫폼개발랩", parentOrganization = cloudDevelopCenter)
        groupRepository.save(cloudDataPlatformDevelopLab)

        val cloudInfraDevelopLab = Organization("클라우드인프라개발랩", parentOrganization = cloudDevelopCenter)
        groupRepository.save(cloudInfraDevelopLab)

        val cloudPlatformDevelopLab = Organization("클라우드플랫폼개발랩", parentOrganization = cloudDevelopCenter)
        groupRepository.save(cloudPlatformDevelopLab)

        val messagingPlatFormDevelopTeam = Organization("메세징플랫폼개발팀", parentOrganization = cloudPlatformDevelopLab)
        groupRepository.save(messagingPlatFormDevelopTeam)

        val monitoringPlatformDevelopTeam = Organization("모니터링플랫폼개발팀", parentOrganization = cloudPlatformDevelopLab)
        groupRepository.save(monitoringPlatformDevelopTeam)

        val infraPlatformDevelopTeam = Organization("인프라플랫폼개발팀", parentOrganization = cloudPlatformDevelopLab)
        groupRepository.save(infraPlatformDevelopTeam)

        testEntityManager.clear()

        println("영속성컨텍스트를 초기화합니다.")

        val findCloudDevelopCenter = groupRepository.findByIdOrNull(cloudDevelopCenter.id)
        findCloudDevelopCenter!!.run {
            assertThat(organizationName).isEqualTo("클라우드개발센터")
            println("Lazy loading이라 추가적인 쿼리가 발생합니다.")
            assertThat(childrenOrganization.size).isEqualTo(5)

            childrenOrganization.forEach { organization ->
                println(organization.organizationName)
                assertThat(organization.parentOrganization).isSameAs(findCloudDevelopCenter)
                if (organization.organizationName == "클라우드플랫폼개발랩") {
                    println("하위 그룹들을 가져올때 lazy loading이기 때문에 한번더 쿼리가 실행됩니다.")
                    assertThat(organization.childrenOrganization.size).isEqualTo(3)
                }
            }
        }

        testEntityManager.clear()

        println("영속성컨텍스트를 다시한번 초기화합니다. 아래의 쿼리에서는 N+1발생하지 않습니다. 모든데이터를 한번에 가져왔기때문에")
        val rootGroup: Organization = groupRepository.findAll().single { group -> group.organizationName == "클라우드개발센터" }
        rootGroup.run {
            assertThat(organizationName).isEqualTo("클라우드개발센터")
            assertThat(childrenOrganization.size).isEqualTo(5)

            childrenOrganization.forEach { group ->
                println(group.organizationName)
                assertThat(group.parentOrganization).isSameAs(rootGroup)
                if (group.organizationName == "클라우드플랫폼개발랩") {
                    assertThat(group.childrenOrganization.size).isEqualTo(3)
                }
            }
        }
    }

    @Test
    fun `findGroupEmployee test - queryCheck`() {
        val group = Organization("조직도")
        testEntityManager.persist(group)

        val employee1 = Employee("jh1", position = Position.JUNIOR)
        val employee2 = Employee("jh2", position = Position.JUNIOR)
        val employee3 = Employee("jh3", position = Position.JUNIOR)
        val employee4 = Employee("jh4", position = Position.JUNIOR)

        testEntityManager.persist(employee1)
        testEntityManager.persist(employee2)
        testEntityManager.persist(employee3)
        testEntityManager.persist(employee4)

        val workPeriod = WorkPeriod(LocalDateTime.now(), null)

        val groupEmployee1 = OrganizationEmployee(group, employee1, workPeriod)
        val groupEmployee2 = OrganizationEmployee(group, employee2, workPeriod)
        val groupEmployee3 = OrganizationEmployee(group, employee3, workPeriod)
        val groupEmployee4 = OrganizationEmployee(group, employee4, workPeriod)

        testEntityManager.persist(groupEmployee1)
        testEntityManager.persist(groupEmployee2)
        testEntityManager.persist(groupEmployee3)
        testEntityManager.persist(groupEmployee4)

        testEntityManager.flush()
        testEntityManager.clear()

        val findGroup = groupRepository.findOrganizationWithEmployee(group.id!!)
        findGroup!!.run {
            assertThat(organizationEmployees.size).isEqualTo(4)
        }

    }

    @Test
    fun `findGroupEmployee test - nullCheck`() {
        assertThat(groupRepository.findOrganizationWithEmployee(1L)).isNull()

    }
}