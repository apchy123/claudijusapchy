package com.claudijusapchy.ratprotection

import java.io.IOException
import java.net.*
import javax.net.ssl.*

object RatProxySelector : ProxySelector() {

    private var delegate: ProxySelector? = null
    private val suspiciousEndpoints = mutableListOf<String>()

    fun install(endpointList: List<String>) {
        suspiciousEndpoints.clear()
        suspiciousEndpoints.addAll(endpointList)
        delegate = ProxySelector.getDefault()
        ProxySelector.setDefault(this)
        installSSLContext()
        startLockThread()
        ModLogger.info("[RatProtection] Installed. Blocking ${endpointList.size} endpoint patterns.")
    }

    fun checkUrl(host: String) {
        if (isSuspicious(host)) {
            ModLogger.block("[RatProtection] BLOCKED: $host")
            throw SecurityException("[RatProtection] Connection blocked: $host")
        }
    }

    private fun installSSLContext() {
        val baseFactory = SSLContext.getDefault().socketFactory
        val blockingFactory = object : SSLSocketFactory() {
            override fun getDefaultCipherSuites() = baseFactory.defaultCipherSuites
            override fun getSupportedCipherSuites() = baseFactory.supportedCipherSuites
            override fun createSocket(host: String, port: Int): Socket { checkUrl(host); return baseFactory.createSocket(host, port) }
            override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int) = baseFactory.createSocket(host, port, localHost, localPort)
            override fun createSocket(host: InetAddress, port: Int) = baseFactory.createSocket(host, port)
            override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int) = baseFactory.createSocket(address, port, localAddress, localPort)
            override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket { checkUrl(host); return baseFactory.createSocket(s, host, port, autoClose) }
        }
        val ctx = SSLContext.getInstance("TLS")
        ctx.init(null, null, null)
        SSLContext.setDefault(ctx)
        HttpsURLConnection.setDefaultSSLSocketFactory(blockingFactory)
    }

    private fun startLockThread() {
        Thread({
            while (true) {
                try {
                    Thread.sleep(3000)
                    if (ProxySelector.getDefault() !== this) {
                        ModLogger.warn("[RatProtection] ProxySelector was swapped! Reinstalling...")
                        ProxySelector.setDefault(this)
                    }
                } catch (e: InterruptedException) { break }
            }
        }, "RatProtection-Lock").apply { isDaemon = true; start() }
    }

    override fun select(uri: URI): List<Proxy> {
        val url = uri.toString()
        if (isSuspicious(url)) {
            ModLogger.block("[RatProtection] BLOCKED: $url")
            throw SecurityException("[RatProtection] Connection blocked: $url")
        }
        return delegate?.select(uri) ?: listOf(Proxy.NO_PROXY)
    }

    override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) {
        delegate?.connectFailed(uri, sa, ioe)
    }

    private fun isSuspicious(url: String): Boolean =
        suspiciousEndpoints.any { url.contains(it, ignoreCase = true) }
}