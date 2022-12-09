package kittoku.osc.preference.custom

import android.content.Context
import android.content.SharedPreferences
import android.text.InputType
import android.util.AttributeSet
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.Preference.SummaryProvider
import kittoku.osc.preference.OscPreference
import kittoku.osc.preference.accessor.getStringPrefValue


internal abstract class StringPreference(context: Context, attrs: AttributeSet) : EditTextPreference(context, attrs) {
    abstract val oscPreference: OscPreference
    abstract val preferenceTitle: String
    protected open val textType = InputType.TYPE_CLASS_TEXT
    protected open val dependingPreference: OscPreference? = null
    protected open val provider = SummaryProvider<Preference> {
        getStringPrefValue(oscPreference, it.sharedPreferences!!).ifEmpty {"[No Value Entered]"}
    }
    protected open val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == oscPreference.name) {
            text = getStringPrefValue(oscPreference, sharedPreferences!!)
            //summaryProvider = provider
        }
    }
    override fun onAttached() {
        super.onAttached()

        dependingPreference?.also {
            dependency = it.name
        }

        setOnBindEditTextListener { editText ->
            editText.inputType = textType
        }

        text = getStringPrefValue(oscPreference, sharedPreferences!!)
        title = preferenceTitle
        summaryProvider = provider
        sharedPreferences!!.registerOnSharedPreferenceChangeListener(listener)
    }
}

internal class HomeHostnamePreference(context: Context, attrs: AttributeSet) : StringPreference(context, attrs) {
    override val textType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    override val oscPreference = OscPreference.HOME_HOSTNAME
    override val preferenceTitle = "Server Name"
    override val provider = SummaryProvider<Preference> {
        if (getStringPrefValue(oscPreference, it.sharedPreferences!!).isEmpty())
            "[No Value Entered]"
        else "DD Server " + getStringPrefValue(oscPreference, it.sharedPreferences!!).toString().split(".").last()
    }
}

internal class HomeUsernamePreference(context: Context, attrs: AttributeSet) : StringPreference(context, attrs) {
    override val oscPreference = OscPreference.HOME_USERNAME
    override val preferenceTitle = "Username"
}

internal class ProxyHostnamePreference(context: Context, attrs: AttributeSet) : StringPreference(context, attrs) {
    override val oscPreference = OscPreference.PROXY_HOSTNAME
    override val preferenceTitle = "Proxy Server Hostname"
    override val dependingPreference = OscPreference.PROXY_DO_USE_PROXY
}

internal class ProxyUsernamePreference(context: Context, attrs: AttributeSet) : StringPreference(context, attrs) {
    override val oscPreference = OscPreference.PROXY_USERNAME
    override val preferenceTitle = "Proxy Username (optional)"
    override val dependingPreference = OscPreference.PROXY_DO_USE_PROXY
}

internal class DNSCustomAddressPreference(context: Context, attrs: AttributeSet) : StringPreference(context, attrs) {
    override val oscPreference = OscPreference.DNS_CUSTOM_ADDRESS
    override val preferenceTitle = "Custom DNS Server Address"
    override val dependingPreference = OscPreference.DNS_DO_USE_CUSTOM_SERVER

    override fun onAttached() {
        super.onAttached()

        dialogMessage = "NOTICE: packets associated with this address is routed to the VPN tunnel"
    }
}
