package com.example.iso8583.client

import com.example.iso8583.util.BizException

abstract class AbstractCallback {
    /**
     * 成功回调
     * @param message
     * @throws Throwable
     */
    protected abstract fun onSuccess(message: ResponseMessage)

    /**
     * 失败回调
     * @param code
     * @param message
     */
    @Deprecated("")
    fun onFail(code: String, message: String) {
        //预留方法
    }
    /**
     * 服务端数据包解码
     * @param messageB
     * @param b
     */
    /**
     * 服务端数据包解码
     * @param messageB
     */
    @JvmOverloads
    fun decode(messageB: ByteArray?, b: BizException? = null) {
        //构建响应消息
        val responseMessage = ResponseMessage(messageB, b)
        onSuccess(responseMessage)
    }
}