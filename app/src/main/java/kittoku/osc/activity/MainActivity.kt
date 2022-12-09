package kittoku.osc.activity

import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kittoku.osc.BuildConfig
import kittoku.osc.R
import kittoku.osc.databinding.ActivityMainBinding
import kittoku.osc.fragment.HomeFragment
import kittoku.osc.fragment.SettingFragment
import kittoku.osc.util.ConfigManager
import kittoku.osc.util.Utils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "${getString(R.string.app_name)}: ${BuildConfig.VERSION_NAME}"
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        object : FragmentStateAdapter(this) {
            private val homeFragment = HomeFragment()

            private val settingFragment = SettingFragment()

            override fun getItemCount() = 2

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> homeFragment
                    1 -> settingFragment
                    else -> throw NotImplementedError()
                }
            }
        }.also {
            binding.pager.adapter = it
        }

        TabLayoutMediator(binding.tabBar, binding.pager) { tab, position ->
            tab.text = when (position) {
                0 -> "HOME"
                1 -> "SETTING"
                else -> throw NotImplementedError()
            }
        }.attach()

        binding.importBtn.setOnClickListener {
            val str = Utils.getClipboard(this)
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val res = ConfigManager.importConfig(str, deviceId, PreferenceManager.getDefaultSharedPreferences(this))
            var toastText = R.string.toast_import_successful
            val duration = Toast.LENGTH_SHORT
            if (res != 0) {
                toastText = res
            }
            var toast = Toast.makeText(applicationContext, toastText, duration)
            toast.show()
        }

        binding.exportDeviceIdBtn.setOnClickListener {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            Utils.setClipboard(this, deviceId)
        }

    }
}
