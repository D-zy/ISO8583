package com.example.iso8583.parse

import com.example.iso8583.dataEncodeType.DataEncodeTypeFactory.create
import com.example.iso8583.dataLengthType.DataLengthTypeFactory.create
import com.example.iso8583.enums.BizCode
import com.example.iso8583.enums.Field
import com.example.iso8583.enums.FieldLengthType
import com.example.iso8583.util.*
import java.util.Collections.sort
import kotlin.experimental.or

/**
 * 域消息组包
 */
class FieldMessagePackage(private var messageType: String) {
    private var fieldDates = LinkedHashMap<Int, FieldData?>()
    private var headerString = ""
    private var bitMapHexString = ""

    /**
     * 初始化消息头
     */
    @Throws(Throwable::class)
    private fun initMessageHead() {
        fieldDates.clear()
        //设置tpdu
        fieldDates[-1] = FieldData(Field.F_TUDP, Constants.FLAG_8583_TPDU)
        //header
//        fieldDates.put(-2, new FieldData(null, Constants.FLAG_8583_HEADER));
        //计算header
        headerString = addHeader()
    }

    var bMessagePackage: ByteArray? = null
        private set

    /**
     * 添加数据包
     */
    fun addPackage(fieldData: FieldData?) {
        fieldData?.let {
            fieldDates[it.getField().fieldId] = fieldData
        }
    }

    /**
     * 移除数据包
     */
    fun removePackage(fieldId: Int) {
        fieldDates.remove(fieldId)
    }

    /**
     * 组消息包
     */
    @JvmOverloads
    @Throws(Throwable::class)
    fun makeMessagePackage(): ByteArray {
        //域排序
        fieldDates = sortFields(fieldDates)
        //消息hex
        val msgHex = StringBuilder()
        //添加header
        msgHex.append(headerString.substring(0, 10))
        /*LogUtils.p("messagePackage->headerHexStr= "+headerString);*/
        //添加消息类型头
        if (messageType.isEmpty()) {
            throw NullPointerException("messageType is null.")
        }
        msgHex.append(messageType)
        /*LogUtils.p("messagePackage->messageType= "+messageType);*/
        //添加位图信息
        val bitMapSet: Set<Int> = HashSet(fieldDates.keys)
        bitMapHexString = buildBitMapHexString(bitMapSet, Constants.BITMAP_BYTE_NUM_64)
        msgHex.append(bitMapHexString)
        /*LogUtils.p("messagePackage->bitMapHexString= "+bitMapHexString);*/
        //报文内容编码
        val bodyHexStr = bodyEncode()
        /*LogUtils.p("messagePackage->bodyHexStr= "+bodyHexStr);*/
        msgHex.append(bodyHexStr)
        /*LogUtils.p("messagePackage->msgHex= "+msgHex.toString());*/
        val iso = ConvertUtil.toHex(msgHex.toString().length / 2, 4) + msgHex.toString()
        LogUtils.e("报文(请求)-->$iso")
        for ((index, e) in fieldDates) {
            if (index == -1) {
                LogUtils.p("TPDUHexString = ${e?.getValue()}")
                LogUtils.p("messageTypeHexString = $messageType")
            } else LogUtils.p("报文(请求字段)-->${index}域:  ${e?.getValue()}")
        }
        /*LogUtils.p("messagePackage->msgHex= "+msgHex.toString());*/
        return ConvertUtil.hex2Byte(msgHex.toString()).also { bMessagePackage = it }
    }

    /**
     * 报文编码
     */
    @Throws(Throwable::class)
    private fun bodyEncode(): String {
        val ret = StringBuilder()
        val iTer: Iterator<Map.Entry<Int, FieldData?>> = fieldDates.entries.iterator()
        while (iTer.hasNext()) {
            val entry = iTer.next()
            val fieldId = entry.key
            val fieldData = entry.value
            //含子域的域，构建新域
            if (fieldData!!.hasChildField()) {
                fieldData.buildParentField(this)
            }
            if (fieldId < 0 || fieldData.getValue().toString().isEmpty()) {
                //域头占用<0的数字已经处理
                continue
            }
            val temp = encodeToHexString(fieldData)
            ret.append(temp)
            /*LogUtils.p("fieldId:"+fieldId+" , fieldIdValue:"+fieldData.getVal().toString()+" , fieldHexString: "+temp);*/
        }
        return ret.toString()

    }

    /**
     * 添加消息头
     */
    @Throws(Throwable::class)
    private fun addHeader(): String {
        val ret = StringBuilder()
        for ((_, fieldData) in fieldDates) {
            ret.append(encodeToHexString(fieldData))
        }
        /*LogUtils.p("header->ret= "+ret);*/
        return ret.toString()
    }

