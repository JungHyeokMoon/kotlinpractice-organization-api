package com.example.group.service

import com.example.group.domain.Group
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // softDelete의 변경감지를 테스트하기 위해서, JPA를 연동했고, 임베디드 데이터베이스를 사용하기 위한 어노테이션
internal class GroupServiceSpringBootTest {

    @Autowired
    private lateinit var groupService: GroupService

    @Autowired
    private lateinit var entityManager: EntityManager

    @Test
    @Transactional
    fun `softDelete Test - groupService가 정상적으로 동작후, 변경감지로 인해, 영속성컨텍스트가 초기화 후, 다시 조회시에 inUse property가 변경됩니다`(){
        val group = Group("softDelete Group Test", true, null)
        entityManager.persist(group)

        group.id?.let { groupService.softDeleteGroup(it) }

        entityManager.flush()
        entityManager.clear()

        val findGroup = entityManager.find(Group::class.java, group.id)
        assertThat(findGroup.inUse).isEqualTo(false)
    }
}