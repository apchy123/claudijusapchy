package com.claudijusapchy.ratprotection

import net.minecraft.network.chat.Component
import java.io.IOException
import java.net.*

object RatProxySelector : ProxySelector() {

    private var delegate: ProxySelector? = null
    private val suspiciousEndpoints = mutableListOf<String>()

    fun install(endpointList: List<String>) {
        suspiciousEndpoints.clear()
        suspiciousEndpoints.addAll(endpointList)
        delegate = ProxySelector.getDefault()
        ProxySelector.setDefault(this)
        ModLogger.info("[RatProtection] Installed. Blocking ${endpointList.size} endpoint patterns.")
        startLockThread()
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
                } catch (e: InterruptedException) {
                    break
                }
            }
        }, "RatProtection-Lock").apply {
            isDaemon = true
            start()
        }
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