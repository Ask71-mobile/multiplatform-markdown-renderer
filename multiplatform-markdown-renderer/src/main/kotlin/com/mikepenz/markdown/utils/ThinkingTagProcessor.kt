package com.mikepenz.markdown.utils

import androidx.compose.runtime.Composable
import com.mikepenz.markdown.compose.components.MarkdownComponentModel
import com.mikepenz.markdown.compose.elements.MarkdownThinking
import com.mikepenz.markdown.model.ThinkingElementType
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

/**
 * Processes markdown content to convert <thinking> tags to code fences with "thinking" language.
 * This allows the thinking tags to be rendered as collapsible components.
 *
 * @param content The original markdown content
 * @return The processed markdown content with <thinking> tags converted to code fences
 */
fun processThinkingTags(content: String): String {
    return content
        .replace(
            regex = Regex("""<thinking\s+title="([^"]*)">([\s\S]*?)</thinking>""", RegexOption.IGNORE_CASE),
            replacement = "```thinking:$1\n$2\n```"
        )
        .replace(
            regex = Regex("""<thinking>([\s\S]*?)</thinking>""", RegexOption.IGNORE_CASE),
            replacement = "```thinking\n$1\n```"
        )
}

/**
 * Extracts the title from a thinking code fence language string.
 * For example: "thinking:Custom Title" -> "Custom Title"
 *
 * @param language The language string from the code fence
 * @return The extracted title or "Thinking..." as default
 */
fun extractThinkingTitle(language: String?): String {
    return when {
        language == null -> "Thinking..."
        language == "thinking" -> "Thinking..."
        language.startsWith("thinking:") -> language.substring(9).trim()
        else -> "Thinking..."
    }
}

/**
 * Custom markdown component handler for thinking blocks.
 * This is the proper way to handle custom elements using the CustomMarkdownComponent typealias.
 *
 * @param elementType The element type being processed
 * @param model The markdown component model containing content and styling
 */
@Composable
fun ThinkingCustomComponent(
    elementType: IElementType,
    model: MarkdownComponentModel
) {
    when (elementType) {
        MarkdownElementTypes.CODE_FENCE -> {
            // Check if this is a thinking code fence
            val language = model.node.findChildOfType(MarkdownTokenTypes.FENCE_LANG)?.getTextInNode(model.content)?.toString()
            if (language == "thinking" || language?.startsWith("thinking:") == true) {
                // Extract the actual code content
                val codeContent = extractCodeFenceContent(model.content, model.node)
                MarkdownThinking(
                    content = codeContent,
                    language = language,
                    style = model.typography.code
                )
            }
        }
        ThinkingElementType -> {
            // Handle direct thinking elements (for future extensions)
            MarkdownThinking(
                content = model.content,
                style = model.typography.code
            )
        }
    }
}

/**
 * Checks if the given element type and model represent a thinking block
 */
fun isThinkingElement(elementType: IElementType, model: MarkdownComponentModel): Boolean {
    return when (elementType) {
        MarkdownElementTypes.CODE_FENCE -> {
            val language = model.node.findChildOfType(MarkdownTokenTypes.FENCE_LANG)?.getTextInNode(model.content)?.toString()
            language == "thinking" || language?.startsWith("thinking:") == true
        }
        ThinkingElementType -> true
        else -> false
    }
}

/**
 * Extracts the content from a code fence node.
 */
private fun extractCodeFenceContent(content: String, node: ASTNode): String {
    if (node.children.size >= 3) {
        val start = node.children[2].startOffset
        val minCodeFenceCount = if (node.findChildOfType(MarkdownTokenTypes.FENCE_LANG) != null && node.children.size > 3) 3 else 2
        val end = node.children[(node.children.size - 2).coerceAtLeast(minCodeFenceCount)].endOffset
        return content.subSequence(start, end).toString().replaceIndent()
    }
    return ""
}