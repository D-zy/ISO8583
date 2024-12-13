package com.wbb.egtwo.iso8583.client.msg

/**
 * @Description: 系统使用的SocketMsgTag
 */
enum class SocketMsgTagEnum(var tagName: String) {
    TAG_BODY("BODY"),
    TAG_LOCAL_IP("LOCAL_IP"),
    TAG_REMOTE_IP("REMOTE_IP")

}