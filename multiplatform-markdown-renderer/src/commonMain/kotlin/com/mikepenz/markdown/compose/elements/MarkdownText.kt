package com.mikepenz.markdown.compose.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.mikepenz.markdown.annotator.AnnotatorSettings
import kotlin.math.pow
import com.mikepenz.markdown.annotator.annotatorSettings
import com.mikepenz.markdown.annotator.buildMarkdownAnnotatedString
import com.mikepenz.markdown.compose.LocalImageTransformer
import com.mikepenz.markdown.compose.LocalMarkdownAnimations
import com.mikepenz.markdown.compose.LocalMarkdownColors
import com.mikepenz.markdown.compose.LocalMarkdownFadeConfig
import com.mikepenz.markdown.compose.LocalMarkdownExtendedSpans
import com.mikepenz.markdown.compose.LocalMarkdownInlineContent
import com.mikepenz.markdown.compose.LocalMarkdownTypography
import com.mikepenz.markdown.compose.elements.material.MarkdownBasicText
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.drawBehind
import com.mikepenz.markdown.model.ImageTransformer
import com.mikepenz.markdown.model.MarkdownImageState
import com.mikepenz.markdown.model.PlaceholderConfig
import com.mikepenz.markdown.model.rememberMarkdownImageState
import com.mikepenz.markdown.utils.MARKDOWN_TAG_IMAGE_URL
import kotlinx.coroutines.delay
import org.intellij.markdown.IElementType
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.ast.findChildOfType


@Composable
fun MarkdownText(
    content: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text,
) {
    val fadeConfig = LocalMarkdownFadeConfig.current
    MarkdownText(
        content = AnnotatedString(content),
        modifier = modifier,
        style = style,
        fadeEffect = fadeConfig.enabled,
        fadeLength = fadeConfig.fadeLength
    )
}

@Composable
fun MarkdownText(
    content: String,
    node: ASTNode,
    style: TextStyle,
    modifier: Modifier = Modifier,
    contentChildType: IElementType? = null,
    annotatorSettings: AnnotatorSettings = annotatorSettings(),
) {
    val childNode = contentChildType?.run(node::findChildOfType) ?: node
    val styledText = buildAnnotatedString {
        pushStyle(style.toSpanStyle())
        buildMarkdownAnnotatedString(
            content = content,
            node = childNode,
            annotatorSettings = annotatorSettings
        )
        pop()
    }

    val fadeConfig = LocalMarkdownFadeConfig.current
    MarkdownText(
        content = styledText,
        modifier = modifier,
        style = style,
        fadeEffect = fadeConfig.enabled,
        fadeLength = fadeConfig.fadeLength
    )
}

@Composable
fun MarkdownText(
    content: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text,
    extendedSpans: ExtendedSpans? = LocalMarkdownExtendedSpans.current.extendedSpans?.invoke(),
    fadeEffect: Boolean = true,
    fadeLength: Int = 20,
) {
    // Apply fade effect to the content if requested
    val processedContent = if (fadeEffect) {
        val effectiveColor = if (style.color.isSpecified) style.color else Color.Black
        val animatedContent by animateFadeOutRemoval(content, effectiveColor, fadeLength)
        animatedContent
    } else {
        content
    }

    // extend the annotated string with `extended-spans` styles if provided
    val extendedStyledText = if (extendedSpans != null) {
        remember(processedContent) {
            extendedSpans.extend(processedContent)
        }
    } else {
        processedContent
    }

    // forward the `onTextLayout` to `extended-spans` if provided
    val onTextLayout: ((TextLayoutResult, Color?) -> Unit)? = if (extendedSpans != null) {
        { layoutResult, color ->
            extendedSpans.onTextLayout(layoutResult, color)
        }
    } else {
        null
    }

    // call drawBehind with the `extended-spans` if provided
    val extendedModifier = if (extendedSpans != null) {
        modifier.drawBehind(extendedSpans)
    } else modifier

    MarkdownText(
        content = extendedStyledText,
        modifier = extendedModifier,
        style = style,
        onTextLayout = onTextLayout
    )
}

