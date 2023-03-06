package com.sirenartt.loon.core

import com.google.android.apps.common.testing.accessibility.framework.AccessibilityHierarchyCheckResult
import com.google.android.apps.common.testing.accessibility.framework.uielement.ViewHierarchyElement
import java.util.*

data class AccessibilityFinding(
    val title: String,
    val description: String,
    val target: String,
    val severity: String
) {
    companion object {
        fun fromAccessibilityCheckResult(checkResult: AccessibilityHierarchyCheckResult): AccessibilityFinding {
            return AccessibilityFinding(
                title = checkResult.getRawTitleMessage(Locale.ENGLISH),
                description = checkResult.getRawMessage(Locale.ENGLISH),
                target = getTarget(checkResult.element),
                severity = checkResult.type.toString()
            )
        }

        private fun getTarget(element: ViewHierarchyElement?): String {
            val resourceName = element?.resourceName
            return if (resourceName != null) {
                "[resourceName=${element.resourceName}, id=${element.id}, class=${element.className}]"
            } else if (element != null) {
                backupTarget(element)
            } else {
                "[unknown]"
            }
        }

        private fun backupTarget(element: ViewHierarchyElement): String {
            val parentName = findClosestParentName(element)

            return if (parentName != null) {
                "[resourceName=${element.resourceName}, id=${element.id}, class=${element.className}, closestParent=${parentName}]"
            } else {
                "[resourceName=${element.resourceName}, id=${element.id}, class=${element.className}]"
            }
        }

        private fun findClosestParentName(element: ViewHierarchyElement): String? {
            val parent = element.parentView
            return if (parent != null) {
                parent.resourceName ?: findClosestParentName(parent)
            } else {
                null
            }
        }
    }
}