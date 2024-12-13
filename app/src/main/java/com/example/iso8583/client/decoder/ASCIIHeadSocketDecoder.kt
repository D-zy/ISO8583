package com.example.iso8583.client.decoder

import com.example.iso8583.util.ConvertUtil
import com.example.iso8583.util.LogUtils
import java.io.IOException
import java.io.InputStream

open class ASCIIHeadSocketDecoder(private val headBytesNum: Int) : AbstractSocketDecoder() {
    private val maxMsgBytes = 99999999

    @Throws(Exception::class)
    override fun decode(inputStream: InputStream): ByteArray {
        val head = ByteArray(headBytesNum)
        var packetLength = -1
        return try {
            packetLength = inputStream.read(head)
            if (packetLength != head.size) {
                LogUtils.e("读取错误：packetLength= %s", packetLength)
                throw IOException("读取包长度错误!")
            }
            packetLength = ConvertUtil.byte2HexString(head).toInt()
            LogUtils.i("packetLength={}", packetLength)
            if (packetLength > maxMsgBytes) {
                throw IOException("packetLength > maxMsgBytes$packetLength>$maxMsgBytes")
            }
            super.readFromInputStreamByPacketLength(inputStream, packetLength)
        } catch (e: IOException) {
            LogUtils.e(e.toString())
            throw e
        }
    }
}