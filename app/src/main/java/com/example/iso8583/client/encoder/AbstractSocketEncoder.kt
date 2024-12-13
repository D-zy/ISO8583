package com.example.iso8583.client.encoder

abstract class AbstractSocketEncoder {
    @Throws(Exception::class)
    abstract fun encode(msg: ByteArray?): ByteArray?
}