package com.example.iso8583.bill

import com.example.iso8583.parse.AnalysisFieldMessagePackage
import com.example.iso8583.enums.Field
import com.example.iso8583.enums.PaymentParams
import com.example.iso8583.util.Constants
import com.example.iso8583.util.ConvertUtil
import com.example.iso8583.util.DcsUtils
import com.example.iso8583.util.ParseUtils
import com.example.iso8583.util.TransUtil
import com.example.iso8583.util.TlvUtils
import org.json.JSONObject
import java.util.UUID

/**
 * 签购单模板对象
 * 所有的域解析/子域解析取值排列，均在模板里进行
 *
 */
class RespData(isoMessage: AnalysisFieldMessagePackage?, bodyMap: HashMap<String, Any>) {

    @Column
    var mti = ""  //主账号

    @Column
    var processingCode = ""  //交易处理码

    @Column
    var cardNo = ""  //主账号

    @Column
    var amount = "0" //交易金额

    @Column
    var tip = "" //交易小费

    @Column
    var traceNo = "" //受卡方系统跟踪号

    @Column
    var hhmmss = ""

    @Column
    var mmdd = ""

    @Column
    var expiryDate = "" //卡片有效期

    @Column
    var entryMode = "" //服务点输入方式码

    @Column
    var track2Data = "" //2磁道

    @Column
    var refNo = "" //参考号

    @Column
    var authCode = "" //授权号

    @Column
    var responseCode = "" //应答码

    @Column
    var terminalId = "" //终端标识码

    @Column
    var merchantId = "" //受卡方标识码

    @Column
    var merchantName = "" //商户名称

    @Column
    var track1Data = "" //1磁道

    @Column
    var cvv2 = "" //

    @Column
    var settlementMode = "" //

    @Column
    var transCode = ""

    @Column
    var batchNo = "" //批次号

    @Column
    var wkTypeList = "" //无卡支持类型列表

    @Column
    var invoiceNo = ""//发票号

    @Column
    var cardPin = ""//pin

    @Column
    var cardPinLenght = ""//pin长度

    @Column
    var qrCodeValue = "" //无卡二维码值

    @Column
    var payOrderId: String? = "" //支付订单号

    @Column
    var exchangeRate: String? = ""

    @Column
    var rmbTotalAmount: String? = ""

    @Column
    var paymentId: String? = ""

    @Column
    var icData = "" //

    @Column
    var extOrderId = "" //商户订单号

    @Column
    var icRemark = "" //IC备注

    @Column
    var readCardMethod = ""  //读卡方式

    @Column
    var signature = ""

    @Column
    var field62 = ""

    @Column
    var typeDes = ""

    //打印对象id
    @Column
    var printId = UUID.randomUUID().toString()

    private var bodyMap: HashMap<String, Any>  //req数据源


