package com.example.iso8583

import android.app.Application
import com.example.iso8583.req.DcsEnvironment
import com.example.iso8583.util.MmvkUtil

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        MmvkUtil.getInstance(this)

        //设置环境
        DcsEnvironment.setPayUrl("uat-pos.ezynet.sg:28998")
    }

    companion object {
        var instance: MyApp? = null

    }

}