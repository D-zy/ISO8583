package com.example.iso8583.enums

/**
 * 业务码
 *
 * @param code 业务码
 * @param msg  业务码描述
 */
enum class BizCode(var code: String, var msg: String) {
    SDK0000("0000", "成功"),
    SDK0001("0001", "网络异常"),
    SDK0002("0002", "交易取消"),
    SDK0003("0003", "请重新签到"),
    SDK0004("0004", "请求组包失败"),
    SDK0005("0005", "无效的请求类型"),
    SDK0006("0006", "无效外部参数r"),
    SDK0007("0007", "无效卡数据"),
    SDK0008("0008", "未知错误"),
    SDK0009("0009", "无效的业务类型"),
    SDK0010("0010", "无卡支付码为空"),
    SDK0011("0011", "MAC校验失败"),
    SDK0012("0012", "输入密码异常"),
    SDK0013("0013", "主密钥加载失败"),
    SDK0014("0014", "签到加载失败"),
    SDK0015("0015", "交易失败"),
    SDK0016("0016", "service未响应"),
    SDK0017("0017", "交易参数不全"),
    SDK0018("0018", "无效交易金额"),
    SDK0019("0019", "厂家自定义错误描述"),
    SDK0020("0020", "有作弊嫌疑"),
    SDK0021("0021", "缺少打印纸"),
    SDK0022("0022", "打印失败"),
    SDK0023("0023", "功能暂未实现"),
    SDK0024("0024", "无效的交易类型"),
    SDK0025("0025", "您的请求过快，请稍后重试！"),
    SDK0026("0026", "服务器异常"),
    SDK0027("0027", "外部订单号重复"),
    SDK0028("0028", "不允许的冲正交易"),
    SDK0029("0029", "查询失败"),
    SDK0030("0030", "无效的数据返回"),
    SDK0031("0031", "请下载主密钥"),
    SDK0032("0032", "签购单上传失败"),
    SDK0033("0033", "处理超时"),
    SDK0034("0034", "系统异常"),
    SDK0035("0035", "交易次数超限"),
    SDK0036("0036", "不支持nfc"),
    SDK0037("0037", "收款码生成失败"),
    SDK0038("0038", "重新下载主密钥"),
    SDK0039("0039", "二次授权失败");

    /**
     * 来自额外拼接的信息，每次更新需要清除
     */
    private val appendMsg = ""

    /**
     * 自定义替换描述信息
     *
     * @param msg
     * @return
     */
    fun replaceMsg(msg: String): BizCode {
        this.msg = msg
        return this
    }

    fun replaceCode(code: String): BizCode {
        this.code = code
        return this
    }

    /**
     * 拼接描述信息
     * 应用场景：某些code会在不同业务流程下重复使用，因此为了区别，
     * 如：map在put时出错与组包时出错，我们可以设置相同的code，拼接不同的错误描述，"未知错误（map参数缺失/请求组包失败）"
     *
     * @param msg
     * @return
     */
    private fun appendMsg(appendMsg: String): String {
        return msg + appendMsg
    }

    /**
     * 默认拼接描述信息，带"()"符号
     *
     * @param msg
     * @return
     */
    fun appendMsgDefault(msg: String? = ""): String {
        return appendMsg("($msg)")
    }


}