package com.example.iso8583.req

import com.example.iso8583.enums.BizCode
import com.example.iso8583.util.Constants
import com.example.iso8583.util.LogUtils
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * 接口回调。（这个类是对服务端返回报文以及sdk内部其他返回报文格式统一封装对象）
 *
 * @author a
 */
abstract class PayCallStateListener {
    /**
     * 外部接收sdk结果处理回调。结果包含所有情况，如服务端成功/失败结果，客户端网络异常，客户端支付取消等。
     *
     * @param result
     * @param obj
     */
    abstract fun onSuccessCall(result: String)

    open fun onSuccessCallInterceptor(code: String, msg: String, result: String) {
        onSuccessCall(result)
    }

    /**
     * 扩展函数。用于对无卡被扫模式下由于用户输入密码导致支付同步支付超时，用这个订单反查支付结果。
     *
     * @param sysOrderId
     */
    fun onCallPayOrder(sysOrderId: String) {}

    /**
     * 扩展函数。回调刷卡类型
     *
     * @param type
     */
    open fun onCallSwipeCardType(type: String) {}

    open fun onCallStartPay(payMap: HashMap<String, Any>) {}

    /**
     * 扩展函数。刷卡状态回调更新UI;
     * 主线程->回调
     *
     * @param stateId
     * @param msg
     */
    open fun onStateCall(stateId: Int, msg: String) {}

    fun workCall(code: String, msg: String) {
        workCall(code, msg, null)
    }

    /**
     * 封装函数。
     *
     * @param code
     * @param data
     * @param obj
     */
    fun workCall(code: String, msg: String, data: Any?) {
        try {
            val ext = JSONObject()
            //成功码，sdk与服务端业务都无需关注成功码本身结果，所以这里兼容统一成sdk成功码（sdk：0000 service：00）
            val newCode = if (Constants.NET_SUCCESS == code) BizCode.SDK0000.code else code
            ext.put(Constants.SDK_JSON_KEY_CODE, newCode)
            ext.put(Constants.SDK_JSON_KEY_MSG, msg)
            try {
                ext.put(Constants.SDK_JSON_KEY_DATA, JSONObject(data.toString()))
            } catch (e: Throwable) {
                ext.put(Constants.SDK_JSON_KEY_DATA, JSONObject())
            }
            LogUtils.ext("sdk result:%s", ext.toString())
            onSuccessCallInterceptor(newCode, msg, ext.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            onSuccessCall("")
        }

    }

}