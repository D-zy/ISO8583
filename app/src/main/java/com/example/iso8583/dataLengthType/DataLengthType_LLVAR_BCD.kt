package com.example.iso8583.dataLengthType

import com.example.iso8583.dataEncodeType.DataEncodeTypeFactory.create
import com.example.iso8583.enums.Field
import com.example.iso8583.util.ConvertUtil

/**
 * 两位变长(BCD)
 */
class DataLengthType_LLVAR_BCD : DataLengthTypeInterface {
    private var field: Field? = null
    private var realLen = 0
    override fun setField(field: Field) {
        this.field = field
    }

    override fun getBytesNum(): Int {
        return 1
    }

    override fun getFieldLength(filedBytes: ByteArray): Int {
        val lengthBytes = ByteArray(getBytesNum())
        for (i in lengthBytes.indices) {
            lengthBytes[i] = filedBytes[i]
        }
        return ConvertUtil.byte2HexString(lengthBytes).toInt()
    }

    override fun getFieldRealLength(): Int {
        return realLen * getBytesNum()
    }

    override fun getFiledBytesNum(filedBytes: ByteArray): Int {
        val det = create(field!!.encodeType)
        realLen = getFieldLength(filedBytes)
        val length = realLen / det.getByteNum()
        return length + if (realLen % det.getByteNum() == 0) 0 else 1
    }

    @Throws(Exception::class)
    override fun encodeFieldLengthHexString(value: Any): String {
        //计算长度
//        int valueLength = fieldConfig.getDataEncodeType().encodeCalcFiledLength(value, fieldConfig);//数据长度
//        //必须放在此处，因为上面处理子域的情况，会重新对valueLength 赋值
//        String valueLengthString = fieldConfig.fillString(valueLength+"", getBytesNum()*2, FieldConfig.lengthDirectFileEnum, FieldConfig.lengthFillStringEnum);
//        String valueLengthHexString = TypeConvert.byte2HexString(TypeConvert.str2Bcd(valueLengthString, fieldConfig.getDirectionFillEnum(), fieldConfig.getFillStringEnum().getValue()));
//        return valueLengthHexString;
        return ConvertUtil.byte2HexString(ConvertUtil.str2Bcd(value.toString()))
    }
}