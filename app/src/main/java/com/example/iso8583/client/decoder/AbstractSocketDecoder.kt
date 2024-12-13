package com.example.iso8583.client.decoder

import com.example.iso8583.util.LogUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

abstract class AbstractSocketDecoder {

    @Throws(Exception::class)
    abstract fun decode(inputStream: InputStream): ByteArray

    @Throws(IOException::class)
    protected fun readFromInputStreamByPacketLength(inputStream: InputStream, packetLength: Int): ByteArray {
        val swapStream = ByteArrayOutputStream()
        val buff = ByteArray(1024)
        var lengthAll = 0
        var length = 0
        while (lengthAll < packetLength && inputStream.read(buff, 0, buff.size).also { length = it } != -1) {
            swapStream.write(buff, 0, length)
            lengthAll += length
            LogUtils.d("length=%s, lengthAll=%s", length, lengthAll)
        }
        LogUtils.i("receive: msgLength=%s", lengthAll)
        return swapStream.toByteArray()
    }
}