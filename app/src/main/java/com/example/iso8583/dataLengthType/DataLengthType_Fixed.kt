package com.example.iso8583.dataLengthType

import com.example.iso8583.dataEncodeType.DataEncodeTypeFactory.create
import com.example.iso8583.enums.Field

/**
 * 定长
 */
class DataLengthType_Fixed : DataLengthTypeInterface {
    private var field: Field? = null
    private var realLen = 0
    override fun setField(field: Field) {
        this.field = field
    }

    override fun getBytesNum(): Int {
        return 0
    }

    override fun getFieldLength(filedBytes: ByteArray): Int {
        return field!!.dataLength.toInt()
    }

    override fun getFieldRealLength(): Int {
        return realLen
    }

    override fun getFiledBytesNum(filedBytes: ByteArray): Int {
        val det = create(field!!.encodeType)
        realLen = field!!.dataLength.toInt()
        val length = realLen / det.getByteNum()
        return length + if (field!!.dataLength.toInt() % det.getByteNum() == 0) 0 else 1
    }

    @Throws(Exception::class)
    override fun encodeFieldLengthHexString(value: Any): String {
        return ""
    }
}