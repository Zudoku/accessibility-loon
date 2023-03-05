package com.sirenartt.loon.core

import android.view.View
import com.google.android.apps.common.testing.accessibility.framework.AccessibilityCheckPreset
import com.sirenartt.loon.reporting.AccessibilityReporter
import java.util.concurrent.TimeUnit

object AccessibilityLoon {
    val registry: AccessibilityFindingRegistry = AccessibilityFindingRegistry()

    var config : AccessibilityLoonConfig = getDefaultConfig()

    private fun getDefaultConfig(): AccessibilityLoonConfig {
        return AccessibilityLoonConfig(
            enabled = true,
            useScreenshotsForAccessibilityChecks = true,
            performCheckIntervalMs = TimeUnit.SECONDS.toMillis(3),
            accessibilityChecks = AccessibilityCheckPreset.LATEST
        )
    }

    @Synchronized
    fun performChecks(rootView: View) {
        val checker = AccessibilityChecker()
        val findings = checker.runChecks(rootView)

        val newDistinctFindings = registry.filterExistingIssues(findings)

        if (newDistinctFindings.isNotEmpty()) {
            registry.addAll(newDistinctFindings)

            val reporter = AccessibilityReporter()
            reporter.report(newDistinctFindings)
        }
    }

    internal val tag = "AccessibilityLoon"
}

data class AccessibilityLoonConfig(
    val enabled: Boolean,
    val useScreenshotsForAccessibilityChecks: Boolean,
    val performCheckIntervalMs: Long,
    val accessibilityChecks: AccessibilityCheckPreset
)
