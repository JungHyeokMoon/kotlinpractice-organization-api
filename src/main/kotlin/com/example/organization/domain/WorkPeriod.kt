package com.example.organization.domain

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
class WorkPeriod(@Column val startDate: LocalDateTime, var endDate: LocalDateTime?)