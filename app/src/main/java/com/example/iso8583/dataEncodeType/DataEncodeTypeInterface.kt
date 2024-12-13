package com.example.iso8583.dataEncodeType

interface DataEncodeTypeInterface {
    fun getByteNum(): Int

    @Throws(Exception::class)
    fun decode(fieldLengthByteArray: ByteArray, fieldValueByteArray: ByteArray): Any

    @Throws(Exception::class)
    fun encodeToHexString(value: Any): String

    @Throws(Exception::class)
    fun encodeCalcFiledLength(value: Any): Int
}