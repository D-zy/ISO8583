package com.example.iso8583.bean

data class ResultBean(var code: String, var msg: String, var data: Data) {

    fun isSuccess(): Boolean = code == "0000"

    data class Data(
        var typeDes: String,
        var mti: String,
        var processingCode: String,
        var mmdd: String,
        var hhmmss: String,
        var merchantId: String, //商户号
        var terminalId: String, //终端机号
        var cardNo: String, //卡号
        var expiryDate: String, //卡片有效期
        var responseCode: String,
        var amount: String, //交易金额
        var tip: String, //交易小费
        var traceNo: String, //交易流水号 (凭证号)
        var refNo: String, //参考号
        var authCode: String, //授权号
        var batchNo: String, //批次号
        var invoiceNo: String, //发票号
        var settlementMode: String,
        var transCode: String,
        var qrCodeValue: String, //无卡二维码值
        var wkTypeList: String, //无卡支持类型列表
        var field62: String,

//        var transStatus: String,
//        var merchantName: String, //商户名称
//        var payOrderId: String, //支付订单号
//        var exchangeRate: String,
//        var rmbTotalAmount: String,
//        var paymentId: String,
//        var transMethod: String, //交易方式： 支付宝，微信，jcb....
//        var transType: String, //交易类型:消费，退款...
//        var extOrderId: String, //商户订单号
//        var dateTime: String, //时间字符串，格式 yyyyMMdd  HHmmss
//        var oriDateTime: String, //原交易时间字符串，格式 yyyyMMdd  HHmmss
//        var oriTraceNo: String, //原交易流水号
//        var cashierNum: String, //收银员号
//        var newTip: String, //交易小费
//        var totalAmount: String, //交易总金额
//        var icRemark: String, //IC备注
//        var track1Data: String,
//        var track2Data: String,
//        var cardPin: String,
//        var cardPinLength: String,
//        var readCardMethod: String, //读卡方式
//        var entryMode: String, //读卡方式
//        var signature: String,
//        var ippPeriod: String,
//        var ippIntrRate: String,
//        var ippFirstPymt: String,
//        var ippOtherPymt: String,
//        var ippTotalInt: String,
//        var installmentsInfo: String, //期数 || 首期还款金额 || 非首期还款金额 || 利率 || 利息
//        var printId: String,

    )
}


data class WkTypeListBean(var transCode: String, var transType: String)