package com.example.organization.enums

enum class ErrorCode(val message: String) {
    ORGANIZATION_NOT_EXIST_MESSAGE("조직이 존재하지 않습니다."),
    PARENT_ORGANIZATION_NOT_EXIST_MESSAGE("상위조직이 존재하지 않습니다."),
    ORGANIZATION_HAS_CHILDREN_ORGANIZATION("하위조직이 존재합니다. 조직을 삭제하려면, 하위 조직들의 상위조직을 변경하세요."),
    WANT_TO_DELETE_ORGANIZATION_IS_ROOT("삭제하려는 조직이, 최상위 조직입니다."),
    ORGANIZATION_NAME_AlREADY_EXIST("같은이름을 가진 조직이 존재합니다.")
}