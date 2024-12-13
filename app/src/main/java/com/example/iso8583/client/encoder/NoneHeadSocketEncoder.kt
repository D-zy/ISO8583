package com.example.iso8583.client.encoder

class NoneHeadSocketEncoder : AbstractSocketEncoder() {
    @Throws(Exception::class)
    override fun encode(msg: ByteArray?): ByteArray? {
        return msg
    }
}