    /**
     * 域-编码计算
     */
    @Throws(Throwable::class)
    private fun encodeToHexString(fieldData: FieldData?): String {
        return try {
            val ret: String
            val dlt = create(fieldData!!.getField())
            val det = create(fieldData.getField().encodeType)
            //数据长度是否定长，FieldLengthType.FIXED.name()为定长
            val flt = FieldLengthType.FIXED.name == fieldData.getField().lengthType

            /*LogUtils.p("encodeToHexString->当前域是= "+fieldData.getField().getFieldId());*/if (flt) {
                val value = fillString(fieldData.getValue().toString(), fieldData.getField().direction, fieldData.getField().fillStr, fieldData.getField().dataLength.toInt(), true)
                /*LogUtils.p("encodeToHexString->val= "+val);*/
                ret = det.encodeToHexString(value)
            } else {
                //变长
                var value: String = fieldData.getValue().toString()
                /*LogUtils.p("encodeToHexString->变长val= "+val);*/
                val valL = det.encodeCalcFiledLength(fieldData.getValue().toString())
                /*LogUtils.p("encodeToHexString->valL= "+valL);*/
                val valLS = valL.toString().padStart(dlt.getBytesNum() * 2, '0')
                val hexL = dlt.encodeFieldLengthHexString(valLS)
                //转化内容
                value = fillString(value, fieldData.getField().direction, fieldData.getField().fillStr, valL, true)
                /*LogUtils.p("encodeToHexString->val= "+val);*/
                val hexV = det.encodeToHexString(value)
                /*LogUtils.p("encodeToHexString->变长valL= "+valL+" ,hexL= "+hexL+" ,hexV= "+hexV);*/ret = hexL + hexV
            }
            /*LogUtils.p("encodeToHexString->ret= "+ret);*/ret
        } catch (e: Throwable) {
            throw BizException(
                BizCode.SDK0004.code, BizCode.SDK0004.appendMsgDefault(fieldData!!.getField().fieldId.toString() + ":" + e.message)
            )
        }
    }

    /**
     * 排序
     */
    private fun sortFields(map: Map<Int, FieldData?>): LinkedHashMap<Int, FieldData?> {
        val filedDataList: List<Map.Entry<Int, FieldData?>> = ArrayList(map.entries)
        sort(filedDataList) { o1, o2 -> o1.key - o2.key }
        val newMap = LinkedHashMap<Int, FieldData?>()
        for ((key, value) in filedDataList) {
            newMap[key] = value
        }
        return newMap
    }

    /**
     * 构建bitMap
     *
     * @param set
     * @return
     */
    private fun buildBitMapHexString(set: Set<Int>, byteNum: Int): String {
        val binaryString = StringBuilder()
        val bytes = ByteArray(byteNum)
        var b: Byte = 0
        for (i in 1..bytes.size * 8) {
            if (set.contains(i)) {
                binaryString.append("1")
                b = b or (1 and 0xff shl 8 - if (i % 8 == 0) 8 else i % 8).toByte()
            } else {
                binaryString.append("0")
            }
            if (i % 8 == 0) {
                bytes[i / 8 - 1] = b
                b = 0
            }
        }
        return ConvertUtil.byte2HexString(bytes)
    }

    /**
     * 字符串补位
     *
     * @param value         目标字符串
     * @param directionType 补位类型，字符串左侧传L，右侧补位传R
     * @param fillStr       填充字符串内容
     * @param maxL          return（x）最大字符串长度
     * @param is2division   是否被2整除
     * @return
     */
    private fun fillString(value: String, directionType: String, fillStr: String, maxL: Int, is2division: Boolean): String {
        //是否被2整除，不能则继续补位
        var newValue = value
        var newMaxL = maxL
        if (is2division && newMaxL % 2 != 0) {
            ++newMaxL
        }
        if (newValue.isEmpty() || directionType.isEmpty() || fillStr.isEmpty() || (newMaxL - newValue.length) % fillStr.length != 0) {
            //数据补位异常
            /**LogUtils.e("数据补位异常,valueL=%s,directionType=%s,fillStrL=%s,maxL=%s", value.length(), directionType, fillStr.length(), maxL); */
            return newValue
        }
        val size = newMaxL - newValue.length
        val fsSize = size / fillStr.length
        var tempFillStr = ""
        for (i in 0 until fsSize) {
            tempFillStr += fillStr
        }

        if ("L" == directionType) { //左边补位
            newValue = tempFillStr + newValue
        } else if ("R" == directionType) {  //右边补位
            newValue += tempFillStr
        }
        return newValue
    }


    init {
        initMessageHead()
    }

}