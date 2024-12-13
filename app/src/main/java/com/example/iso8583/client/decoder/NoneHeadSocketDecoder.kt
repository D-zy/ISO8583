package com.example.iso8583.client.decoder

import java.io.ByteArrayOutputStream
import java.io.InputStream

class NoneHeadSocketDecoder : AbstractSocketDecoder() {
    @Throws(Exception::class)
    override fun decode(inputStream: InputStream): ByteArray {
        val swapStream = ByteArrayOutputStream()
        val buff = ByteArray(100)
        var rc = 0
        while (inputStream.read(buff, 0, 100).also { rc = it } != -1) {
            swapStream.write(buff, 0, rc)
        }
        return swapStream.toByteArray()
    }
}