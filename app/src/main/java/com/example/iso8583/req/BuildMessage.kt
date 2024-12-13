package com.example.iso8583.req

import com.example.iso8583.enums.BizCode
import com.example.iso8583.enums.Field
import com.example.iso8583.enums.PaymentParams
import com.example.iso8583.parse.FieldData
import com.example.iso8583.parse.FieldMessagePackage
import com.example.iso8583.util.*

/**
 * 构建域Message
 * 便于代码阅读，减少代码冗余使用
 *
 *
 */
class BuildMessage {

    /**
     * 数据源
     */
    private lateinit var bodyMap: HashMap<String, Any>

    /**
     * 业务处理
     */
    private lateinit var paymentParams: PaymentParams

    private lateinit var f: FieldMessagePackage

    @Throws(Throwable::class)
    fun build(bodyMap: HashMap<String, Any>, paymentParams: PaymentParams): ByteArray {
        this.bodyMap = bodyMap
        this.paymentParams = paymentParams
        f = FieldMessagePackage(getValue(Constants.FIELD_MTI))
        val fields = paymentParams.fields
        //解析域
        for (field in fields) {
            fieldsParse(field)
        }

        //返回请求的8583报文
        return f.makeMessagePackage()
    }

    /**
    M 字段必须存在
    ME 如果请求中存在字段，则必须在响应中回显相同的值
    O 字段在请求中是可选的。 如果存在，系统将采取某些行动
    在上面。
    C01 有条件的 – 手动输入必须输入帐号
    C02 有条件的 – 手动输入密钥必须有到期日期
    C03 有条件的 - 磁条（滑动）或 EMV 必须使用磁道 1 或 2
    交易（插入）。
    C04 有条件的——如果芯片/EMV 交易是强制性的
    C06 Conditional – 手动输入的必填项，否则该字段从磁道
     */
    @Throws(Throwable::class)
    private fun fieldsParse(field: String) {
        try {
            if (Field.F2.fieldId2String == field) {  // C01–手动输入卡号时必填项
                parse(Field.F2, Constants.FIELD_2)
            } else if (Field.F3.fieldId2String == field) { // 冲正时，跟原交易3域一样
                parse(Field.F3, Constants.FIELD_3)
            } else if (Field.F4.fieldId2String == field) {
                parse(Field.F4, Constants.FIELD_4)
            } else if (Field.F11.fieldId2String == field) {
                parse(Field.F11, Constants.FIELD_11)
            } else if (Field.F12.fieldId2String == field) {
                parse(Field.F12, Constants.FIELD_12)
            } else if (Field.F13.fieldId2String == field) {
                parse(Field.F13, Constants.FIELD_13)
            } else if (Field.F14.fieldId2String == field) {
                parse(Field.F14, Constants.FIELD_14)
            } else if (Field.F22.fieldId2String == field) {
                parse(Field.F22, Constants.FIELD_22)
            } else if (Field.F23.fieldId2String == field) { // C04–芯片/EMV交易为强制性
                parse(Field.F23, Constants.FIELD_23)
            } else if (Field.F24.fieldId2String == field) { //网络识别码
                parse(Field.F24, Constants.FIELD_24)
            } else if (Field.F25.fieldId2String == field) {
                parse(Field.F25, Constants.FIELD_25)
            } else if (Field.F26.fieldId2String == field) { //终端机器最大输入密码长度 2位(04-12)
                parse(Field.F26, Constants.FIELD_26)
            } else if (Field.F35.fieldId2String == field) { // 2磁道
                parse(Field.F35, Constants.FIELD_35)
            } else if (Field.F36.fieldId2String == field) { // 3磁道
                parse(Field.F36, Constants.FIELD_36)
            } else if (Field.F37.fieldId2String == field) { // 参考号
                parse(Field.F37, Constants.FIELD_37)
            } else if (Field.F38.fieldId2String == field) { // 授权码
                parse(Field.F38, Constants.FIELD_38)
            } else if (Field.F39.fieldId2String == field) { // 交易状态码
                parse(Field.F39, Constants.FIELD_39)
            } else if (Field.F41.fieldId2String == field) { // 终端号
                parse(Field.F41, Constants.FIELD_41)
            } else if (Field.F42.fieldId2String == field) { // 商户号
                parse(Field.F42, Constants.FIELD_42)
            } else if (Field.F45.fieldId2String == field) { // 1磁道
                parse(Field.F45, Constants.FIELD_45)
            } else if (Field.F48.fieldId2String == field) { // 自定义域
                parseField_48()
            } else if (Field.F49.fieldId2String == field) { // 汇币类型
                parse(Field.F49, Constants.FIELD_49)
            } else if (Field.F52.fieldId2String == field) { // 交易密码
                parse(Field.F52, Constants.FIELD_52)
            } else if (Field.F54.fieldId2String == field) { // 交易小费
                parse(Field.F54, Constants.FIELD_54)
            } else if (Field.F55.fieldId2String == field) { // ic信息  C04–芯片 或 EMV交易为强制性
                parse(Field.F55, Constants.FIELD_55)
            } else if (Field.F59.fieldId2String == field) { // 无卡 交易信息
                parse(Field.F59, Constants.FIELD_59)
            } else if (Field.F60.fieldId2String == field) { // 批次号
                parseField_60()
            } else if (Field.F61.fieldId2String == field) { // 原始交易信息
                parseField_61()
            } else if (Field.F62.fieldId2String == field) { // 发票 or pinkey
                parseField_62()
            } else if (Field.F63.fieldId2String == field) {
                parse(Field.F63, Constants.FIELD_63)
            } else {
                LogUtils.d("No exist this filedId: %s ,Please add this filedId.", field)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BizException(BizCode.SDK0004.code, BizCode.SDK0004.appendMsgDefault(field + ":" + e.message))
        }
    }

    /**
     * 创建FieldData
     *
     * @param f
     * @param value
     * @return
     */
    private fun buildFieldData(f: Field, value: String?): FieldData? {
        return if (!value.isNullOrEmpty()) FieldData(f, value) else null
    }

    /**
     * 获取与验证数据对象
     *
     * @param key
     * @param replaceStr
     * @param defaultStr
     */
    private fun getValue(key: String, replaceStr: String = "", defaultStr: String = ""): String {
        val obj = bodyMap[key]
        return if (null == obj) {
            defaultStr.ifEmpty { "" }
        } else if (replaceStr.isNotEmpty()) {
            replaceStr
        } else {
            obj.toString()
        }
    }

    /**
     * F48 自定义域
     */
    @Throws(Throwable::class)
    private fun parseField_48() {
        val field48 = FieldData(Field.F48, "")

        //银行卡 验证码
        val cvv2Value = getValue(Constants.FIELD_48_1_CVV2)
        if (cvv2Value.isNotEmpty()) field48.addChildFieldHexKV("01", cvv2Value)

        //原交易金额
        val oriAmount = getValue(Constants.FIELD_48_2_ORI_AMT)
        if (oriAmount.isNotEmpty()) field48.addChildFieldHexKV("02", oriAmount)

        //结算模式： 系统结算:SYSTEM    终端结算:TERMINAL
        val settlementMode = getValue(Constants.FIELD_48_3_SETTLEMENT_MODE)
        if (settlementMode.isNotEmpty()) field48.addChildFieldHexKV("03", settlementMode)

        if (field48.hasChildField()) f.addPackage(field48)

    }

    /**
     * F60 自定义域
     *
     */
    private fun parseField_60() {
        val field60 = FieldData(Field.F60, "")

        //sn号
        val sn = getValue(Constants.FIELD_60_1_SN)
        field60.addChildFieldKV("key1", sn)

        //批次号
        val batchNo = getValue(Constants.FIELD_60_2_BATCH_NO)
        if (batchNo.isNotEmpty()) field60.addChildFieldKV("key2", batchNo)

        //原交易MTI
        val oriMTI = getValue(Constants.FIELD_60_3_ORI_MTI)
        if (oriMTI.isNotEmpty()) field60.addChildFieldKV("key3", oriMTI)

        //原交易流水
        val oriTraceNo = getValue(Constants.FIELD_60_4_ORI_TRACE_NO)
        if (oriTraceNo.isNotEmpty()) field60.addChildFieldKV("key4", oriTraceNo)

        if (field60.hasChildField()) f.addPackage(field60)
    }

    /**
     * F61 原始交易信息 | 二维码值
     */
    @Throws(Throwable::class)
    private fun parseField_61() {
        if (getValue(Constants.FIELD_61_ORI_DATE).isNotEmpty()) { //原交易日期
            parse(Field.F61, Constants.FIELD_61_ORI_DATE)
        } else if (getValue(Constants.FIELD_61_QR_CODE).isNotEmpty()) { //用户被扫  二维码值
            parse(Field.F61, Constants.FIELD_61_QR_CODE)
        }
    }

    /**
     * F62 发票 | PinKey
     */
    @Throws(Throwable::class)
    private fun parseField_62() {
        if (getValue(Constants.FIELD_62_INV_NO).isNotEmpty()) { //发票
            parse(Field.F62, Constants.FIELD_62_INV_NO)
        } else if (getValue(Constants.FIELD_62).isNotEmpty()) { //PinKey
            parse(Field.F62, Constants.FIELD_62)
        }
    }

    /**
     * 若值不为空，则上传
     */
    private fun parse(field: Field, key: String, defaultStr: String = "") {
        val value = getValue(key)
        if (value.isNotEmpty()) {
            f.addPackage(buildFieldData(field, value))
        } else if (defaultStr.isNotEmpty()) {
            f.addPackage(buildFieldData(field, defaultStr))
        }
    }

}