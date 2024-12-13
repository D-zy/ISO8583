package com.example.iso8583.dataEncodeType

import com.example.iso8583.util.ConvertUtil

class DataEncodeType_HEX : DataEncodeTypeInterface {
    override fun getByteNum(): Int {
        return 1
    }

    @Throws(Exception::class)
    override fun decode(fieldLengthByteArray: ByteArray, fieldValueByteArray: ByteArray): Any {
        return ConvertUtil.byte2HexString(fieldValueByteArray)
    }

    @Throws(Exception::class)
    override fun encodeToHexString(value: Any): String {
        return value.toString()
    }

    @Throws(Exception::class)
    override fun encodeCalcFiledLength(value: Any): Int {
        return value.toString().length / 2
    }
}