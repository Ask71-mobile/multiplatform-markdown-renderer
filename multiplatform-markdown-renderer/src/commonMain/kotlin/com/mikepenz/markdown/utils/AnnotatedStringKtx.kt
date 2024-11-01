package com.mikepenz.markdown.utils

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.mikepenz.markdown.compose.LocalMarkdownAnnotator
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType
import org.intellij.markdown.flavours.gfm.GFMElementTypes
import org.intellij.markdown.flavours.gfm.GFMTokenTypes

@Composable
internal fun AnnotatedString.Builder.appendMarkdownLink(content: String, node: ASTNode) {
    val linkText = node.findChildOfType(MarkdownElementTypes.LINK_TEXT)?.children?.innerList()
    if (linkText == null) {
        append(node.getUnescapedTextInNode(content))
        return
    }
    val destination = node.findChildOfType(MarkdownElementTypes.LINK_DESTINATION)
        ?.getUnescapedTextInNode(content)
        ?.toString()
    val linkLabel = node.findChildOfType(MarkdownElementTypes.LINK_LABEL)
        ?.getUnescapedTextInNode(content)
    val annotation = destination ?: linkLabel
    if (annotation != null) pushStringAnnotation(MARKDOWN_TAG_URL, annotation)
    val linkColor = LocalMarkdownColors.current.linkText
    val linkTextStyle = LocalMarkdownTypography.current.link.copy(color = linkColor).toSpanStyle()
    pushStyle(linkTextStyle)
    buildMarkdownAnnotatedString(content, linkText)
    pop()
    if (annotation != null) pop()
}

@Composable
internal fun AnnotatedString.Builder.appendAutoLink(content: String, node: ASTNode) {
    val targetNode = node.children.firstOrNull {
        it.type.name == MarkdownElementTypes.AUTOLINK.name
    } ?: node
    val destination = targetNode.getUnescapedTextInNode(content)
    pushStringAnnotation(MARKDOWN_TAG_URL, (destination))
    val linkColor = LocalMarkdownColors.current.linkText
    val linkTextStyle = LocalMarkdownTypography.current.link.copy(color = linkColor).toSpanStyle()
    pushStyle(linkTextStyle)
    append(destination)
    pop()
}

/**
 * Builds an [AnnotatedString] with the contents of the given Markdown [ASTNode] node.
 *
 * This method automatically constructs the string with child components like:
 * - Paragraph
 * - Image
 * - Strong
 * - ...
 */
@Composable
fun AnnotatedString.Builder.buildMarkdownAnnotatedString(content: String, node: ASTNode) {
    buildMarkdownAnnotatedString(content, node.children)
}

/**
 * Builds an [AnnotatedString] with the contents of the given Markdown [ASTNode] node.
 *
 * This method automatically constructs the string with child components like:
 * - Paragraph
 * - Image
 * - Strong
 * - ...
 */
@Composable
fun AnnotatedString.Builder.buildMarkdownAnnotatedString(content: String, children: List<ASTNode>) {
    val annotator = LocalMarkdownAnnotator.current.annotate

    var skipIfNext: Any? = null
    children.forEach { child ->
        if (skipIfNext == null || skipIfNext != child.type) {
            if (annotator == null || !annotator(content, child)) {
                val parentType = child.parent?.type
                when (child.type) {
                    // Element types
                    MarkdownElementTypes.PARAGRAPH -> buildMarkdownAnnotatedString(content, child)
                    MarkdownElementTypes.IMAGE -> child.findChildOfTypeRecursive(
                        MarkdownElementTypes.LINK_DESTINATION
                    )?.let {
                        appendInlineContent(MARKDOWN_TAG_IMAGE_URL, it.getUnescapedTextInNode(content))
                    }

                    MarkdownElementTypes.EMPH -> {
                        pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                        buildMarkdownAnnotatedString(content, child)
                        pop()
                    }

                    MarkdownElementTypes.STRONG -> {
                        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                        buildMarkdownAnnotatedString(content, child)
                        pop()
                    }

                    GFMElementTypes.STRIKETHROUGH -> {
                        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                        buildMarkdownAnnotatedString(content, child)
                        pop()
                    }

                    MarkdownElementTypes.CODE_SPAN -> {
                        val codeStyle = LocalMarkdownTypography.current.inlineCode
                        pushStyle(
                            codeStyle.copy(
                                color = LocalMarkdownColors.current.inlineCodeText,
                                background = LocalMarkdownColors.current.inlineCodeBackground
                            ).toSpanStyle()
                        )
                        append(' ')
                        buildMarkdownAnnotatedString(content, child.children.innerList())
                        append(' ')
                        pop()
                    }

                    MarkdownElementTypes.AUTOLINK -> appendAutoLink(content, child)
                    MarkdownElementTypes.INLINE_LINK -> appendMarkdownLink(content, child)
                    MarkdownElementTypes.SHORT_REFERENCE_LINK -> appendMarkdownLink(content, child)
                    MarkdownElementTypes.FULL_REFERENCE_LINK -> appendMarkdownLink(content, child)

                    // Token Types
                    MarkdownTokenTypes.TEXT -> append(child.getUnescapedTextInNode(content))
                    GFMTokenTypes.GFM_AUTOLINK -> if (child.parent == MarkdownElementTypes.LINK_TEXT) {
                        append(child.getUnescapedTextInNode(content))
                    } else appendAutoLink(content, child)

                    MarkdownTokenTypes.SINGLE_QUOTE -> append('\'')
                    MarkdownTokenTypes.DOUBLE_QUOTE -> append('\"')
                    MarkdownTokenTypes.LPAREN -> append('(')
                    MarkdownTokenTypes.RPAREN -> append(')')
                    MarkdownTokenTypes.LBRACKET -> append('[')
                    MarkdownTokenTypes.RBRACKET -> append(']')
                    MarkdownTokenTypes.LT -> append('<')
                    MarkdownTokenTypes.GT -> append('>')
                    MarkdownTokenTypes.COLON -> append(':')
                    MarkdownTokenTypes.EXCLAMATION_MARK -> append('!')
                    MarkdownTokenTypes.BACKTICK -> append('`')
                    MarkdownTokenTypes.HARD_LINE_BREAK -> append("\n\n")
                    MarkdownTokenTypes.EMPH -> if (parentType != MarkdownElementTypes.EMPH && parentType != MarkdownElementTypes.STRONG) append('*')
                    MarkdownTokenTypes.EOL -> append('\n')
                    MarkdownTokenTypes.WHITE_SPACE -> if (length > 0) {
                        append(' ')
                    }

                    MarkdownTokenTypes.BLOCK_QUOTE -> {
                        skipIfNext = MarkdownTokenTypes.WHITE_SPACE
                    }
                }
            }
        } else {
            skipIfNext = null
        }
    }
}
