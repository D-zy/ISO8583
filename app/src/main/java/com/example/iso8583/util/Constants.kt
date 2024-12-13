package com.example.iso8583.util

/**
 * 基本变量、常量配置中心
 *
 * @author a
 */
object Constants {
    /**
     * 开启模式；如日志，或者单元测试的开关控制
     */
    @JvmField
    var isDeBug = false

    const val PACKAGE_NAME = "com.example"

    /**
     * 数据传输定义的map key值
     */
    const val FIELD_MTI = "MTI"
    const val FIELD_2 = "cardNo"
    const val FIELD_3 = "processingCode"
    const val FIELD_4 = "amount"
    const val FIELD_11 = "traceNo"
    const val FIELD_12 = "HHmmss"
    const val FIELD_13 = "MMdd"
    const val FIELD_14 = "expiryDate" //卡片有效期
    const val FIELD_22 = "entryMode"
    const val FIELD_23 = "cardSequenceNo"
    const val FIELD_24 = "networkIdentificationCode"
    const val FIELD_25 = "servicePointConditionCode"
    const val FIELD_26 = "pinLength" //pin 长度
    const val FIELD_35 = "track2Data"
    const val FIELD_36 = "track3Data"
    const val FIELD_38 = "authCode"
    const val FIELD_39 = "responseCode"
    const val FIELD_37 = "refNo"
    const val FIELD_41 = "terminalId"
    const val FIELD_42 = "merchantId"
    const val FIELD_52 = "cardPIN"
    const val FIELD_54 = "tip"
    const val FIELD_55 = "icData"
    const val FIELD_45 = "track1Data"
    const val FIELD_48_1_CVV2 = "cvv2"
    const val FIELD_48_2_ORI_AMT = "oriAmount"
    const val FIELD_48_3_SETTLEMENT_MODE = "settlementMode"
    const val FIELD_49 = "currencyCode"
    const val FIELD_59 = "transCode"
    const val FIELD_62 = "field62"
    const val FIELD_62_INV_NO = "invoiceNo" //62 发票号
    const val FIELD_60_1_SN = "sn"
    const val FIELD_60_2_BATCH_NO = "batchNo"
    const val FIELD_60_3_ORI_MTI = "oriMTI"
    const val FIELD_60_4_ORI_TRACE_NO = "oriTraceNo"
    const val FIELD_61_QR_CODE = "qrCode" //qr码值
    const val FIELD_61_ORI_DATE = "oriDate" //原交易日期
    const val FIELD_63 = "field63" //结算数据 或 分期交易数据

    const val SDK_PAY_TYPE = "payType"
    const val SDK_IC_REMARK = "icRemark"
    const val SDK_PRINT_ID = "printId"

    /**
     * 外部响应Json数据key
     */
    const val SDK_JSON_KEY_CODE = "code"
    const val SDK_JSON_KEY_MSG = "msg"
    const val SDK_JSON_KEY_DATA = "data"

    /**
     * 返回数据域长度内容数据解析 @{@see AnalysisFieldLengthType.java}
     */
    const val TYPE_N = "N"
    const val TYPE_N_HEX = "N_HEX"
    const val TYPE_L_N = "L_N"
    const val TYPE_L_NEX = "L_NEX"



    /**
     * 服务端网络成功码
     */
    const val NET_SUCCESS = "00"

    /**
     * 交易处理中码
     */
    const val CODE_Q8 = "Q8"

    /**
     * 网络socket配置
     */
    var SOCKET_ADR = "uat-pos.ezynet.sg"
    var SOCKET_PORT = 28998

    /**
     * 网络socket超时时间 单位(秒)
     */
    const val SOCKET_REQ_TIME_OUT = 60
    const val SOCKET_READ_TIME_OUT = 60
    /** --------------------------------8583报文域常量配置----------------------------  */
    /**
     * tpdu
     */
    const val FLAG_8583_TPDU = "6000093019"

    /**
     * 8583 header
     */
    const val FLAG_8583_HEADER = "603100311003"

    /**
     * 位图长度
     */
    const val BITMAP_BYTE_NUM_64 = 8

    /**
     * 39域 冲正
     * 98 POS终端在时限内未能收到POS中心的应答
     * 96 POS终端收到POS中心的批准应答消息，但由于POS机故障无法完成交易
     * A0 POS终端对收到POS中心的批准应答消息，验证MAC出错
     * 06 其他情况引发的冲正
     */
    const val ISO8583_REVERSE_68 = "68"
    const val ISO8583_REVERSE_Q8 = "Q8"
    const val ISO8583_REVERSE_98 = "98"
    const val ISO8583_REVERSE_A0 = "A0"
    /**
     * 49域，汇币类型；156代表软妹币
     */
    const val ISO8583_FIELD_49_1 = "702"
    const val LOGON = "LOGON" //签到
    const val SALE = "SALE" //消费
    const val SALE_VOID = "SALE VOID" //消费撤销
    const val SALE_REFUND = "SALE REFUND" //消费退款
    const val OFFLINE = "OFFLINE" //离线消费
    const val OFFLINE_VOID = "OFFLINE VOID" //离线撤销
    const val OFFLINE_REFUND = "OFFLINE REFUND" //离线退款
    const val TIP_ADJUST = "TIP ADJUST" //小费调整
    const val TIP_ADJUST_VOID = "TIP ADJUST VOID" //小费调整撤销
    const val TIP_ADJUST_REFUND = "TIP ADJUST REFUND" //小费调整退款
    const val IPP = "IPP" //分期消费
    const val IPP_VOID = "IPP VOID" //分期消费撤销
    const val IPP_REFUND = "IPP REFUND" //分期消费退款--->
    const val SETTLEMENT_REQUEST = "SETTLEMENT_REQUEST" //
    const val BATCH_UPLOAD = "BATCH_UPLOAD" //
    const val SETTLEMENT_COMPLETION = "SETTLEMENT_COMPLETION" //
    const val ECHO_TEST = "ECHO_TEST" //
    const val PIN_WORKING_KEY = "PIN_Working_Key" //
    const val PIN_CHANGE_ACKNOWLEDGEMENT = "PIN_CHANGE_ACKNOWLEDGEMENT" //
    const val SALE_CERTIFICATE = "SALE_CERTIFICATE" //

    // 无该类型，多余
    const val IPP_TIP_ADJUST_VOID = "IPP TIP ADJUST VOID" //分期消费 小费调整撤销--->无该类型，多余
    const val PRE_AUTH = "PRE AUTH" //预授权
    const val AUTH_REVERSAL = "AUTH REVERSAL" //预授权撤销

    const val AUTH_COMPLETE = "AUTH COM." //预授权完成
    const val AUTH_COMPLETE_VOID = "AUTH COM.  VOID" //预授权完成撤销
    const val AUTH_COMPLETE_REFUND = "AUTH COM. REFUND" //预授权完成退款

    const val AUTH_COMPLETE_OFFLINE = "AUTH COM. OFFLINE" //预授权完成离线
    const val COMPLETE_OFFLINE_VOID = "COM. OFFLINE VOID" //预授权完成离线撤销
    const val COMPLETE_OFFLINE_REFUND = "COM. OFFLINE REFUND" //预授权完成离线退款
    const val REVERSED = "REVERSED" // 冲正
    const val WECHAT = "WECHAT" //
    const val ALIPAY = "ALIPAY" //
    const val UNION = "UNION" //
    const val GRABPAY = "GRABPAY" //


}