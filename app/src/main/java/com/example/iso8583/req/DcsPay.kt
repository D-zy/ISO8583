package com.example.iso8583.req

import android.annotation.SuppressLint
import android.content.Context
import java.util.*

/**
 * 支付业务接口层
 *
 * @author a
 */
class DcsPay private constructor() : DcsPayTask(), IPayCall {

    /**
     * sdk初始化函数【加载资源文件与实例化】
     *
     * @param context
     */
    override fun init(context: Context) {
        initEnv(context)
    }

    /**
     * 消费
     */
    override fun sale(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        saleTask(bodyMap, callStateListener)
    }

    /**
     * 分期
     */
    override fun ipp(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        ippTask(bodyMap, callStateListener)
    }

    /**
     * 小费调整
     */
    override fun tipAdjust(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        tipAdjustTask(bodyMap, callStateListener)
    }

    /**
     * 消费撤销
     */
    override fun void(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        voidTask(bodyMap, callStateListener)
    }

    /**
     * 消费退款
     */
    override fun refund(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        refundTask(bodyMap, callStateListener)
    }

    /**
     * 预授权
     */
    override fun preAuth(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        preAuthTask(bodyMap, callStateListener)
    }

    /**
     * 预授权撤销
     */
    override fun preAuthVoid(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        preAuthVoidTask(bodyMap, callStateListener)
    }

    /**
     * 预授权完成离线
     */
    override fun preAuthCaptureOffline(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        preAuthCaptureOfflineTask(bodyMap, callStateListener)
    }

    /**
     * 无卡支付类型列表
     */
    override fun noCardTypeList(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        noCardTypeListTask(bodyMap, callStateListener)
    }

    /**
     * 无卡支付
     */
    override fun noCard(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        noCardTask(bodyMap, callStateListener)
    }

    /**
     * 无卡支付查询
     */
    override fun noCardQuery(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        noCardQueryTask(bodyMap, callStateListener)
    }

    /**
     * 冲正
     */
    override fun reverse(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        reverseTask(bodyMap, listener)
    }

    /**
     * 离线交易
     */
    override fun offline(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        offlineTask(bodyMap, callStateListener)
    }

    /**
     * 结算请求
     */
    override fun settlementRequest(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        settlementRequestTask(bodyMap, callStateListener)
    }

    /**
     * 结算完成
     */
    override fun settlementCompletion(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        settlementCompletionTask(bodyMap, callStateListener)
    }

    /**
     * 批上送
     */
    override fun batchUpload(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener) {
        batchUploadTask(bodyMap, callStateListener)
    }

    /**
     * 下载pinKey
     */
    override fun pinKeyDownload(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        downloadPinKeyTask(bodyMap, listener)
    }

    /**
     * 下载RSA公钥
     */
    override fun rsaPublicKeyDownload(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        rsaPublicKeyDownloadTask(bodyMap, listener)
    }

    /**
     * 下载TMK
     */
    override fun tmkDownload(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        tmkDownloadTask(bodyMap, listener)
    }

    /**
     * 签到
     */
    override fun signOn(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        signOnTask(bodyMap, listener)
    }

    /**
     * 签退
     */
    override fun signOff(bodyMap: HashMap<String, Any>, listener: PayCallStateListener) {
        signOffTask(bodyMap, listener)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var _instance: DcsPay? = null

        private var refDL = false

        @JvmStatic
        val instance: IPayCall
            get() {
                if (null == _instance) {
                    refDL = !refDL
                    _instance = DcsPay()
                }
                return _instance!!
            }
    }

    init {
        if (refDL) {
            refDL = false
        } else {
            try {
                throw IllegalAccessException("不允许通过反射方式创建.")
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }
}