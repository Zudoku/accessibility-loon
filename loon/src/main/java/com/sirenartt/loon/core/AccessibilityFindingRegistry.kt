package com.sirenartt.loon.core

class AccessibilityFindingRegistry {
    private val findings = mutableListOf<AccessibilityFinding>()

    @Synchronized
    fun add(value: AccessibilityFinding) {
        findings.add(value)
    }

    @Synchronized
    fun addAll(values: List<AccessibilityFinding>) {
        findings.addAll(values)
    }

    @Synchronized
    fun filterExistingIssues(values: List<AccessibilityFinding>): List<AccessibilityFinding> {
        return values.filter { !findings.contains(it) }
    }
}