    init {
        isoMessage.let {
            this.bodyMap = bodyMap
            //mti
            mti = getValue(bodyMap, Constants.FIELD_MTI)
            //主账号
            cardNo = getFieldData(it, Field.F2.fieldId, Constants.FIELD_2)
            //交易处理码
            processingCode = getFieldData(it, Field.F3.fieldId,Constants.FIELD_3)
            //交易金额
            amount = getValue(bodyMap, Constants.FIELD_4, "0")
            //交易小费
            tip = getValue(bodyMap, Constants.FIELD_54, "0")
            //受卡方系统跟踪号
            traceNo = getFieldData(it, Field.F11.fieldId, Constants.FIELD_11)
            //交易时间
            hhmmss = getFieldData(it, Field.F12.fieldId, "", DcsUtils.getHHmmss())
            //交易日期
            mmdd = getFieldData(it, Field.F13.fieldId, "", DcsUtils.getMMdd())
            //卡片有效期
            expiryDate = getFieldData(it, Field.F14.fieldId, Constants.FIELD_14)
            //服务点输入方式码
            entryMode = getFieldData(it, Field.F22.fieldId, Constants.FIELD_22)
            //2磁道
            track2Data = getFieldData(it, Field.F35.fieldId, Constants.FIELD_35)
            //参考号
            refNo = getFieldData(it, Field.F37.fieldId)
            //授权码
            authCode = getFieldData(it, Field.F38.fieldId)
            //应答码
            responseCode = getFieldData(it, Field.F39.fieldId, Constants.FIELD_39)
            //终端标识码
            terminalId = getFieldData(it, Field.F41.fieldId, Constants.FIELD_41)
            //受卡方标识码
            merchantId = getFieldData(it, Field.F42.fieldId, Constants.FIELD_42)
            //商户名称
            merchantName = getFieldData(it, Field.F43.fieldId)
            //1磁道
            track1Data = getFieldData(it, Field.F45.fieldId, Constants.FIELD_45)
            //48域:
            val f48 = getFieldData(it, Field.F48.fieldId)
            if (f48.isNotEmpty()) {
                val map48 = ParseUtils.parseField(ConvertUtil.hex2Byte(f48))
                if (map48.containsKey("05")) wkTypeList = map48["05"] as String //tag05 无卡支持类型
                if (map48.containsKey("03")) settlementMode = ConvertUtil.asciiToString(map48["03"])
            }
            if (settlementMode.isEmpty()) settlementMode = getValue(bodyMap, Constants.FIELD_48_3_SETTLEMENT_MODE)
            cvv2 = getValue(bodyMap, Constants.FIELD_48_1_CVV2) //tag01
            //芯片信息
            icData = getFieldData(it, Field.F55.fieldId, Constants.FIELD_55)
            //交易方式Code
            transCode = getFieldData(it, Field.F59.fieldId, Constants.FIELD_59)

            //61域：无卡二维码值 或者 原交易日期
            val f61 = getFieldData(it, Field.F61.fieldId)
            if (f61.length != 4) qrCodeValue = f61
            //批次号
            batchNo = getValue(bodyMap, Constants.FIELD_60_2_BATCH_NO)

            val field63 = getFieldData(it, Field.F63.fieldId)
            if (field63.isNotEmpty()) {
                //  482142 000000000048 323834323030303031303031323032313035323839303734383531393532
                val wkPayFlag = TransUtil.isWKPayFlag(transCode)
                if (wkPayFlag) {
//                    if (field63.length > 5) exchangeRate = field63.substring(0, 6) //汇率
//                    if (field63.length > 17) rmbTotalAmount = field63.substring(6, 18).replace("^[0]+".toRegex(), "") //人民币总金额
//                    if (field63.length > 18) paymentId = ConvertUtil.asciiToString(field63.substring(18)) //payment id
                    //333132303232303830383139303734313031303030354930303032383132383530
                    paymentId = ConvertUtil.asciiToString(field63).substring(2)
                }
            }
            //应用标签
            icRemark = getValue(bodyMap, Constants.SDK_IC_REMARK)

            if (icRemark.isNotEmpty()) {
                val track2 = TlvUtils.buildRemark(icRemark)["TRACK2"]
                if (!track2.isNullOrEmpty() && track2.contains("D")) {
                    track2Data = track2
                    val arr = track2.split("D").toTypedArray()
                    cardNo = arr[0]
                    expiryDate = arr[1].substring(0, 4)  //过期日期
                }
            }


            //模拟：附加测试值-------start-------
            //pin
            cardPinLenght = getValue(bodyMap, Constants.FIELD_26)
            cardPin = getValue(bodyMap, Constants.FIELD_52)

            //模拟：附加测试值-------end--------
            val paymentParams = bodyMap[Constants.SDK_PAY_TYPE] as PaymentParams
            if (paymentParams == PaymentParams.RSA_KEY_DOWNLOAD || paymentParams == PaymentParams.TMK_DOWNLOAD || paymentParams == PaymentParams.DOWNLOAD_PIN_KEY) {
                field62 = getFieldData(it, Field.F62.fieldId)
            } else if (paymentParams == PaymentParams.SINGON) {
                val f60 = getFieldData(it, Field.F60.fieldId)
                batchNo = ParseUtils.parseField60(f60)["02"] as String
            } else {
                //发票号
                invoiceNo = getValue(bodyMap, Constants.FIELD_62_INV_NO)
            }

            typeDes = paymentParams.typeDes
        }
    }


    fun toJsonString(): String {
        return vf(this, this.javaClass)
    }

    /**
     * 自动Json化
     */
    private fun <T> vf(o: Any, cls: Class<T>): String {
        val j = JSONObject()
        try {
            val fields = cls.declaredFields
            for (f in fields) {
                if (f.isAnnotationPresent(Column::class.java)) {
                    f.isAccessible = true
                    val jKey = f.name
                    val obj = if (null == f[o]) "" else f[o]?.toString()
                    j.put(jKey, obj)
                }
            }
            j.put(Constants.SDK_PRINT_ID, printId)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return j.toString()
    }

    /**
     * 解析域
     * params: 从bodyMap 取值
     * fixParams: 固定值
     */
    private fun getFieldData(isoMessage: AnalysisFieldMessagePackage?, fieldId: Int, mapParams: String = "", fixParams: String = ""): String {
        var newValue = bodyMap[mapParams]?.let { it as String } ?: ""
        if (fixParams.isNotEmpty()) newValue = fixParams
        val f = isoMessage?.getFieldData(fieldId)
        return if (f?.getValue() == null) newValue else f.getValue().toString().trim()
    }

    /**
     * 获取bodyMap的值
     */
    private fun getValue(bodyMap: HashMap<String, Any>, key: String, default: String = ""): String {
        return bodyMap[key]?.let { it as String } ?: default
    }

}