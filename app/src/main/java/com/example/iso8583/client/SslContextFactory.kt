package com.example.iso8583.client

import com.example.iso8583.MyApp
import com.example.iso8583.R
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/*
 * 方式1:  https://blog.csdn.net/nadeal/article/details/107413987
证书类文件转换
* pem转换为cer
openssl x509 -inform pem -in cacert.pem -outform der -out cacert.cer

* crt转换为pem
openssl x509 -in cacert.crt -out cacert.pem

* crt转换为cer
openssl x509 -in cacert.crt -out cacert.cer -outform der
————————————————

 * 方式2:  https://blog.csdn.net/thekenofDIS/article/details/132871697  转jks
keytool -import -trustcacerts -alias root -file rootCA.pem -keystore test2.jks
*
*
 *   https://blog.csdn.net/weixin_43192102/article/details/122214603  Java实现SSL Socket长连接
 */
object SslContextFactory {
    private const val TLS = "TLSv1.2"
    private const val PROVIDER = "SunX509"
    private const val STORE_TYPE = "JKS"

    @JvmStatic
    val clientContext: SSLContext
        get() {
            val sslContext: SSLContext
            try {
                sslContext = SSLContext.getInstance(TLS)
                //生成信任证书Manager,默认系统会信任CA机构颁发的证书,自定的证书需要手动的加载 
                val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null)
                //生成验证工厂
                val certificateFactory = CertificateFactory.getInstance("X.509")
                //生成别名(可以随便填写)
                val certificateAlias = Integer.toString(0)
                //加载证书
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(MyApp.instance!!.resources.openRawResource(R.raw.uat_pos)))

                //初始化
                trustManagerFactory.init(keyStore)
                sslContext.init(null, trustManagerFactory.trustManagers, null)
            } catch (e: Exception) {
                throw Error(e)
            }
            return sslContext
        }
}