package com.example.iso8583.parse

import com.example.iso8583.dataEncodeType.DataEncodeTypeFactory
import com.example.iso8583.dataLengthType.DataLengthTypeFactory
import com.example.iso8583.enums.Field
import com.example.iso8583.enums.FieldDirection
import com.example.iso8583.util.ConvertUtil
import com.example.iso8583.util.LogUtils


/**
 * 域-解码消息包
 */
class AnalysisFieldMessagePackage {

    private val fieldDates = LinkedHashMap<Int, FieldData>()

    /**
     * @param messageBytes
     * @throws Throwable
     */
    @Throws(Throwable::class)
    fun deCode(messageBytes: ByteArray) {
        LogUtils.e("报文(返回)-->" + ConvertUtil.byte2HexString(messageBytes))

        fieldDates.clear()
        //处理包长
        var starPos = 0
        val TPDUBytesArray = ByteArray(5)
        System.arraycopy(messageBytes, starPos, TPDUBytesArray, 0, TPDUBytesArray.size)
        val TPDUHexString = ConvertUtil.byte2HexString(TPDUBytesArray)
        starPos += TPDUBytesArray.size
        LogUtils.p("TPDUHexString = $TPDUHexString")

        //mab报文;
        val mabWithMacByteArray = ByteArray(messageBytes.size - starPos)
        System.arraycopy(messageBytes, starPos, mabWithMacByteArray, 0, mabWithMacByteArray.size)

        //处理消息类型
        val messageTypeByteArray = ByteArray(2)
        System.arraycopy(messageBytes, starPos, messageTypeByteArray, 0, messageTypeByteArray.size)
        val messageTypeHexString = ConvertUtil.byte2HexString(messageTypeByteArray)
        starPos += messageTypeByteArray.size
        LogUtils.p("messageTypeHexString = $messageTypeHexString")

        //解析bitmap
        val bitMapByteArray = ByteArray(8)
        System.arraycopy(messageBytes, starPos, bitMapByteArray, 0, bitMapByteArray.size)
        val bitMapBinaryString = ConvertUtil.byte2BinaryString(bitMapByteArray)
        starPos += bitMapByteArray.size

        //主域解析
        var i = 1
        for (c in bitMapBinaryString.toCharArray()) {
            if (c == '1' && i != 1) {
                /*LogUtils.p("当前 域："+i);*/
                val areaByteArray = ByteArray(messageBytes.size - starPos)
                System.arraycopy(messageBytes, starPos, areaByteArray, 0, areaByteArray.size)
                /*LogUtils.p("剩下的数据->"+ConvertUtil.byte2HexString(areaByteArray));*/
                val field = Field.selectFieldForId(i.toString())
                if (null == field) {
                    i++
                    continue
                }
                val dlt = DataLengthTypeFactory.create(field)
                val det = DataEncodeTypeFactory.create(field.encodeType)
                val lenL = dlt.getBytesNum()
                val valL = dlt.getFiledBytesNum(areaByteArray)
                val realLen = dlt.getFieldRealLength()
                /*LogUtils.p("当前 域-"+i+"  长度占 "+lenL+" 字节, "+"数值占 "+valL+" 字节");*/
                val fieldLengthByteArray = ByteArray(lenL + valL)
                try {
                    if (lenL + valL > 0) {
                        System.arraycopy(areaByteArray, 0, fieldLengthByteArray, 0, fieldLengthByteArray.size)
                        val fieldValueByteArray = ByteArray(valL)
                        System.arraycopy(areaByteArray, lenL, fieldValueByteArray, 0, fieldValueByteArray.size)
                        val o = det.decode(fieldLengthByteArray, fieldValueByteArray)
                        /*LogUtils.p("o解密的数据如下->" + o);*/
                        var data = o.toString().trim { it <= ' ' }
                        data = if (realLen != data.length && realLen <= data.length) {
                            if (field.direction == FieldDirection.L.name) data.substring(data.length - realLen) else data.substring(0, realLen)
                        } else data
                        fieldDates[i] = FieldData(field, data)
                        LogUtils.p("报文(返回字段)-->${i}域:  ${fieldDates[i]?.getValue().toString()}")

                        /*LogUtils.p("解密的数据如下->" + fieldDates.get(i).getVal());*/
                        starPos += lenL + valL
                    }
                } catch (ignored: Exception) {
                }
            }
            i++
        }

        LogUtils.p("field analysis end.")
    }

    /*响应code*/
    fun returnCode(): String {
        val f39 = getFieldData(Field.F39.fieldId)
        return f39?.getValue()?.toString() ?: "-1"
    }

    /*响应msg*/
    fun returnMessage(): String {
        val f56 = getFieldData(Field.F56.fieldId)
        if (null == f56) {
            return if (returnCode() == "00") "Success" else "Fail"
        } else if (f56.getValue().toString().isEmpty()) {
            return if (returnCode() == "00") "Success" else "Fail"
        }
        return f56.getValue().toString()
    }

    fun getFieldData(fieldId: Int): FieldData? {
        return fieldDates[fieldId]
    }

}