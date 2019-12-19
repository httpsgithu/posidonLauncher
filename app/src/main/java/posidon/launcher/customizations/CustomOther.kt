/*
 * Copyright (c) 2019 Leo Shneyderis
 * All rights reserved
 */

package posidon.launcher.customizations

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import posidon.launcher.Main
import posidon.launcher.R
import posidon.launcher.tools.Settings
import posidon.launcher.tools.Tools
import kotlin.system.exitProcess


class CustomOther : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Tools.applyFontSetting(this)
        setContentView(R.layout.custom_other)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        findViewById<View>(R.id.settings).setPadding(0, 0, 0, Tools.navbarHeight)
        (findViewById<View>(R.id.hidestatus) as Switch).isChecked = Settings.getBool("hidestatus", false)
        (findViewById<View>(R.id.mnmlstatus) as Switch).isChecked = Settings.getBool("mnmlstatus", false)
        val hapticbar = findViewById<SeekBar>(R.id.hapticbar)
        hapticbar.progress = Settings.getInt("hapticfeedback", 14)
        hapticbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Settings.putInt("hapticfeedback", seekBar.progress)
                Tools.vibrate(this@CustomOther)
            }
        })
        findViewById<Spinner>(R.id.animationOptions).setSelection(when(Settings.getString("anim:app_open", "posidon")) {
            "scale_up" -> 2
            "clip_reveal" -> 1
            else -> 0
        })
    }

    override fun onPause() {
        Settings.putBool("hidestatus", (findViewById<View>(R.id.hidestatus) as Switch).isChecked)
        Settings.putBool("mnmlstatus", (findViewById<View>(R.id.mnmlstatus) as Switch).isChecked)
        Settings.putString("anim:app_open", when(findViewById<Spinner>(R.id.animationOptions).selectedItemPosition) {
            2 -> "scale_up"
            1 -> "clip_reveal"
            else -> "posidon"
        })
        Main.customized = true
        super.onPause()
    }

    fun openHideApps(v: View) { startActivity(Intent(this, CustomHiddenApps::class.java)) }
    fun stop(v: View) { exitProcess(0) }

    fun chooseLauncher(v: View) {
        val packageManager: PackageManager = packageManager
        val componentName = ComponentName(this, FakeLauncherActivity::class.java)
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP)
        val selector = Intent(Intent.ACTION_MAIN)
        selector.addCategory(Intent.CATEGORY_HOME)
        selector.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(selector)
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP)
    }
}