package com.example.iso8583.dataEncodeType

import com.example.iso8583.util.ConvertUtil

class DataEncodeType_BCD : DataEncodeTypeInterface {
    override fun getByteNum(): Int {
        return 2
    }

    @Throws(Exception::class)
    override fun decode(fieldLengthByteArray: ByteArray, fieldValueByteArray: ByteArray): Any {
        return ConvertUtil.bcd2Str(fieldValueByteArray)
    }

    @Throws(Exception::class)
    override fun encodeToHexString(value: Any): String {
        return ConvertUtil.byte2HexString(ConvertUtil.str2Bcd(value.toString()))
    }

    @Throws(Exception::class)
    override fun encodeCalcFiledLength(value: Any): Int {
        return value.toString().length
    }
}