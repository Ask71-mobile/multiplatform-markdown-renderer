package com.mikepenz.markdown.model

import org.intellij.markdown.IElementType

/**
 * Custom element type for thinking blocks in markdown.
 * This represents the processed thinking content that should be rendered as collapsible sections.
 */
object ThinkingElementType : IElementType("THINKING_BLOCK") {
    override fun toString(): String = "THINKING_BLOCK"
}