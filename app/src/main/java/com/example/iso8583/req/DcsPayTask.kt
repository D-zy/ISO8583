package com.example.iso8583.req

import android.content.Context
import com.example.iso8583.bill.RespData
import com.example.iso8583.client.AbstractCallback
import com.example.iso8583.client.ResponseMessage
import com.example.iso8583.enums.BizCode
import com.example.iso8583.enums.PaymentParams
import com.example.iso8583.util.*

/**
 * 支付业务实现层
 *
 * @author a
 */
open class DcsPayTask {

    private lateinit var _context: Context

    /**
     * 初始化环境
     */
    @Throws(Exception::class)
    protected fun initEnv(context: Context) {
        _context = context.applicationContext
    }

    /**
     * 签到
     */
    protected fun signOnTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.SINGON
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 签退
     */
    protected fun signOffTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.SINGOFF
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 消费
     */
    protected fun saleTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.YK_SALE
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 消费分期
     */
    protected fun ippTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.IPP
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 小费调整-离线
     */
    protected fun tipAdjustTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.TIP_ADJUST
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 消费撤销
     */
    protected fun voidTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.VOID
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 消费退款
     */
    protected fun refundTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.REFUND
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 预授权
     */
    protected fun preAuthTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.PRE_AUTH
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 预授权撤销
     */
    protected fun preAuthVoidTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.AUTH_REVOCATION
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 预授权完成离线
     */
    protected fun preAuthCaptureOfflineTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.AUTH_COMPLETE_OFFLINE
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 离线交易
     */
    protected fun offlineTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.OFFLINE
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 结算请求
     */
    protected fun settlementRequestTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.SETTLEMENT_REQUEST
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 结算完成
     */
    protected fun settlementCompletionTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.SETTLEMENT_COMPLETION
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 批次上送
     */
    protected fun batchUploadTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.BATCH_UPLOAD
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 无卡支付
     * 包含主扫与被扫模式
     */
    protected fun noCardTypeListTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.WK_TYPE_LIST
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 无卡支付
     * 包含主扫与被扫模式
     */
    protected fun noCardTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.WK_SALE
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 无卡支付结果查询
     */
    protected fun noCardQueryTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.WK_QUERY
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 冲正 (有卡)
     */
    protected fun reverseTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.YK_REVERSE
        publicRouteTask(bodyMap, listener)
    }

    /**
     * pinKey
     */
    protected fun downloadPinKeyTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.DOWNLOAD_PIN_KEY
        publicRouteTask(bodyMap, listener)
    }

    protected fun rsaPublicKeyDownloadTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.RSA_KEY_DOWNLOAD
        publicRouteTask(bodyMap, listener)
    }

    protected fun tmkDownloadTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        bodyMap[Constants.SDK_PAY_TYPE] = PaymentParams.TMK_DOWNLOAD
        publicRouteTask(bodyMap, listener)
    }

    /**
     * 公共业务路由处理任务
     */
    private fun publicRouteTask(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        try {
//            CoroutineScope(Dispatchers.IO).launch {
            runCatching { LogUtils.w("\n报文参数:${bodyMap}") }
            //在线
            RequestHelper.sendData(bodyMap, object : AbstractCallback() {
                override fun onSuccess(message: ResponseMessage) {
                    val paySlip = RespData(message.messagePackage, bodyMap)
                    listener.workCall(message.code, message.message, paySlip.toJsonString())
                }
            })
//            }
        } catch (biz: BizException) {
            listener.workCall(biz.code, biz.msg)
        } catch (e: Throwable) {
            listener.workCall(BizCode.SDK0008.code, BizCode.SDK0008.appendMsgDefault(e.message))
        }
    }

}