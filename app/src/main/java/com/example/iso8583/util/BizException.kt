package com.example.iso8583.util

import com.example.iso8583.enums.BizCode

/**
 * 业务异常
 */
class BizException : Throwable {

    var code: String = ""

    var msg: String = ""

    constructor(bizCode: BizCode) : this(bizCode.code, bizCode.msg)

    constructor(code: String, msg: String) {
        this.code = code
        this.msg = msg
    }

}