package com.example.iso8583.util

object TrackUtils {
    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
//        encrypt("6011492100005191=24071011123412312345", true);
        isFallBack("6011492100005191=24071011123412312345")
        encrypt("B6011492100005191^TEST CARD/DISCOVER ^2407101112341231234567890123456", false)
    }

    /**
     * 银联磁条交易需检测：https://docs.sunmi.com/others/magnetic-stripe-card-data-format/magnetic-stripe-card-data-format/
     * 是否是芯片卡降级交易 (复合卡的服务代码总是2开头或者6开头的三位数，而普通的单磁条卡的服务代码一定不以2或者6开头)
     * 根据二磁道=符号后面第五位识别：规则是 该数字 ==6 或者 ==2  则该卡是IC卡，否则是普通磁条卡
     */
    fun isFallBack(values: String): Boolean {
        if (values.contains("=")) {
            val s = values.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val v = s.substring(4, 5)
            return v == "2" || v == "6"
        }
        return false
    }

    /**
     * 二磁道需要将“=”转为“D”后，长度为16的倍数，不足补F，再加密
     *
     * @param values    磁道值
     * @param isReplace 二磁道：true   三磁道：false
     */
    @Throws(Exception::class)
    fun encrypt(values: String, isReplace: Boolean): String {
        var newValues = values
        if (isReplace) {
            newValues = newValues.replace("=", "D")
        }
        val length = newValues.length
        val x = length % 16
        // 需要补位的长度
        var addLen = 0
        if (x != 0) {
            addLen = 16 - length % 16
        }
        val sb = StringBuilder(newValues)
        for (i in 0 until addLen) {
            sb.append("F")
        }
        newValues = sb.toString()
        println(newValues)
        return newValues
    }
}