package com.mikepenz.markdown.compose.components

import androidx.compose.runtime.Composable
import com.mikepenz.markdown.compose.elements.MarkdownThinking
import com.mikepenz.markdown.model.ThinkingElementType
import com.mikepenz.markdown.utils.ThinkingCustomComponent
import org.intellij.markdown.IElementType
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.ast.getTextInNode

/**
 * Checks if the given element type and model represent a thinking block
 */
private fun isThinkingElement(elementType: IElementType, model: MarkdownComponentModel): Boolean {
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

/**
 * Creates markdown components with thinking tag support.
 * 
 * This is a convenience function that creates standard markdown components
 * with a custom handler for thinking blocks. Use this when you want to
 * manually control the component configuration while still having thinking support.
 * 
 * Example usage:
 * ```
 * val components = markdownComponentsWithThinking()
 * 
 * Markdown(
 *     content = processThinkingTags(content),
 *     components = components,
 *     colors = markdownColor(),
 *     typography = markdownTypography()
 * )
 * ```
 * 
 * @param text Custom text component
 * @param eol Custom end-of-line component
 * @param codeFence Custom code fence component
 * @param codeBlock Custom code block component
 * @param heading1 Custom heading 1 component
 * @param heading2 Custom heading 2 component
 * @param heading3 Custom heading 3 component
 * @param heading4 Custom heading 4 component
 * @param heading5 Custom heading 5 component
 * @param heading6 Custom heading 6 component
 * @param setextHeading1 Custom setext heading 1 component
 * @param setextHeading2 Custom setext heading 2 component
 * @param blockQuote Custom block quote component
 * @param paragraph Custom paragraph component
 * @param orderedList Custom ordered list component
 * @param unorderedList Custom unordered list component
 * @param image Custom image component
 * @param horizontalRule Custom horizontal rule component
 * @param table Custom table component
 * @param checkbox Custom checkbox component
 * @param customHandler Additional custom handler (will be chained with thinking handler)
 * @return MarkdownComponents with thinking tag support
 */
fun markdownComponentsWithThinking(
    text: MarkdownComponent = CurrentComponentsBridge.text,
    eol: MarkdownComponent = CurrentComponentsBridge.eol,
    codeFence: MarkdownComponent = CurrentComponentsBridge.codeFence,
    codeBlock: MarkdownComponent = CurrentComponentsBridge.codeBlock,
    heading1: MarkdownComponent = CurrentComponentsBridge.heading1,
    heading2: MarkdownComponent = CurrentComponentsBridge.heading2,
    heading3: MarkdownComponent = CurrentComponentsBridge.heading3,
    heading4: MarkdownComponent = CurrentComponentsBridge.heading4,
    heading5: MarkdownComponent = CurrentComponentsBridge.heading5,
    heading6: MarkdownComponent = CurrentComponentsBridge.heading6,
    setextHeading1: MarkdownComponent = CurrentComponentsBridge.setextHeading1,
    setextHeading2: MarkdownComponent = CurrentComponentsBridge.setextHeading2,
    blockQuote: MarkdownComponent = CurrentComponentsBridge.blockQuote,
    paragraph: MarkdownComponent = CurrentComponentsBridge.paragraph,
    orderedList: MarkdownComponent = CurrentComponentsBridge.orderedList,
    unorderedList: MarkdownComponent = CurrentComponentsBridge.unorderedList,
    image: MarkdownComponent = CurrentComponentsBridge.image,
    horizontalRule: MarkdownComponent = CurrentComponentsBridge.horizontalRule,
    table: MarkdownComponent = CurrentComponentsBridge.table,
    checkbox: MarkdownComponent = CurrentComponentsBridge.checkbox,
    customHandler: CustomMarkdownComponent? = null,
): MarkdownComponents = markdownComponents(
    text = text,
    eol = eol,
    codeFence = { model ->
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
        } else {
            // Fall back to the default code fence component
            codeFence.invoke(model)
        }
    },
    codeBlock = codeBlock,
    heading1 = heading1,
    heading2 = heading2,
    heading3 = heading3,
    heading4 = heading4,
    heading5 = heading5,
    heading6 = heading6,
    setextHeading1 = setextHeading1,
    setextHeading2 = setextHeading2,
    blockQuote = blockQuote,
    paragraph = paragraph,
    orderedList = orderedList,
    unorderedList = unorderedList,
    image = image,
    horizontalRule = horizontalRule,
    table = table,
    checkbox = checkbox,
    custom = customHandler
)