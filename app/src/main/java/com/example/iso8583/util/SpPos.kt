package com.example.iso8583.util

/**
 * pos存储信息
 */

object SpPos {

    var batchNo: String
        get() = MmvkUtil.decodeString("batchNo", "000001")
        set(value) = MmvkUtil.encode("batchNo", value)

    var traceNo: String
        get() = MmvkUtil.decodeString("traceNo", "000001")
        set(value) = MmvkUtil.encode("traceNo", value)

    var invoiceNo: String
        get() = MmvkUtil.decodeString("invoiceNo", "000001")
        set(value) = MmvkUtil.encode("invoiceNo", value)

    var settlementMode: String
        get() = MmvkUtil.decodeString("settlementMode")
        set(value) = MmvkUtil.encode("settlementMode", value)

    var pinPublicKey: String
        get() = MmvkUtil.decodeString("pinPublicKey")
        set(value) = MmvkUtil.encode("pinPublicKey", value)

    var signTag: Boolean
        get() = MmvkUtil.decodeBoolean("signTag",false)
        set(value) = MmvkUtil.encode("signTag", value)

}
