package com.mikepenz.markdown.compose.elements

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownDimens
import com.mikepenz.markdown.compose.LocalMarkdownPadding
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.utils.extractThinkingTitle

@Composable
fun MarkdownThinking(
    content: String,
    language: String? = null,
    style: TextStyle = LocalMarkdownTypography.current.code,
    title: String = extractThinkingTitle(language)
) {
    var expanded by remember { mutableStateOf(false) }
    
    val backgroundColor = LocalMarkdownColors.current.codeBackground
    val cornerSize = LocalMarkdownDimens.current.codeBackgroundCornerSize
    val padding = LocalMarkdownPadding.current.codeBlock
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize()
    ) {
        // Header with expand/collapse button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cornerSize))
                .background(
                    color = backgroundColor.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(cornerSize)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = style.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                contentDescription = if (expanded) "Collapse thinking" else "Expand thinking",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Collapsible content
        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 0.dp,
                            bottomStart = cornerSize,
                            bottomEnd = cornerSize
                        )
                    )
                    .padding(padding)
            ) {
                Markdown(
                    content = content,
                    colors = DefaultMarkdownColors(
                        text = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        codeBackground = LocalMarkdownColors.current.codeBackground,
                        inlineCodeBackground = LocalMarkdownColors.current.inlineCodeBackground,
                        dividerColor = LocalMarkdownColors.current.dividerColor,
                        tableBackground = LocalMarkdownColors.current.tableBackground
                    ),
                    typography = DefaultMarkdownTypography(
                        h1 = style.copy(fontSize = style.fontSize * 1.1f),
                        h2 = style.copy(fontSize = style.fontSize * 1.05f),
                        h3 = style.copy(fontSize = style.fontSize * 1.0f),
                        h4 = style.copy(fontSize = style.fontSize * 0.95f),
                        h5 = style.copy(fontSize = style.fontSize * 0.9f),
                        h6 = style.copy(fontSize = style.fontSize * 0.9f),
                        text = style.copy(fontSize = style.fontSize * 0.9f),
                        code = style.copy(fontSize = style.fontSize * 0.85f),
                        inlineCode = style.copy(fontSize = style.fontSize * 0.85f),
                        quote = style.copy(fontSize = style.fontSize * 0.9f),
                        paragraph = style.copy(fontSize = style.fontSize * 0.9f),
                        ordered = style.copy(fontSize = style.fontSize * 0.9f),
                        bullet = style.copy(fontSize = style.fontSize * 0.9f),
                        list = style.copy(fontSize = style.fontSize * 0.9f),
                        textLink = LocalMarkdownTypography.current.textLink,
                        table = style.copy(fontSize = style.fontSize * 0.9f)
                    ),
                    components = markdownComponents(), // Use standard components to avoid recursion
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}