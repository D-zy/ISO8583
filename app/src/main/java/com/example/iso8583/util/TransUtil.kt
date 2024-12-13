package com.example.iso8583.util

import com.example.iso8583.bean.WkTypeListBean
import com.example.iso8583.enums.PaymentParams

object TransUtil {
    /**
     * 是否是无卡交易
     *
     * @param code
     * @return
     */
    fun isWKPayFlag(code: String): Boolean {
        return !("10501" == code || "10601" == code || "10701" == code || "10801" == code || "10901" == code || "11001" == code || "11901" == code)
    }

    /**
     * 交易：是否支持批次上送
     */
    fun isSupportBatchUp(transType: String): Boolean {
        return isRefund(transType)
                || transType == Constants.SALE
                || transType == Constants.IPP
                || transType == Constants.TIP_ADJUST
                || transType == Constants.OFFLINE
                || transType == Constants.AUTH_COMPLETE
                || transType == Constants.AUTH_COMPLETE_OFFLINE
    }


    /**
     * 支付宝，微信支付，显示Rmb金额
     *
     * @param transCode 交易code
     * @return
     */
    fun isContainsRMB(transCode: String): Boolean {
        return transCode == "10101" || transCode == "10201" || transCode == "10102" || transCode == "10202"
    }

    fun txnType(transType: String): String {
        return when {
            transType == Constants.SALE -> "S"
            isVoid(transType) -> "V"
            transType == Constants.IPP -> "T"
            isRefund(transType) -> "R"
            transType == Constants.AUTH_COMPLETE_OFFLINE || transType == Constants.AUTH_COMPLETE -> "C"
            transType == Constants.OFFLINE -> "O"
            transType == Constants.TIP_ADJUST -> "A"
            else -> ""

        }
    }

    fun hostType(transCode: String): Int {
        return when (transCode) {
            "11901", "11902" -> 17
            "11701", "11702" -> 16
            "11601", "11602" -> 15
            "11501", "11502" -> 14
            "11401", "11402" -> 13
            "11301", "11302" -> 12
            "11201", "11202" -> 11
            "11101", "11102" -> 10
            "10401", "10402" -> 9
            "10101", "10102" -> 8
            "10201", "10202" -> 7
            "10301", "10302" -> 6
            "11001" -> 5
            "10901" -> 4
            "10801" -> 3
            "10701" -> 2
            "10601" -> 1
            "10501" -> 0
            else -> 18
        }
    }

    /**
     * 是否是 c扫b, pos被扫
     */
    fun isC2B(transCode: String): Boolean {
        return transCode == "10102" || transCode == "10202" || transCode == "10302" || transCode == "10402"
                || transCode == "11102" || transCode == "11202" || transCode == "11302" || transCode == "11302"
                || transCode == "11402" || transCode == "11502" || transCode == "11602" || transCode == "11702"
    }

    /**
     * 是否是分期类型交易
     */
    fun isIppType(transType: String): Boolean {
        return transType == Constants.IPP
                || transType == Constants.IPP_VOID
                || transType == Constants.IPP_REFUND
    }

    /**
     * 是否是2支离线交易： 消费离线, 预授权完成离线
     */
    fun isOfflinePay(transType: String): Boolean {
        return transType == Constants.OFFLINE || transType == Constants.AUTH_COMPLETE_OFFLINE
    }

    fun isOfflineAndTipAdjustPay(transType: String): Boolean {
        return transType == Constants.OFFLINE || transType == Constants.AUTH_COMPLETE_OFFLINE || transType == Constants.TIP_ADJUST
    }

    /**
     * 是否是2支离线撤销交易： 消费离线，预授权完成离线
     */
    fun isOfflineCancelPay(transType: String): Boolean {
        return transType == Constants.OFFLINE_VOID || transType == Constants.COMPLETE_OFFLINE_VOID
    }

    /**
     * 撤销或退款类交易
     */
    fun isVoidOrRefund(transType: String): Boolean {
        return isVoid(transType) || isRefund(transType)
    }

    fun isRefund(transType: String): Boolean {
        return transType == Constants.SALE_REFUND
                || transType == Constants.OFFLINE_REFUND
                || transType == Constants.TIP_ADJUST_REFUND
                || transType == Constants.AUTH_COMPLETE_REFUND
                || transType == Constants.COMPLETE_OFFLINE_REFUND
                || transType == Constants.IPP_REFUND
    }

    fun isVoid(transType: String): Boolean {
        return transType == Constants.SALE_VOID
                || transType == Constants.OFFLINE_VOID
                || transType == Constants.TIP_ADJUST_VOID
                || transType == Constants.AUTH_COMPLETE_VOID
                || transType == Constants.COMPLETE_OFFLINE_VOID
                || transType == Constants.IPP_VOID
    }

    fun saleToRefund(transType: String): String {
        return when (transType) {
            Constants.SALE -> Constants.SALE_REFUND
            Constants.IPP -> Constants.IPP_REFUND
            Constants.TIP_ADJUST -> Constants.TIP_ADJUST_REFUND
            Constants.OFFLINE -> Constants.OFFLINE_REFUND
            Constants.AUTH_COMPLETE -> Constants.AUTH_COMPLETE_REFUND
            Constants.AUTH_COMPLETE_OFFLINE -> Constants.COMPLETE_OFFLINE_REFUND
            else -> ""
        }
    }

    /**
     * 是否可以撤销的交易
     */
    fun isCanVoid(transType: String): Boolean {
        return transType == Constants.SALE
                || transType == Constants.TIP_ADJUST
                || transType == Constants.IPP
                || transType == Constants.OFFLINE
                || transType == Constants.AUTH_COMPLETE
                || transType == Constants.AUTH_COMPLETE_OFFLINE
    }

    fun isCanRefund(transType: String): Boolean {
        return transType == Constants.SALE
                || transType == Constants.TIP_ADJUST
                || transType == Constants.OFFLINE
                || transType == Constants.AUTH_COMPLETE
                || transType == Constants.AUTH_COMPLETE_OFFLINE
                || transType == Constants.IPP //TODO:20230807
    }

    /**
     * 是否可以冲正
     */
    fun isCzFlag(transType: String): Boolean {
        return transType == Constants.SALE_REFUND
                || transType == Constants.OFFLINE_REFUND
                || transType == Constants.TIP_ADJUST_REFUND
                || transType == Constants.IPP_REFUND
                || transType == Constants.SALE_VOID
                || transType == Constants.TIP_ADJUST //小费调整是不支持的，这里放开只是为了测试
                || transType == Constants.TIP_ADJUST_VOID
                || transType == Constants.IPP_VOID
                || transType == Constants.SALE
                || transType == Constants.IPP
                || transType == Constants.PRE_AUTH
                || transType == Constants.OFFLINE_VOID
                || transType == Constants.AUTH_REVERSAL
                || transType == Constants.COMPLETE_OFFLINE_VOID
                || transType == Constants.AUTH_COMPLETE_REFUND
                || transType == Constants.COMPLETE_OFFLINE_REFUND

    }




}