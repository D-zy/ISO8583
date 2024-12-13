package com.example.iso8583.client.encoder

class LineSocketEncoder : AbstractSocketEncoder() {
    @Throws(Exception::class)
    override fun encode(msg: ByteArray?): ByteArray? {
        val lineBytes = "\r\n".toByteArray()
        val message = ByteArray(msg!!.size + lineBytes.size)
        System.arraycopy(msg, 0, message, 0, msg.size)
        System.arraycopy(lineBytes, 0, message, msg.size, lineBytes.size)
        return message
    }
}