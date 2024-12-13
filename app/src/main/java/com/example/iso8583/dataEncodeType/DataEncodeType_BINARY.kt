package com.example.iso8583.dataEncodeType

import com.example.iso8583.util.ConvertUtil

class DataEncodeType_BINARY : DataEncodeTypeInterface {
    override fun getByteNum(): Int {
        return 1
    }

    @Throws(Exception::class)
    override fun decode(fieldLengthByteArray: ByteArray, fieldValueByteArray: ByteArray): Any {
        //兼容下载主密钥(byte)与签到(hex)返回的62域格式不一致问题
        val camouflageStr = ConvertUtil.byte2HexString(fieldValueByteArray)
        val isHex = ConvertUtil.isHexString(camouflageStr)
        return if (isHex) {
            camouflageStr
        } else String(fieldValueByteArray)
    }

    @Throws(Exception::class)
    override fun encodeToHexString(value: Any): String {
        //Binary 类型不手动补长
        val bytes: ByteArray? = if (value is String) {
            ConvertUtil.hex2Byte(value.toString() + "")
        } else {
            value as ByteArray
        }
        //        if(fieldConfig.isFixedLength() && bytes.length!=fieldConfig.getDataLength()){
//            throw new RuntimeException("length is invalid.");
//        }
        return ConvertUtil.byte2HexString(bytes)
    }

    @Throws(Exception::class)
    override fun encodeCalcFiledLength(value: Any): Int {
        val bytes: ByteArray? = if (value is String) {
            ConvertUtil.hex2Byte(value.toString() + "")
        } else {
            value as ByteArray
        }
        return bytes!!.size
    }
}