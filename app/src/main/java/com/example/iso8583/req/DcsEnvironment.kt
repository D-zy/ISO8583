package com.example.iso8583.req

import com.example.iso8583.util.Constants

/**
 * 环境配置对象。
 * @author a
 */
object DcsEnvironment {

    /**
     * 设置支付地址
     * @param payURL
     */
    fun setPayUrl(payURL: String) {
        val adr = payURL.split(":").toTypedArray()
        Constants.SOCKET_ADR = adr[0]
        Constants.SOCKET_PORT = adr[1].toInt()
    }

    /**
     * sdk debug状态  设置外部日志输出
     * @return
     */
    fun isDebug(isbug: Boolean) {
        Constants.isDeBug = isbug
    }

}