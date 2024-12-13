package com.example.iso8583.enums

/**
 * 交易类型 BizType
 */
enum class BizType(val key: String) {

    xf("type_xf"),      //消费
    lx("type_lx"),      //离线消费
    xffq("type_xffq"),  //分期消费
    xftz("type_xftz"),  //小费调整
    tk("type_tk"),      //消费退款
    cx("type_cx"),      //消费撤销
    cz("type_cz"),      // 冲正
    ysq("type_ysq"),    //预授权
    ysqwclx("type_ysqwclx"),//预授权完成离线
    ysqcx("type_ysqcx"), //预授权撤销
    ysqwc("type_ysqwc"), //预授权完成
    ysqwccx("type_ysqwccx"), //预授权完成
    xfcu("type_xfcu"), //交易认证上传
    echo_test("echo_test"), //交易认证上传
    pin_working("pin_working"),
    acknowledgement("acknowledgement"),
    certificate("certificate"),
    download_pin_key("download_pin_key"),
    download_rsa_key("download_rsa_key"),
    download_tmk("download_tmk"),

    zs("type_zs"),      //无卡主扫
    bs("type_bs"),      //无卡被扫
    wkcz("type_wkcz"),  //无卡冲正
    wkcx("type_wkcx"),  // 无卡撤销
    wktk("type_wktk"),  //无卡退款
    qm("type_qm"),      //签名
    js("type_js"),      //开始结算
    jswc("type_jswc"),  //结算完成
    pcss("type_pcss"),  //批次上送
    zmy("type_zmy"),    //主密钥
    qd("type_qd"),      //签到

}