package kittoku.osc.preference.accessor

import android.content.SharedPreferences
import kittoku.osc.preference.OscPreference


internal fun getBooleanPrefValue(key: OscPreference, prefs: SharedPreferences): Boolean {
    val defaultValue = when (key) {
        OscPreference.ROOT_STATE -> false
        OscPreference.HOME_CONNECTOR -> false
        OscPreference.SSL_DO_VERIFY -> false
        OscPreference.SSL_DO_ADD_CERT -> false
        OscPreference.SSL_DO_SELECT_SUITES -> false
        OscPreference.PROXY_DO_USE_PROXY -> false
        OscPreference.PPP_PAP_ENABLED -> true
        OscPreference.PPP_MSCHAPv2_ENABLED -> true
        OscPreference.PPP_IPv4_ENABLED -> true
        OscPreference.PPP_IPv6_ENABLED -> false
        OscPreference.DNS_DO_REQUEST_ADDRESS -> true
        OscPreference.DNS_DO_USE_CUSTOM_SERVER -> false
        OscPreference.ROUTE_DO_ADD_DEFAULT_ROUTE -> true
        OscPreference.ROUTE_DO_ROUTE_PRIVATE_ADDRESSES -> false
        OscPreference.ROUTE_DO_ADD_CUSTOM_ROUTES -> false
        OscPreference.ROUTE_DO_ENABLE_APP_BASED_RULE -> false
        OscPreference.RECONNECTION_ENABLED -> true
        OscPreference.LOG_DO_SAVE_LOG -> false
        else -> throw NotImplementedError()
    }

    return prefs.getBoolean(key.name, defaultValue)
}

internal fun setBooleanPrefValue(value: Boolean, key: OscPreference, prefs: SharedPreferences) {
    prefs.edit().also {
        it.putBoolean(key.name, value)
        it.apply()
    }
}
