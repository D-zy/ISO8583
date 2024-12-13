package com.example.iso8583.client.encoder

open class ASCIIHeadSocketEncoder(private val headBytesNum: Int) : AbstractSocketEncoder() {
    @Throws(Exception::class)
    override fun encode(msg: ByteArray?): ByteArray? {
        var msgLength = msg!!.size.toString() + ""
        if (msgLength.length > headBytesNum) {
            throw Exception("报文头字节数过少，无法表示报文长度, headBytesNum=$headBytesNum, msgLength=$msgLength")
        }
        while (msgLength.length < headBytesNum) {
            msgLength = "0$msgLength"
        }
        val head = msgLength.toByteArray()
        val message = ByteArray(head.size + msg.size)
        System.arraycopy(head, 0, message, 0, head.size)
        System.arraycopy(msg, 0, message, head.size, msg.size)
        return message
    }
}