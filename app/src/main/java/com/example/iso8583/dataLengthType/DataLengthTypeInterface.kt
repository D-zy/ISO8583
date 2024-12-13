package com.example.iso8583.dataLengthType

import com.example.iso8583.enums.Field

interface DataLengthTypeInterface {
    /**
     * 设置域-属性
     * 获取域的相关属性
     * @param field
     */
    fun setField(field: Field)

    /**
     * 长度占用几个字节
     * @return
     */
    fun getBytesNum(): Int

    /**
     * 获取域实际值长度（不算补0或补空格的长度）
     * @param filedBytes
     * @return
     */
    fun getFieldLength(filedBytes: ByteArray): Int

    /**
     * 获取域数据长度（占用几个字节）
     * @return
     */
    fun getFiledBytesNum(filedBytes: ByteArray): Int

    /**
     * 获取域真实长度
     * 用于某些奇数长度数据右补位的解析
     * @return
     */
    fun getFieldRealLength(): Int

    @Throws(Exception::class)
    fun encodeFieldLengthHexString(value: Any): String
}