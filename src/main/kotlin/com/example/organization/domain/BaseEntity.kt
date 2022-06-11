package com.example.organization.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {

    @CreatedDate
    var createAt: LocalDateTime = LocalDateTime.MIN
        protected set

    @LastModifiedDate
    var updateAt: LocalDateTime = LocalDateTime.MIN
        protected set

}