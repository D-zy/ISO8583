package com.example.iso8583.client.encoder

class DefaultSocketEncoder : AbstractSocketEncoder() {
    @Throws(Exception::class)
    override fun encode(msg: ByteArray?): ByteArray? {
        val head = int2byte(msg!!.size)
        val message = ByteArray(head.size + msg.size)
        System.arraycopy(head, 0, message, 0, head.size)
        System.arraycopy(msg, 0, message, head.size, msg.size)
        return message
    }

    private fun int2byte(i: Int): ByteArray {
        val ret = ByteArray(4)
        ret[0] = (i shr 24).toByte()
        ret[1] = (i shr 16).toByte()
        ret[2] = (i shr 8).toByte()
        ret[3] = i.toByte()
        return ret
    }
}