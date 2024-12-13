package com.example.iso8583.dataLengthType

import com.example.iso8583.enums.Field
import com.example.iso8583.util.ConvertUtil

/**
 * 三位变长(ascii)
 */
class DataLengthType_LLLVAR_ASCII : DataLengthTypeInterface {
    private var field: Field? = null
    private var realLen = 0
    override fun setField(field: Field) {
        this.field = field
    }

    override fun getBytesNum(): Int {
        return 3
    }

    override fun getFieldLength(filedBytes: ByteArray): Int {
        val lengthBytes = ByteArray(getBytesNum())
        for (i in lengthBytes.indices) {
            lengthBytes[i] = filedBytes[i]
        }
        return Integer.valueOf(String(lengthBytes))
    }

    override fun getFieldRealLength(): Int {
        return realLen
    }

    override fun getFiledBytesNum(filedBytes: ByteArray): Int {
        return getFieldLength(filedBytes).also { realLen = it }
    }

    @Throws(Exception::class)
    override fun encodeFieldLengthHexString(value: Any): String {
        //计算长度
//        FieldConfig fieldConfig;
//        int valueLength = fieldConfig.getDataEncodeType().encodeCalcFiledLength(value, fieldConfig);//数据长度
//        //必须放在此处，因为上面处理子域的情况，会重新对valueLength 赋值
//        String valueLengthString = fieldConfig.fillString(valueLength+"", getBytesNum(), FieldConfig.lengthDirectFileEnum, FieldConfig.lengthFillStringEnum);
//        String valueLengthHexString = TypeConvert.byte2HexString(valueLengthString.getBytes());
//        return valueLengthHexString;
        return ConvertUtil.byte2HexString(value.toString().toByteArray())
    }
}