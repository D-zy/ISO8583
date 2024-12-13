package com.example.iso8583.client.decoder

import java.io.ByteArrayOutputStream
import java.io.InputStream

class LineSocketDecoder : AbstractSocketDecoder() {

    @Throws(Exception::class)
    override fun decode(inputStream: InputStream): ByteArray {
        val swapStream = ByteArrayOutputStream()
        val buff = ByteArray(100)
        var rc = 0
        val lineBytes = "\r\n".toByteArray()
        while (inputStream.read(buff, 0, 100).also { rc = it } != -1) {
            swapStream.write(buff, 0, rc)
            val endByte = buff[rc - 1]
            if (endByte == lineBytes[0] || endByte == lineBytes[1]) {
                break
            }
        }
        return swapStream.toByteArray()
    }

}