package com.example.iso8583.req

import com.example.iso8583.client.AbstractCallback
import com.example.iso8583.client.BlockSocketClient
import com.example.iso8583.enums.BizCode
import com.example.iso8583.enums.PaymentParams
import com.example.iso8583.util.BizException
import com.example.iso8583.util.Constants
import java.net.InetAddress

/**
 * 网络请求
 */
object RequestHelper {
    /**
     * 发送数据
     *
     * @param bodyMap
     * @param listener
     * @throws Throwable
     */
    @Throws(Throwable::class)
    fun sendData(bodyMap: HashMap<String, Any>, listener: AbstractCallback) {
        try {
            val messageType = bodyMap[Constants.SDK_PAY_TYPE]
            val type = messageType as PaymentParams
            val reqByte: ByteArray = BuildMessage().build(bodyMap, type)
            sendData(Constants.SOCKET_ADR, Constants.SOCKET_PORT, reqByte, listener)
//            sendData(InetAddress.getByName(Constants.SOCKET_ADR).hostAddress!!, Constants.SOCKET_PORT, reqByte, listener)
        } catch (b: BizException) {
            throw BizException(BizCode.SDK0000.code, BizCode.SDK0000.msg)
        } catch (e: Throwable) {
            e.printStackTrace()
            throw BizException(BizCode.SDK0004.code, BizCode.SDK0004.appendMsgDefault(e.message))
        }
    }

    /**
     * 发送数据
     * @param ip
     * @param port
     * @param msgB
     * @param listener
     * @throws Throwable
     */
    @Throws(Throwable::class)
    fun sendData(ip: String, port: Int, msgB: ByteArray, listener: AbstractCallback) {
        val noneHeadSocketClient = BlockSocketClient()
        noneHeadSocketClient.setSendData(ip, port, msgB, listener)
        noneHeadSocketClient.start()
    }
}