package com.example.iso8583.util

import com.example.iso8583.bean.WkTypeListBean
import com.xuexiang.xui.widget.textview.LoggerTextView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object DcsUtils {

    lateinit var logger: LoggerTextView

    /*12位长度的随机金额*/
    fun get12Amt(): String {
        return Random.nextInt(100, 999).toString().padStart(12,'0')
    }

    /*3位长度的随机金额*/
    fun getRandomAmt(): String {
        return Random.nextInt(100, 999).toString()
    }

    /*随机数字*/
    fun getRandomNum(length: Int = 6): String {
        val str = StringBuilder()
        for (i in 1..length) {
            str.append(Random.nextInt(0, 9))
        }
        return str.toString()
    }

    //随机获取32位的16进制的数
    fun getHex32(): String {
        var s = ""
        for (i in 0 until 32) s += "1234567890ABCDEF".random()
        return s
    }

    /**
     * 获取6位交易流水号
     * 最大不能超过6位，即 traceNo < 999999；否则需要重新签到。
     */
    fun getTransNo(isUpdate: Boolean = true): String {
        val traceNo = SpPos.traceNo
        return if (isUpdate) {
            var num = traceNo.toInt()
            if (num >= 999999) num = 0
            val newTraceNo = num.plus(1).toString().padStart(6, '0')
            SpPos.traceNo = newTraceNo
            newTraceNo
        } else traceNo
    }

    /**
     * 获取6位发票号
     * 最大不能超过6位，即 traceNo < 999999；否则需要重新签到。
     * isSave:true -> 更新并保存
     */
    fun getInvoiceNo(isUpdate: Boolean = true, isSave: Boolean = true): String {
        val invoiceNo = SpPos.invoiceNo
        return if (isUpdate) {
            var num = invoiceNo.toInt()
            if (num >= 999999) num = 0
            val newInvoiceNo = num.plus(1).toString().padStart(6, '0')
            if (isSave) SpPos.invoiceNo = newInvoiceNo
            newInvoiceNo
        } else invoiceNo
    }

    /*将当前时间格式化*/
    fun format(pattern: String): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val formatted = current.format(formatter)
        println("当前日期时间: $formatted")
        return formatted
    }

    fun getHHmmss(): String {
        return format("HHmmss")
    }

    fun getMMdd(): String {
        return format("MMdd")
    }

    //根据卡bin 确定交易code
    fun paymentMethod(cardNo: String? = null): String {
        if (cardNo == null) return ""
        val bin: Int
        if (cardNo.length < 8) return ""
        else bin = cardNo.substring(0, 6).toInt()
        return when (bin) {
            in 400000..499999 -> "10501" //Visa
            in 510000..559999, in 222100..272099, in 679981..679999 -> "10601" //MasterCard
            in 300000..305999, in 309500..309599, in 360000..369999, in 380000..399999 -> "10701" //Diners
            in 601100..601109, in 601120..601149, in 601174..601174, in 601177..601179, in 601186..601199, in 644000..659999 -> "10701"//Discover
            in 350000..359999 -> "10801" //JCB
            in 340000..349999, in 370000..379999 -> "10901" //AMEX
            in 600000..699999 -> "11001" //UnionPay
            else -> "" //未知卡
        }
    }

    /*
    * 返回数组 首期金额 和 其他期金额
    需要在终端上计算好:
      1、总利息=0
      2、非首期的每期还款金额 = (交易金额 Transaction amount + 总利息) /分期期数非首期的每期还款金额，去小数处理，即为一个整数
      3、首期还款金额= (交易金额 Transaction amount + 总利息) -（非首期的每期还款金额去小数后）*（期数-1）。例子：
      金额 = $316.00，期数 = 6 month，利息=0
      非首期的每期还款金额= (316.00 + 0) / 6 = 52.66 ->$52.00 首期还款金额= $316.00 – $52.00*5 = $316 - $260 = $56.00
      备注：4 域不包含利息，结算请求金额不包含利息
     */
    fun parsePeriodAmount(amount: String, period: Int): Array<String> {
        //每期整数金额部分
        val amount2 = if (amount.contains(".")) {
            val arr = amount.split(".")
            arr[0].toInt() / period
        } else {
            amount.toInt() / period
        }
        //首期金额
        val amount1 = amount.toBigDecimal() - amount2.times(period.minus(1)).toBigDecimal()
        return arrayOf(String.format("%.2f", amount1.toDouble()), String.format("%.2f", amount2.toDouble()))
    }


    /*无卡交易列表:主扫|被扫*/
    fun wkTypeList(bitMapBinaryString: String, isB2C: Boolean): MutableList<WkTypeListBean> {
        val typeList = mutableListOf<WkTypeListBean>()
        val typeStr = ConvertUtil.byte2BinaryString(ConvertUtil.hex2Byte(bitMapBinaryString))
        for (i in typeStr.toCharArray().indices) {
            if (typeStr[i] == '1') {
                //商户扫客户  b2c
                if (isB2C) when (i) {
                    0 -> typeList.add(WkTypeListBean("10101", "WeChat"))
                    2 -> typeList.add(WkTypeListBean("10201", "Alipay"))
                    4 -> typeList.add(WkTypeListBean("10301", "UnionPay"))
                    6 -> typeList.add(WkTypeListBean("10401", "GrabPay"))
                    14 -> typeList.add(WkTypeListBean("11101", "PayNow"))
                    16 -> typeList.add(WkTypeListBean("11201", "Singtel Dash"))
                    18 -> typeList.add(WkTypeListBean("11301", "ShopeePay"))
                    20 -> typeList.add(WkTypeListBean("11401", "Thai"))
                    22 -> typeList.add(WkTypeListBean("11501", "LiquidPay"))
                    24 -> typeList.add(WkTypeListBean("11601", "XNAP"))
                    26 -> typeList.add(WkTypeListBean("11701", "DinersSGPay"))
                }
                //客户扫商户 c2b
                else when (i) {
                    1 -> typeList.add(WkTypeListBean("10102", "WeChat"))
                    3 -> typeList.add(WkTypeListBean("10202", "Alipay"))
                    5 -> typeList.add(WkTypeListBean("10302", "UnionPay"))
                    7 -> typeList.add(WkTypeListBean("10402", "GrabPay"))
                    15 -> typeList.add(WkTypeListBean("11102", "PayNow"))
                    17 -> typeList.add(WkTypeListBean("11202", "Singtel Dash"))
                    19 -> typeList.add(WkTypeListBean("11302", "ShopeePay"))
                    21 -> typeList.add(WkTypeListBean("11402", "Thai"))
                    23 -> typeList.add(WkTypeListBean("11502", "LiquidPay"))
                    25 -> typeList.add(WkTypeListBean("11602", "XNAP"))
                    27 -> typeList.add(WkTypeListBean("11702", "DinersSGPay"))
                }
            }
        }
        return typeList
    }

    @JvmStatic
    fun main(args: Array<String>) {

        val aa = "9F260817E5F403894800C7820278008407A0000000041010950500000080009A031511159C01009F02060000000030249F03060000000000009F090200029F10120310A080032200009F0000000000000000FF9F1A0207029F1E0831323233333632309F2701809F3303E0B0C89F34035E03009F3501229F3602000A9F3704144A686E9F4104000087269F5301525F2A0207025F340100"
        TlvUtils.builderTlvList(aa)!!.forEach {
//            println(it.tag + " ->" + it.value)
        }
        val s = TlvUtils.builderTlvToMap(aa)["5F34"]
        println(s)
    }

}