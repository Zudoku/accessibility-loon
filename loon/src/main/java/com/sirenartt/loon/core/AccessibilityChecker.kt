package com.sirenartt.loon.core

import android.graphics.Bitmap
import android.view.View
import com.google.android.apps.common.testing.accessibility.framework.*
import com.google.android.apps.common.testing.accessibility.framework.uielement.AccessibilityHierarchyAndroid
import com.google.android.apps.common.testing.accessibility.framework.utils.contrast.BitmapImage
import com.google.common.collect.ImmutableSet

class AccessibilityChecker {

    fun runChecks(rootView: View): List<AccessibilityFinding> {
        val hierarchy: AccessibilityHierarchyAndroid = AccessibilityHierarchyAndroid
            .newBuilder(rootView)
            .build()

        val results: MutableList<AccessibilityHierarchyCheckResult> = ArrayList()
        val (parameters, bitmap) = buildParameters(rootView)

        for (check in getChecks()) {
            results.addAll(check.runCheckOnHierarchy(hierarchy, null, parameters))
        }

        bitmap?.recycle()

        val findings = AccessibilityCheckResultUtils.getResultsForTypes(
            results, getAccessibilityFindingResultTypes()
        )

        return findings.map {
            AccessibilityFinding.fromAccessibilityCheckResult(it)
        }
    }

    private fun getAccessibilityFindingResultTypes(): Set<AccessibilityCheckResult.AccessibilityCheckResultType> {
        return setOf(
            AccessibilityCheckResult.AccessibilityCheckResultType.WARNING,
            AccessibilityCheckResult.AccessibilityCheckResultType.ERROR,
            AccessibilityCheckResult.AccessibilityCheckResultType.INFO
        )
    }

    private fun buildParameters(rootView: View): Pair<Parameters, Bitmap?>  {
        val parameters = Parameters()

        return if (AccessibilityLoon.config.useScreenshotsForAccessibilityChecks) {
            val screenshotter = Screenshotter()
            val screenshot: Bitmap = screenshotter.getScreenshot(rootView)
            parameters.putScreenCapture(BitmapImage(screenshot))

            parameters to screenshot
        } else {
            parameters to null
        }
    }

    private fun getChecks(): ImmutableSet<AccessibilityHierarchyCheck> {
        return AccessibilityCheckPreset.getAccessibilityHierarchyChecksForPreset(
            AccessibilityLoon.config.accessibilityChecks
        )
    }
}