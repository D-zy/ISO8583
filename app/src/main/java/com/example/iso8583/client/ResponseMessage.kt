package com.example.iso8583.client

import com.example.iso8583.enums.BizCode
import com.example.iso8583.parse.AnalysisFieldMessagePackage
import com.example.iso8583.util.BizException
import com.example.iso8583.util.LogUtils
import java.io.Serializable

/**
 * 响应消息对象
 *
 */
class ResponseMessage : Serializable {
    var messagePackage: AnalysisFieldMessagePackage? = null
        private set
    lateinit var code: String
        private set
    lateinit var message: String
        private set

    fun setCode(newCode: String) {
        code = newCode
    }

    private constructor() {}
    constructor(code: String, msg: String) {
        this.code = code
        message = msg
    }

    @JvmOverloads
    constructor(messageB: ByteArray?, b: BizException? = null) {
        initAnalysisFieldMessagePackage(messageB, b)
    }

    /**
     * 初始化解码模块儿辅助类
     * @param messageB
     */
    private fun initAnalysisFieldMessagePackage(messageB: ByteArray?, bx: BizException?) {
        try {
            if (null != messageB) {
                //解码数据包
                messagePackage = AnalysisFieldMessagePackage()
                messagePackage?.let {
                    it.deCode(messageB)
                    //解码业务响应码与描述
                    code = bx?.code ?: it.returnCode()
                    message = bx?.msg ?: it.returnMessage()
                }
            } else if (null != bx) {
                //异常
                code = bx.code
                message = bx.msg
            } else {
                //网络异常
                code = BizCode.SDK0001.code
                message = BizCode.SDK0001.msg
            }
        } catch (b: BizException) {
            LogUtils.e("解析数据包过程发生异常:%s", b.msg)
            code = b.code
            message = b.msg
        } catch (e: Throwable) {
            LogUtils.e("解析数据包过程发生异常:%s", e.message)
            code = BizCode.SDK0034.code
            message = BizCode.SDK0034.msg
        }
    }

    override fun toString(): String {
        return "ResponseMessage{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}'
    }
}