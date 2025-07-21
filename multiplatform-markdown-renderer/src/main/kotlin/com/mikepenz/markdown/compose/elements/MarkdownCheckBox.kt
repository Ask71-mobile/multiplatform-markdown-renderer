package com.mikepenz.markdown.compose.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.LocalMarkdownFadeConfig
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.getTextInNode

@Composable
fun MarkdownCheckBox(
    content: String,
    node: ASTNode,
    style: TextStyle,
    checkedIndicator: @Composable (Boolean, Modifier) -> Unit = { checked, modifier ->
        //TODO: If needed add fade here
        val fadeConfig = LocalMarkdownFadeConfig.current
        MarkdownText(
            content = "[${if (checked) "x" else " "}] ",
            modifier = modifier,
            style = style.copy(fontFamily = FontFamily.Monospace)
        )
    },
) {
    val checked = node.getTextInNode(content).contains("[x]")
    Row {
        checkedIndicator(checked, Modifier.padding(end = 4.dp))
    }
}
