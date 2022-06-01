package com.example.group.enums

enum class ErrorCode(val message: String) {
    GROUP_NOT_EXIST_MESSAGE("그룹이 존재하지 않습니다."),
    PARENT_GROUP_NOT_EXIST_MESSAGE("상위그룹이 존재하지 않습니다."),
    GROUP_HAS_CHILDREN_GROUP("하위그룹이 존재합니다. 그룹을 삭제하려면, 하위 그룹들의 상위그룹을 변경하세요."),
    WANT_TO_DELETE_GROUP_IS_ROOT("삭제하려는 그룹이, 최상위 그룹입니다."),
}