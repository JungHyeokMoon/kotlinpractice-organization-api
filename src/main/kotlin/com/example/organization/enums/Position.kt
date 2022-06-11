package com.example.organization.enums

enum class Position(val positionName: String, val compareNumber: Int) {
    EXECUTIVE_DIRECTOR("총괄이사", 0),
    DIRECTOR("이사", 1),
    SENIOR_READ("수석", 2),
    LEAD("책임", 3),
    SENIOR("선임", 4),
    JUNIOR("전임", 5),
    STAFF("사원", 6)
}