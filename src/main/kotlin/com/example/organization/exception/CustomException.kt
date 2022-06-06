package com.example.organization.exception

import com.example.organization.enums.ErrorCode

class CustomException(message :String) : RuntimeException(message){
    constructor(errorCode: ErrorCode): this(errorCode.message)
}
