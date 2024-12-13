package com.example.iso8583.client.decoder

import com.example.iso8583.util.LogUtils
import java.io.IOException
import java.io.InputStream

class ShortSocketDecoder : AbstractSocketDecoder {
    private var maxMsgBytes = 10240

    constructor()
    constructor(maxMsgBytes: Int) {
        this.maxMsgBytes = maxMsgBytes
    }

    @Throws(Exception::class)
    override fun decode(inputStream: InputStream): ByteArray {
        val head = ByteArray(2)
        var packetLength = -1
        return try {
            packetLength = inputStream.read(head)
            if (packetLength != head.size) {
                LogUtils.e("读取错误：packetLength=%s", packetLength)
                throw IOException("读取包长度错误!")
            }
            packetLength = byte2Short(head)
            if (packetLength > maxMsgBytes) {
                throw IOException("packetLength > maxMsgBytes$packetLength>$maxMsgBytes")
            }
            super.readFromInputStreamByPacketLength(inputStream, packetLength)
        } catch (e: IOException) {
            LogUtils.e(e.toString())
            throw e
        }
    }

    private fun byte2Short(b: ByteArray): Int {
        return b[0].toInt() and 0xff shl 8 or (b[1].toInt() and 0xff)
    }
}