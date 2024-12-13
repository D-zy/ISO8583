package com.example.iso8583.client

import com.example.iso8583.client.decoder.ShortSocketDecoder
import com.example.iso8583.client.encoder.ShortSocketEncoder
import com.example.iso8583.client.msg.SocketMsg
import com.example.iso8583.enums.BizCode
import com.example.iso8583.util.BizException
import com.example.iso8583.util.LogUtils
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class BlockSocketClient : Thread() {

    private var isConnected = false
    private lateinit var socket: Socket
    private lateinit var outputStream: OutputStream
    private lateinit var inputStream: InputStream

    private lateinit var abstractCallback: AbstractCallback
    private lateinit var ip: String
    private var port: Int = 0
    private var connectTimeoutSeconds: Int = 60
    private var readTimeoutSeconds: Int = 60
    private lateinit var msg: ByteArray
    private val socketEncoder = ShortSocketEncoder()
    private val socketDecoder = ShortSocketDecoder()

    fun setSendData(ip: String, port: Int, msg: ByteArray, callback: AbstractCallback) {
        this.ip = ip
        this.port = port
        this.msg = msg
        this.abstractCallback = callback
    }

    @Throws(Throwable::class)
    override fun run() {
        try {
            val resp = sendAndRead(msg)
            if (resp.size < 10) throw BizException(BizCode.SDK0026)
            abstractCallback.decode(resp)
        } catch (b: BizException) {
            b.printStackTrace()
            LogUtils.d("network error. %s", b.msg)
            abstractCallback.decode(null, b)
        } catch (e: Throwable) {
            e.printStackTrace()
            LogUtils.d("network error. %s", e.message)
            abstractCallback.decode(null)
        }
    }


    @Throws(Throwable::class)
    private fun sendAndRead(msg: ByteArray): ByteArray {
        val sendFlag = send(msg)
        val ret: ByteArray
        if (!sendFlag) {
            close()
            throw IOException("send failed. msg.length=" + msg.size)
        } else {
            try {
                ret = read()
            } catch (e: Throwable) {
                throw BizException(BizCode.SDK0016)
            }
        }
        close()
        return ret
    }

    @Throws(Exception::class)
    private fun send(msg: ByteArray): Boolean {
        return try {
            if (!isConnected) {
                LogUtils.i("socket connect %s:%s", ip, port)
                socket = SslContextFactory.clientContext.socketFactory.createSocket()
                socket.let {
                    it.soTimeout = readTimeoutSeconds * 1000
                    it.reuseAddress = true
                    it.keepAlive = false
                    it.connect(InetSocketAddress(ip, port), connectTimeoutSeconds * 1000)
                }
                outputStream = socket.getOutputStream()
                inputStream = socket.getInputStream()
                isConnected = true
            }
            outputStream.write(socketEncoder.encode(msg))
            outputStream.flush()
            true
        } catch (e: Exception) {
            LogUtils.e(e.toString())
            close()
            throw e
        }
    }

    @Throws(Exception::class)
    private fun read(): ByteArray {
        return try {
            val ret = socketDecoder.decode(inputStream)
            val socketMsg = SocketMsg.serializeFromBytes(ret)
            if (socketMsg == null) {
                ret
            } else {
                socketMsg.msg
            }
        } catch (e: Exception) {
            LogUtils.e(e.toString())
            close()
            throw e
        }
    }

    private fun close() {
        try {
            outputStream.close()
            inputStream.close()
            socket.close()
        } catch (e: IOException) {
            LogUtils.e(e.toString())
        }
    }
}