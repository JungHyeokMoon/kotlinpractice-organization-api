package com.example.group.exception

import com.example.group.enums.ErrorCode

class CustomException(message :String) : RuntimeException(message){
    constructor(errorCode: ErrorCode): this(errorCode.message)
}
