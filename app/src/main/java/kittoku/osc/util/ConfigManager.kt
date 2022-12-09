package kittoku.osc.util

import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import kittoku.osc.R
import kittoku.osc.dto.EConfigType
import kittoku.osc.dto.ServerConfig
import kittoku.osc.dto.ZzaConfig
import kittoku.osc.preference.OscPreference
import kittoku.osc.preference.accessor.setIntPrefValue
import kittoku.osc.preference.accessor.setStringPrefValue
import javax.net.ssl.SSLContext

object ConfigManager {

    /**
     * Import config form URI
     */
    fun importConfig(str: String?, deviceId: String, prefs: SharedPreferences): Int {
        try {
            if (str == null || TextUtils.isEmpty(str)) {
                return R.string.error_corrupted_uri
            }

            var definitiveUri = str.toString()
            if (definitiveUri.startsWith(EConfigType.ZZA.protocolScheme)) {
                definitiveUri = definitiveUri.replace(EConfigType.ZZA.protocolScheme, "")
                definitiveUri = Utils.decode(definitiveUri)
                if (TextUtils.isEmpty(definitiveUri)) {
                    return R.string.error_corrupted_uri
                }
            } else {
                return R.string.error_incorrect_protocol
            }

            // remove the older generation prefixes
            if (definitiveUri.startsWith(EConfigType.ZZA.protocolScheme)) {
                definitiveUri = definitiveUri.replace(EConfigType.VMESS.protocolScheme, "")
            }
            if (definitiveUri.startsWith(EConfigType.VMESS.protocolScheme)) {
                definitiveUri = definitiveUri.replace(EConfigType.VMESS.protocolScheme, "")
            }

            var result = Utils.decode(definitiveUri)
            if (TextUtils.isEmpty(result)) {
                return R.string.error_corrupted_uri
            }
            val zzaConfig = Gson().fromJson(result, ZzaConfig::class.java)


            // Although ZzaConfig fields are non null, looks like Gson may still create null fields
            if (TextUtils.isEmpty(zzaConfig.add)
                || TextUtils.isEmpty(zzaConfig.sstpport)
                || TextUtils.isEmpty(zzaConfig.id)
            ) {
                return R.string.error_incorrect_protocol
            }

            if (!zzaConfig.id.startsWith(deviceId)) {
                return R.string.error_incorrect_device
            }

            val serverConfig = ServerConfig.create()
            serverConfig?.server = zzaConfig.add
            serverConfig?.user = zzaConfig.id
            zzaConfig.sstpport.toIntOrNull()?.let { serverConfig?.port = it }
            serverConfig?.password = zzaConfig.password

            serverConfig?.let {
                applyConfig(it, prefs)
            }
        } catch (e: Exception) {
            return R.string.error_internal_error
        }

        return 0
    }

    private fun applyConfig(serverConfig: ServerConfig, prefs: SharedPreferences)
    {
        setStringPrefValue(serverConfig.server, OscPreference.HOME_HOSTNAME, prefs)
        setStringPrefValue(serverConfig.user, OscPreference.HOME_USERNAME, prefs)
        setStringPrefValue(serverConfig.password, OscPreference.HOME_PASSWORD, prefs)
        setIntPrefValue(serverConfig.port, OscPreference.SSL_PORT, prefs)
        setStringPrefValue(SSLContext.getDefault().supportedSSLParameters.protocols.last(), OscPreference.SSL_VERSION, prefs)
    }
}

