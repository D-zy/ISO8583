package com.example.iso8583.parse

import com.example.iso8583.enums.Field
import com.example.iso8583.util.ConvertUtil

/**
 * 域数据源
 */
class FieldData(field: Field, value: String) {

    private var value = ""
    private var field: Field
    private val L_T_HEX = 1
    private val L_T_NORMAL = 0

    /**
     * 组包-父域下子域的存放，用于生成父域内容使用
     */
    private var childFieldKVMap: LinkedHashMap<String, Any>? = null

    /**
     * 解包-父域下子域的存放，用于父域下访问子域内容使用
     */
    private var fieldChildDataMap: LinkedHashMap<Any, FieldChildData>? = null

    /**
     * 获取域-数值
     *
     * @return
     */
    fun getField(): Field {
        return field
    }

    /**
     * 获取域-数值
     *
     * @return
     */
    fun getValue(): Any {
        return value
    }

    /**
     * 解包-添加一个子域对象
     *
     * @param key
     * @param f
     */
    fun addFieldChildData(key: Any, f: FieldChildData) {
        fieldChildDataMap?.let { it[key] = f } ?: run { fieldChildDataMap = LinkedHashMap() }
    }

    /**
     * 解包-获取某个域其中的一个子域对象
     *
     * @param key
     * @return
     */
    fun getFieldChildData(key: Any): FieldChildData? {
        return fieldChildDataMap?.let { it[key] }

    }

    /**
     * 组包-添加一个子域对象
     * 添加子域健值对数据，长度类型为子域字符串长度
     * 当存在健值对的时候，该域数据为 this.val + buildField
     * 由于所有父域含子域，子域的编码类型及长度类型不确定（如多个60域的01子域可能一个按字节，一个按其他等），处理麻烦，即通过该种方式交给具体需求方处理
     *
     * @param key
     * @param v
     * @return
     */
    @Throws(Throwable::class)
    fun addChildFieldKV(key: String, v: String): FieldData {
        return addChildField(key, v, L_T_NORMAL)
    }

    /**
     * 组包-添加一个子域对象
     * 添加子域健值对数据，长度类型为Hex
     *
     * @param key
     * @param v
     * @return
     */
    @Throws(Throwable::class)
    fun addChildFieldHexKV(key: String, v: String): FieldData {
        return addChildField(key, v, L_T_HEX)
    }

    /**
     * 添加子域健值对数据
     *
     * @param key
     * @param v
     * @param lengthType
     * @return
     */
    private fun addChildField(key: String, v: String, lengthType: Int): FieldData {
        if (null == childFieldKVMap) childFieldKVMap = LinkedHashMap()
        childFieldKVMap!![key] = "$v|$lengthType"
        return this
    }

    /**
     * 是否含子域数据
     *
     * @return
     */
    fun hasChildField(): Boolean {
        return childFieldKVMap != null && childFieldKVMap!!.size > 0
    }

    /**
     * 构建父域
     * 防止日志打印buildParentField()时this.val被重新赋值
     *
     * @param o
     * @return
     * @throws Throwable
     */
    @Throws(Throwable::class)
    fun buildParentField(o: Any?): String {
        return if (o is FieldMessagePackage) {
            buildParentField().also { value = it }
        } else buildParentField()
    }

    /**
     * 构建父域
     *
     * @return
     * @throws Throwable
     */
    @Throws(Throwable::class)
    fun buildParentField(): String {
        if (!hasChildField()) {
            return ""
        }
        val sb = StringBuilder()
        for (key in childFieldKVMap!!.keys) {
            val vals = childFieldKVMap!![key].toString().split("|").toTypedArray()
            val valB = vals[0].toByteArray(charset("GBK"))
            val lengthType = vals[1].toInt()
            val vL0 = valB.size
            var vL1: String? = ""
            if (L_T_NORMAL == lengthType) {
                vL1 = vL0.toString()
            } else if (L_T_HEX == lengthType) {
                vL1 = Integer.toHexString(vL0)
            }
            val lHex = vL1?.padStart(2, '0')
            var vHex = if (value.isNotEmpty()) String(valB) else ConvertUtil.byte2HexString(valB)
            if (vHex == null) vHex = "" //特殊处理 sn
            if (!key.contains("key")) {
                sb.append(key)
            }
            sb.append(lHex)
            sb.append(vHex)
        }
        return fieldTlv(sb.toString())
    }

    /**
     * 域-tlv格式重组
     *
     * @param fieldValue
     * @return
     */
    private fun fieldTlv(fieldValue: String): String {
        var fieldVal = fieldValue
        if (value.isNotEmpty() && hasChildField() && "null" != value) {
            val tlvSb = StringBuilder()
            tlvSb.append(value)
            tlvSb.append(fieldVal.length.toString().padStart(3, '0'))
            tlvSb.append(fieldVal)
            fieldVal = tlvSb.toString()
        }
        return fieldVal
    }

    override fun toString(): String {
        return "FieldData{" +
                "val='" + value + '\'' +
                '}'
    }

    init {
        this.value = value.ifEmpty { "" }
        this.field = field
    }
}