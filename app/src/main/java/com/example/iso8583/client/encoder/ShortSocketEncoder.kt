package com.example.iso8583.client.encoder

class ShortSocketEncoder : AbstractSocketEncoder() {
    @Throws(Exception::class)
    override fun encode(msg: ByteArray?): ByteArray? {
        val head = short2byte(msg!!.size)
        val message = ByteArray(head.size + msg.size)
        System.arraycopy(head, 0, message, 0, head.size)
        System.arraycopy(msg, 0, message, head.size, msg.size)
        return message
    }

    private fun short2byte(i: Int): ByteArray {
        val ret = ByteArray(2)
        ret[0] = (i shr 8).toByte()
        ret[1] = i.toByte()
        return ret
    }
}