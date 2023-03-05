package com.sirenartt.loon.startup

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.sirenartt.loon.core.AccessibilityLoon

@Suppress("unused")
class LoonStartupInitializer : Initializer<LoonStartupInitializer> {

    private val loonLifecycleCallbacks = LoonLifecycleCallbacks()

    override fun create(context: Context) = apply {
        val application = context.applicationContext as Application

        if (AccessibilityLoon.config.enabled) {
            application.registerActivityLifecycleCallbacks(
                loonLifecycleCallbacks
            )
            Log.i(AccessibilityLoon.tag, "AccessibilityLoon initialization ready")
        }
    }
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}