@Composable
fun MarkdownText(
    content: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalMarkdownTypography.current.text,
    onTextLayout: ((TextLayoutResult, Color?) -> Unit)?,
) {
    val baseColor = LocalMarkdownColors.current.text
    val animations = LocalMarkdownAnimations.current
    val transformer = LocalImageTransformer.current
    val inlineContent = LocalMarkdownInlineContent.current

    val layoutResult: MutableState<TextLayoutResult?> = remember { mutableStateOf(null) }
    val imageState = rememberMarkdownImageState()

    val placeholderState by remember(imageState) {
        derivedStateOf {
            transformer.placeholderConfig(
                imageState.density,
                imageState.containerSize,
                imageState.intrinsicImageSize
            )
        }
    }

    MarkdownBasicText(
        text = content,
        modifier = modifier
            .onPlaced {
                it.parentLayoutCoordinates?.also { coordinates ->
                    imageState.updateContainerSize(coordinates.size.toSize())
                }
            }
            .let {
                // for backwards compatibility still check the `animate` property
                @Suppress("DEPRECATION")
                if (placeholderState.animate) animations.animateTextSize(it) else it
            },
        style = style,
        inlineContent = remember(inlineContent.inlineContent, placeholderState, transformer, imageState) {
            inlineContent.inlineContent + mapOf(
                MARKDOWN_TAG_IMAGE_URL to createImageInlineTextContent(
                    placeholderState,
                    transformer,
                    imageState
                )
            )
        },
        onTextLayout = {
            layoutResult.value = it
            onTextLayout?.invoke(it, baseColor)
        }
    )
}


fun createImageInlineTextContent(
    placeholderState: PlaceholderConfig,
    transformer: ImageTransformer,
    imageState: MarkdownImageState,
): InlineTextContent {
    return InlineTextContent(
        Placeholder(
            width = placeholderState.size.width.sp,
            height = placeholderState.size.height.sp,
            placeholderVerticalAlign = placeholderState.verticalAlign
        )
    ) { link ->
        transformer.transform(link)?.let { imageData ->
            val intrinsicSize = transformer.intrinsicSize(imageData.painter)
            LaunchedEffect(intrinsicSize) {
                imageState.updateImageSize(intrinsicSize)
            }
            Image(
                painter = imageData.painter,
                contentDescription = imageData.contentDescription,
                modifier = imageData.modifier,
                alignment = imageData.alignment,
                contentScale = imageData.contentScale,
                alpha = imageData.alpha,
                colorFilter = imageData.colorFilter
            )
        }
    }
}

/**
 * Animates the fade-out effect removal when streaming stops
 */
@Composable
private fun animateFadeOutRemoval(
    content: AnnotatedString,
    textColor: Color,
    fadeLength: Int = 20
): State<AnnotatedString> {
    val text = content.text
    if (text.isEmpty()) return remember { mutableStateOf(content) }

    // Track if content is still streaming
    var isStreaming by remember { mutableStateOf(true) }
    var lastContentLength by remember { mutableIntStateOf(text.length) }

    // Detect when streaming stops
    LaunchedEffect(text.length) {
        lastContentLength = text.length
        isStreaming = true
        delay(100) // Wait to see if more content arrives
        // Only set streaming to false if no new content arrived during the delay
        if (text.length == lastContentLength) {
            isStreaming = false
        }
    }

    // Animate fade multiplier: 1.0 (full fade) -> 0.0 (no fade) when streaming stops
    val fadeMultiplier by animateFloatAsState(
        targetValue = if (isStreaming) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 500),
        label = "fade_multiplier"
    )

    return remember(content, fadeMultiplier, fadeLength) {
        derivedStateOf {
            applyAnimatedFadeEffect(content, textColor, fadeLength, fadeMultiplier)
        }
    }
}

/**
 * Applies a fade effect with animated multiplier
 */
private fun applyAnimatedFadeEffect(
    content: AnnotatedString,
    textColor: Color,
    fadeLength: Int,
    fadeMultiplier: Float
): AnnotatedString {
    val text = content.text
    if (text.isEmpty()) return content

    // Find the last non-whitespace character
    val trimmedLength = text.trimEnd().length
    if (trimmedLength == 0) return content

    val actualFadeLength = minOf(trimmedLength, fadeLength)
    val fadeStartIndex = trimmedLength - actualFadeLength

    return buildAnnotatedString {
        // Copy the original text and styles up to fade start
        append(content.subSequence(0, fadeStartIndex))

        // Apply fade effect to each character individually
        for (i in 0 until actualFadeLength) {
            val charIndex = fadeStartIndex + i
            val baseFadeAlpha = 0.89f.pow(i.toFloat())

            // Calculate final alpha: interpolate between baseFadeAlpha and 1.0
            val finalAlpha = baseFadeAlpha + (1.0f - baseFadeAlpha) * (1.0f - fadeMultiplier)
            val fadeColor = textColor.copy(alpha = finalAlpha)

            withStyle(SpanStyle(color = fadeColor)) {
                append(text[charIndex])
            }
        }

        // Append any remaining text after trimmed content (whitespace)
        if (trimmedLength < text.length) {
            append(text.substring(trimmedLength))
        }
    }
}
