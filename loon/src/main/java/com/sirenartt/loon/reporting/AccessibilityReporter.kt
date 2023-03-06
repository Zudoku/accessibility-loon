package com.sirenartt.loon.reporting

import android.util.Log
import com.sirenartt.loon.core.AccessibilityFinding
import com.sirenartt.loon.core.AccessibilityLoon

class AccessibilityReporter {

    fun report(findings: List<AccessibilityFinding>) {
        findings.forEach {
            log(it)
        }
    }

    private fun log(finding: AccessibilityFinding) {
        Log.w(AccessibilityLoon.tag, "--- [AccessibilityLoon] Found accessibility problem ---")
        Log.w(AccessibilityLoon.tag, "View: ${finding.target}")
        Log.w(AccessibilityLoon.tag, "Severity: ${finding.severity}")
        Log.w(AccessibilityLoon.tag, "Problem: ${finding.title}")
        Log.w(AccessibilityLoon.tag, "Description: ${finding.description}")
        Log.w(AccessibilityLoon.tag, "-------------------------------------------------------")
    }
}