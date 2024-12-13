package com.example.iso8583.req

import android.content.Context

/**
 * sdk接口
 *
 * @author a
 */
interface IPayCall {
    @Throws(Exception::class)
    fun init(context: Context)
    fun sale(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun tipAdjust(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun void(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun refund(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun preAuth(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun preAuthVoid(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun preAuthCaptureOffline(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun noCardTypeList(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun noCard(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun noCardQuery(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun reverse(bodyMap: HashMap<String, Any>, listener: PayCallStateListener)
    fun offline(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun ipp(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun settlementRequest(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun settlementCompletion(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)
    fun batchUpload(bodyMap: HashMap<String, Any>, callStateListener: PayCallStateListener)

    fun signOn(bodyMap: HashMap<String, Any>, listener: PayCallStateListener)
    fun signOff(bodyMap: HashMap<String, Any>, listener: PayCallStateListener)
    fun pinKeyDownload(bodyMap: HashMap<String, Any>, listener: PayCallStateListener)
    fun rsaPublicKeyDownload(bodyMap: HashMap<String, Any>, listener: PayCallStateListener)
    fun tmkDownload(bodyMap: HashMap<String, Any>, listener: PayCallStateListener)

}