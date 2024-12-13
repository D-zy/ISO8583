package com.example.iso8583.dataEncodeType

import com.example.iso8583.util.ConvertUtil

class DataEncodeType_ASCII : DataEncodeTypeInterface {
    override fun getByteNum(): Int {
        return 1
    }

    @Throws(Exception::class)
    override fun decode(fieldLengthByteArray: ByteArray, fieldValueByteArray: ByteArray): Any {
        return String(fieldValueByteArray, charset("GBK"))
    }

    @Throws(Exception::class)
    override fun encodeToHexString(value: Any): String {
        return ConvertUtil.byte2HexString(value.toString().toByteArray(charset("GBK")))
    }

    @Throws(Exception::class)
    override fun encodeCalcFiledLength(value: Any): Int {
        return value.toString().toByteArray(charset("GBK")).size
    }
}