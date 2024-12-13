package com.example.iso8583

import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.iso8583.bean.ResultBean
import com.example.iso8583.req.DcsPay
import com.example.iso8583.req.PayCallStateListener
import com.example.iso8583.util.Constants
import com.example.iso8583.util.ConvertUtil
import com.example.iso8583.util.DcsUtils
import com.example.iso8583.util.DesUtils
import com.example.iso8583.util.LogUtils
import com.example.iso8583.util.PinUtils
import com.example.iso8583.util.RSAUtils
import com.example.iso8583.util.SpPos
import com.example.iso8583.util.TlvUtils
import com.example.iso8583.util.TrackUtils
import com.google.gson.Gson
import com.xuexiang.xui.utils.WidgetUtils
import com.xuexiang.xui.utils.XToastUtils
import com.xuexiang.xui.widget.dialog.LoadingDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.textview.LoggerTextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    //测试数据
    private val mid = "000240320000003"
    private val tid = "67200001"
    private val cardNo = "5413330057004047"
    private val expiryDate = "2512"
    private val cvv2 = "989"
    private val pin = "5587"
    private val track1 = "" //暂无测试数据
    private val track2 = "5413330057004047D2512201055420977"
    private val iccData = "9F260817E5F403894800C7820278008407A0000000041010950500000080009A031511159C01009F02060000000030249F03060000000000009F090200029F10120310A080032200009F0000000000000000FF9F1A0207029F1E0831323233333632309F2701809F3303E0B0C89F34035E03009F3501229F3602000A9F3704144A686E9F4104000087269F5301525F2A0207025F340100"
    private val currencyCode = "702"
    private val sn = "sn123456" //如果没有,可为空字符串
    private val sale = "sale"
    private val preAuth = "preAuth"
    private lateinit var logger: LoggerTextView
    private lateinit var readMethod: RadioGroup
    private var transMap = mutableMapOf<String, ResultBean.Data>() //记录交易
    private lateinit var oriData: ResultBean.Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logger = findViewById(R.id.logger)
        readMethod = findViewById(R.id.rgReadMethod)
        DcsUtils.logger = logger
        logger.setLogFormatter { logContent, _ -> "[${DcsUtils.format("HH:mm:ss")}] $logContent" }

        findViewById<AppCompatButton>(R.id.keyDownload).setOnClickListener { rsaPublicKeyDownload() }
        findViewById<AppCompatButton>(R.id.signOn).setOnClickListener { signOn() }
        findViewById<AppCompatButton>(R.id.signOff).setOnClickListener { signOff() }
        findViewById<AppCompatButton>(R.id.sale).setOnClickListener { sale() }
        findViewById<AppCompatButton>(R.id.offline).setOnClickListener { offline() }
        findViewById<AppCompatButton>(R.id.ipp).setOnClickListener { ipp() }
        findViewById<AppCompatButton>(R.id.saleVoid).setOnClickListener { void() }
        findViewById<AppCompatButton>(R.id.refund).setOnClickListener { refund() }
        findViewById<AppCompatButton>(R.id.tipAdjust).setOnClickListener { tipAdjust() }
        findViewById<AppCompatButton>(R.id.preAuth).setOnClickListener { preAuth() }
        findViewById<AppCompatButton>(R.id.preAuthVoid).setOnClickListener { preAuthVoid() }
        findViewById<AppCompatButton>(R.id.preAuthCaptureOffline).setOnClickListener { preAuthCaptureOffline() }
        findViewById<AppCompatButton>(R.id.noCardList).setOnClickListener { noCardList() }
        findViewById<AppCompatButton>(R.id.b2c).setOnClickListener { b2c() }
        findViewById<AppCompatButton>(R.id.c2b).setOnClickListener { c2b() }
        findViewById<AppCompatButton>(R.id.noCardQuery).setOnClickListener { noCardQuery() }
        findViewById<AppCompatButton>(R.id.settlementRequest).setOnClickListener { settlementRequest() }
        findViewById<AppCompatButton>(R.id.batchUpload).setOnClickListener { batchUpload() }
        findViewById<AppCompatButton>(R.id.settlementComplete).setOnClickListener { settlementComplete() }

    }

    /*RSA公钥下载*/
    private fun rsaPublicKeyDownload() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0800"
        req[Constants.FIELD_3] = "710000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        DcsPay.instance.rsaPublicKeyDownload(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    val map62 = TlvUtils.builderTlvToMap3(entity.data.field62)
                    val rsaPublicKey = map62["01"] ?: ""
                    val randomPlaintext = DcsUtils.getHex32()
                    LogUtils.p("随机明文:$randomPlaintext")
                    val v1 = ConvertUtil.byte2HexString(RSAUtils.encryptByPublicKey(ConvertUtil.hex2Byte(randomPlaintext), rsaPublicKey))
                    val v2 = DesUtils.encryptDoubleDes("0000000000000000", randomPlaintext)
                    val f62_1 = "01" + ConvertUtil.toHex(v1.length, 4) + v1
                    val f62_2 = "02" + ConvertUtil.toHex(v2.length, 4) + v2
                    val field62 = f62_1 + f62_2

                    //tmk下载
                    tmkDownload(field62, randomPlaintext)
                } else cancelLoading("RSA公钥下载失败${entity.msg}")

            }
        })
    }

    /*TMK下载*/
    private fun tmkDownload(field62: String, randomPlaintext: String) {
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0800"
        req[Constants.FIELD_3] = "810000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_62] = field62
        DcsPay.instance.tmkDownload(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    val f62 = entity.data.field62
                    val map62 = TlvUtils.builderTlvToMap3(f62)
                    val tmk = map62["01"] ?: ""
                    LogUtils.p("key(返回密文Tmk):$tmk")
                    val plainTextTmk = DesUtils.decryptDoubleDes(tmk, randomPlaintext)
                    LogUtils.p("key(明文Tmk):${plainTextTmk}")

                    //pinKey下载
                    pinKeyDownload(plainTextTmk)
                } else cancelLoading("TMK下载失败${entity.msg}")
            }
        })
    }

    /*pinKey下载*/
    private fun pinKeyDownload(plaintextTmk: String) {
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0800"
        req[Constants.FIELD_3] = "910000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        DcsPay.instance.pinKeyDownload(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    val f62 = entity.data.field62
                    val map62 = TlvUtils.builderTlvToMap3(f62)
                    val f62_1 = map62["01"] ?: ""
                    val f62_2 = map62["02"] ?: ""
                    val pinPublicKey = DesUtils.decryptDoubleDes(f62_1, plaintextTmk)
                    val isCheck = DesUtils.encryptDoubleDes("0000000000000000", pinPublicKey) == f62_2
                    println(DesUtils.encryptDoubleDes("0000000000000000", pinPublicKey))
                    SpPos.pinPublicKey = pinPublicKey
                    println("key(pinKey返回 f62_1:$f62_1")
                    println("key(pinKey返回 f62_2:$f62_2")
                    if (!isCheck) showToast("key校验不过")
                    cancelLoading("下载成功")
                } else cancelLoading("pinKey下载失败${entity.msg}")
            }
        })
    }

    /*签到*/
    private fun signOn() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0800"
        req[Constants.FIELD_3] = "920000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_60_1_SN] = sn
        DcsPay.instance.signOn(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    SpPos.signTag = true
                    SpPos.batchNo = entity.data.batchNo
                    SpPos.settlementMode = entity.data.settlementMode
                    cancelLoading("签到成功")
                } else cancelLoading("签到失败${entity.msg}")
            }
        })
    }

    /*签退*/
    private fun signOff() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0820"
        req[Constants.FIELD_3] = "920000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        DcsPay.instance.signOff(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    SpPos.signTag = false
                    cancelLoading("签退成功")
                } else cancelLoading("签退失败${entity.msg}")
            }
        })
    }

    /*设置其他参数*/
    private fun setOtherReq(req: HashMap<String, Any>) {
        //是否含密码: 有密码->1  无密码->2
        var f22_3 = mutableListOf("1", "2").random() //22域第3位数字, 默认没密码
        if (f22_3 == "1" && readMethod.checkedRadioButtonId != R.id.rbManual) {
            req[Constants.FIELD_26] = pin.length.toString().padStart(2, '0')
            req[Constants.FIELD_52] = PinUtils.encrypt(cardNo, pin, SpPos.pinPublicKey)
        }
        var f22_1_2 = "" //22域前2位数字
        when (readMethod.checkedRadioButtonId) {
            R.id.rbManual -> {
                req[Constants.FIELD_2] = cardNo
                req[Constants.FIELD_14] = expiryDate
                req[Constants.FIELD_48_1_CVV2] = cvv2
                f22_1_2 = "01"
                f22_3 = "2"
            }

            R.id.rbSwipeCard -> {
                req[Constants.FIELD_35] = TrackUtils.encrypt(track2, true)
                req[Constants.FIELD_45] = TrackUtils.encrypt(track1, true)
                f22_1_2 = "02"
            }

            R.id.rbInsertCard -> {
                req[Constants.FIELD_35] = track2
                req[Constants.FIELD_55] = iccData
                f22_1_2 = "05"
            }

            R.id.rbWaveCard -> {
                req[Constants.FIELD_35] = track2
                req[Constants.FIELD_55] = iccData
                f22_1_2 = "07"
            }

            else -> {}
        }
        req[Constants.FIELD_22] = f22_1_2 + f22_3
        if (req.contains(Constants.FIELD_55)) { //只有芯片信息里能拿到卡序列号
            req[Constants.FIELD_23] = TlvUtils.buildRemark(iccData)["PAN_SN"] ?: ""
        }
    }

    /*消费*/
    private fun sale() {
        showLoading()
        logger.clearLog()
        val req = HashMap<String, Any>()
        setOtherReq(req)
        req[Constants.FIELD_MTI] = "0200"
        req[Constants.FIELD_3] = "000000"
        req[Constants.FIELD_4] = DcsUtils.get12Amt()
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_1_CVV2] = cvv2
        req[Constants.FIELD_48_3_SETTLEMENT_MODE] = SpPos.settlementMode
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.sale(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                if (entity.isSuccess()) {
                    transMap[sale] = entity.data
                }
                cancelLoading()
            }
        })
    }

    /*离线*/
    private fun offline() {
        showLoading()
        logger.clearLog()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0220"
        req[Constants.FIELD_2] = cardNo
        req[Constants.FIELD_3] = "000000"
        req[Constants.FIELD_4] = DcsUtils.get12Amt()
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_14] = expiryDate
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_38] = DcsUtils.getRandomNum(6)
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_1_CVV2] = cvv2
        req[Constants.FIELD_48_3_SETTLEMENT_MODE] = SpPos.settlementMode
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.sale(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                if (entity.isSuccess()) {
                    transMap[sale] = entity.data
                }
                cancelLoading()
            }
        })
    }

    /*分期*/
    private fun ipp() {
        showLoading()
        logger.clearLog()
        val req = HashMap<String, Any>()
        setOtherReq(req)
        req[Constants.FIELD_MTI] = "0200"
        req[Constants.FIELD_3] = "000000"
        val amount = DcsUtils.getRandomAmt()
        req[Constants.FIELD_4] = amount.padStart(12, '0')
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_1_CVV2] = cvv2
        req[Constants.FIELD_48_3_SETTLEMENT_MODE] = SpPos.settlementMode
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        //todo
        val period = 3 //可设置3,6,9,12,24,36,48
        val parseAmount = DcsUtils.parsePeriodAmount(amount, period)
