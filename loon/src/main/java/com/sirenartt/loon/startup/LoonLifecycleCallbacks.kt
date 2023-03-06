package com.sirenartt.loon.startup

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.sirenartt.loon.core.AccessibilityLoon
import java.lang.ref.WeakReference

class LoonLifecycleCallbacks: Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {}
    override fun onActivityStarted(p0: Activity) {}
    override fun onActivityStopped(p0: Activity) {}
    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {}
    override fun onActivityDestroyed(p0: Activity) {}

    private var currentActivity: WeakReference<Activity>? = null

    init {
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!AccessibilityLoon.config.enabled) return
                val activity = currentActivity?.get()
                if (activity != null) {
                    val rootView = activity.window.decorView.rootView
                    AccessibilityLoon.performChecks(rootView)
                }
                handler.postDelayed(this, AccessibilityLoon.config.performCheckIntervalMs)
            }
        }, AccessibilityLoon.config.performCheckIntervalMs)

    }

    override fun onActivityResumed(p0: Activity) {
        currentActivity = WeakReference(p0)
    }

    override fun onActivityPaused(p0: Activity) {
        if (currentActivity?.get() == p0) {
            currentActivity = null
        }
    }


}