//        //"01"+首期金额(12)+期数(2)+费率(4)+利息(12)+非首期金额(12)+总金额(12) 01000000001466050000000000000000000000001300000000006666
        req[Constants.FIELD_63] = "01" +
                parseAmount[0].toDouble().times(100).toString().padStart(12, '0') +
                period.toString().padStart(2, '0') +
                "0000" +
                "000000000000" +
                parseAmount[1].toDouble().times(100).toString().padStart(12, '0') +
                amount.toDouble().times(100).toString().padStart(12, '0')
        DcsPay.instance.ipp(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                if (entity.isSuccess()) {
                    transMap[sale] = entity.data
                }
                cancelLoading()
            }
        })
    }

    /*撤销*/
    private fun void() {
        if (isNotExist(sale)) return
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0200"
        req[Constants.FIELD_3] = "020000"
        req[Constants.FIELD_4] = oriData.amount.padStart(12, '0')
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_60_3_ORI_MTI] = oriData.mti
        req[Constants.FIELD_60_4_ORI_TRACE_NO] = oriData.traceNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.void(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) transMap.remove(sale)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /*退款*/
    private fun refund() {
        if (isNotExist(sale)) return
        showLoading()
        val req = HashMap<String, Any>()
        setOtherReq(req) //和原交易一致
        req[Constants.FIELD_MTI] = "0200"
        req[Constants.FIELD_3] = "200000"
        req[Constants.FIELD_4] = oriData.amount.padStart(12, '0')
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_14] = oriData.expiryDate
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.refund(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                if (entity.isSuccess()) transMap.remove(sale)
                cancelLoading()
            }
        })
    }

    /*小费调整*/
    private fun tipAdjust() {
        if (isNotExist(sale)) return
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0220"
        req[Constants.FIELD_2] = oriData.cardNo
        req[Constants.FIELD_3] = "020000"
        req[Constants.FIELD_4] = (oriData.amount.toBigDecimal() + "100".toBigDecimal()).toString().padStart(12, '0')
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_14] = oriData.expiryDate
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_38] = oriData.authCode
        req[Constants.FIELD_39] = oriData.responseCode
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_54] = "100".padStart(12, '0') //增加小费:1SGD
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_60_3_ORI_MTI] = oriData.mti
        req[Constants.FIELD_60_4_ORI_TRACE_NO] = oriData.traceNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.tipAdjust(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                if (entity.isSuccess()) transMap.remove(sale)
                cancelLoading()
            }
        })
    }

    /*预授权*/
    private fun preAuth() {
        showLoading()
        logger.clearLog()
        val req = HashMap<String, Any>()
        setOtherReq(req)
        req[Constants.FIELD_MTI] = "0100"
        req[Constants.FIELD_3] = "300000"
        req[Constants.FIELD_4] = DcsUtils.get12Amt()
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_3_SETTLEMENT_MODE] = SpPos.settlementMode
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.preAuth(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) transMap[preAuth] = entity.data
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /*预授权撤销*/
    private fun preAuthVoid() {
        if (isNotExist(preAuth)) return
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0120"
        req[Constants.FIELD_3] = "020000"
        req[Constants.FIELD_4] = oriData.amount.padStart(12, '0')
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_60_3_ORI_MTI] = oriData.mti
        req[Constants.FIELD_60_4_ORI_TRACE_NO] = oriData.traceNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.preAuthVoid(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) transMap.remove(preAuth)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /*预授权完成离线*/
    private fun preAuthCaptureOffline() {
        if (isNotExist(preAuth)) return
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0220"
        req[Constants.FIELD_2] = cardNo
        req[Constants.FIELD_3] = "000000"
        req[Constants.FIELD_4] = oriData.amount
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_14] = expiryDate
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_38] = oriData.authCode
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_1_CVV2] = cvv2
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.preAuthCaptureOffline(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) transMap.remove(preAuth)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /*无卡交易类型状态列表查询*/
    private fun noCardList() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0780"
        req[Constants.FIELD_3] = "000000"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        DcsPay.instance.noCardTypeList(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                val wkList = DcsUtils.wkTypeList(entity.data.wkTypeList, isB2C = true) //true:扫一扫, false:二维码
                val codeType = wkList.map { it.transCode + " : " + it.transType }
                logger.logWarning("列表:$codeType")
                cancelLoading()
            }
        })
    }

    /*商户扫客户:POS扫一扫*/
    private fun b2c() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0700"
        req[Constants.FIELD_3] = "000000"
        req[Constants.FIELD_4] = DcsUtils.get12Amt()
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "67"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_3_SETTLEMENT_MODE] = SpPos.settlementMode
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = "10301" //从wkList获取该值
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_61_QR_CODE] = "请扫描客户二维码/条形码,拿到码值"
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.noCard(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    //
                } else if (entity.data.responseCode == "Q8") {
                    //查询
                    noCardQuery()
                }
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /*客户扫商户:POS生成二维码*/
    private fun c2b() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0700"
        req[Constants.FIELD_3] = "010000"
        req[Constants.FIELD_4] = DcsUtils.get12Amt()
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "67"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_3_SETTLEMENT_MODE] = SpPos.settlementMode
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = "10302" //从wkList获取该值
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_62_INV_NO] = DcsUtils.getInvoiceNo()
        DcsPay.instance.noCard(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                if (entity.isSuccess()) {
                    transMap["sale"] = entity.data
                    //第一步:生成二维码,让客户扫完支付
                    entity.data.qrCodeValue
                    //第二步:查询
//                    noCardQuery()
                }
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /**
     * 无卡查询
     * 若响应码为Q8, 则通常一直查询5分钟, 间隔时间:3/5/8秒一次
     */
    private fun noCardQuery() {
        if (isNotExist(sale)) return
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0700"
        req[Constants.FIELD_3] = "020000"
        req[Constants.FIELD_4] = oriData.amount
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = DcsUtils.paymentMethod(cardNo)
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_61_QR_CODE] = oriData.qrCodeValue
        req[Constants.FIELD_62_INV_NO] = oriData.invoiceNo
        DcsPay.instance.noCardQuery(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /**
     * 结算请求:商户结算方式必须是终端结算(若是系统结算,[结算请求,批上送,结算完成],这3个接口无需调用)
     */
    private fun settlementRequest() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0500"
        req[Constants.FIELD_3] = "920000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        //格式:消费总笔数(3)+消费总金额(12)+退款总笔数(3)+退款总金额(12) 001000000000100001000000000100
        req[Constants.FIELD_63] = "1".padStart(3, '0') +
                "100".padStart(12, '0') +
                "0".padStart(3, '0') +
                "0".padStart(12, '0')
        DcsPay.instance.settlementRequest(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /**
     * 批上送:当结算请求响应code为95时调用,需要上送的交易类型如下:
     *       有卡消费 (前提: 未被撤销/冲正/小费调整)
     *       分期消费 (前提: 未被撤销/冲正/小费调整)
     *       小费调整 (前提: 未被撤销/小费调整)
     *       离线消费 (前提: 未被撤销)
     *       预授权完成离线(前提: 未被撤销)
     *       无卡消费(主扫/被扫) (前提: 未被撤销)
     *       退货(前提:未被冲正)
     */
    private fun batchUpload() {
        if (isNotExist(sale)) return
        showLoading()
        val req = HashMap<String, Any>()
        setOtherReq(req)
        req[Constants.FIELD_MTI] = "0320"
        req[Constants.FIELD_3] = oriData.processingCode
        req[Constants.FIELD_4] = oriData.amount
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_22] = "012"
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_25] = "00"
        req[Constants.FIELD_37] = oriData.refNo
        req[Constants.FIELD_38] = oriData.authCode
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_48_2_ORI_AMT] = oriData.amount
        req[Constants.FIELD_49] = currencyCode
        req[Constants.FIELD_59] = oriData.transCode
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_60_3_ORI_MTI] = oriData.mti
        req[Constants.FIELD_60_4_ORI_TRACE_NO] = oriData.traceNo
        req[Constants.FIELD_62_INV_NO] = oriData.invoiceNo
        DcsPay.instance.batchUpload(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /**
     * 结算完成请求:批上送结束后调用
     */
    private fun settlementComplete() {
        showLoading()
        val req = HashMap<String, Any>()
        req[Constants.FIELD_MTI] = "0500"
        req[Constants.FIELD_3] = "960000"
        req[Constants.FIELD_11] = DcsUtils.getTransNo()
        req[Constants.FIELD_12] = DcsUtils.getHHmmss()
        req[Constants.FIELD_13] = DcsUtils.getMMdd()
        req[Constants.FIELD_24] = "100"
        req[Constants.FIELD_41] = tid
        req[Constants.FIELD_42] = mid
        req[Constants.FIELD_60_1_SN] = sn
        req[Constants.FIELD_60_2_BATCH_NO] = SpPos.batchNo
        req[Constants.FIELD_63] = oriData.qrCodeValue
        DcsPay.instance.noCardQuery(req, object : PayCallStateListener() {
            override fun onSuccessCall(result: String) {
                val entity = Gson().fromJson(result, ResultBean::class.java)
                showLogger(entity)
                cancelLoading()
            }
        })
    }

    /*显示Loading*/
    private var loadingDialog: LoadingDialog? = null
    private fun showLoading() {
        CoroutineScope(Dispatchers.Main).launch {
            if (loadingDialog == null) {
                loadingDialog = WidgetUtils.getLoadingDialog(this@MainActivity, "").setIconScale(0f)
                loadingDialog?.show()
            }
        }
    }

    /*隐藏Loading*/
    private fun cancelLoading(toastMsg: String? = null) {
        CoroutineScope(Dispatchers.Main).launch {
            loadingDialog?.cancel()
            loadingDialog = null
            toastMsg?.let { showToast(toastMsg) }
        }
    }

    private fun showMsg(msg: ResultBean.Data) {
        val newMsg = msg.toString().replace("Data(", "{\n\u3000").replace(", ", ",\n\u3000").replace(")", "\n}")
        CoroutineScope(Dispatchers.Main).launch {
            MaterialDialog.Builder(this@MainActivity)
                .title("提示:")
                .content(newMsg)
                .positiveText("确定")
                .onPositive { _, _ -> }
                .show()
        }
    }

    /*提示*/
    private fun showToast(msg: String) {
        XToastUtils.toast(msg)
    }

    /*显示日志*/
    private fun showLogger(responseBean: ResultBean) {
        val logStr = responseBean.toString().replace("ResultBean", "")
        if (responseBean.isSuccess()) logger.logSuccess(logStr) else logger.logError(logStr)
    }

    /*检测是否存在交易*/
    private fun isNotExist(type: String): Boolean {
        if (transMap[type] == null) {
            showToast("请先一笔做${if (type == sale) "消费" else "预授权"}")
            return true
        } else oriData = transMap[type]!!
        return false
    }

}