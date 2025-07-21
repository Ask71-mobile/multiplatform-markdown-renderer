package com.mikepenz.markdown.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.compose.components.MarkdownComponents
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.components.markdownComponentsWithThinking
import com.mikepenz.markdown.model.ImageTransformer
import com.mikepenz.markdown.model.MarkdownAnimations
import com.mikepenz.markdown.model.MarkdownAnnotator
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownDimens
import com.mikepenz.markdown.model.MarkdownExtendedSpans
import com.mikepenz.markdown.model.MarkdownInlineContent
import com.mikepenz.markdown.model.MarkdownPadding
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.NoOpImageTransformerImpl
import com.mikepenz.markdown.model.ReferenceLinkHandler
import com.mikepenz.markdown.model.ReferenceLinkHandlerImpl
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.markdownAnimations
import com.mikepenz.markdown.model.markdownAnnotator
import com.mikepenz.markdown.model.markdownDimens
import com.mikepenz.markdown.model.markdownExtendedSpans
import com.mikepenz.markdown.model.markdownInlineContent
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.utils.processThinkingTags
import com.mikepenz.markdown.utils.ThinkingCustomComponent
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

/**
 * Renders markdown content with support for collapsible `<thinking>` tags.
 * 
 * This composable automatically processes `<thinking>` tags in the content and renders them
 * as collapsible components. It supports both basic thinking tags and titled thinking tags.
 * 
 * Supported syntax:
 * - `<thinking>content</thinking>` - Creates a collapsible section titled "Thinking..."
 * - `<thinking title="Custom Title">content</thinking>` - Creates a collapsible section with custom title
 * - ````thinking` code fence - Alternative syntax that renders as collapsible
 * - ````thinking:Custom Title` code fence - Alternative syntax with custom title
 * 
 * Example usage:
 * ```
 * MarkdownWithThinking(
 *     content = """
 *         # My Document
 *         
 *         <thinking>
 *         This is my internal reasoning process.
 *         It will be rendered as a collapsible section.
 *         </thinking>
 *         
 *         Here's my final answer.
 *     """,
 *     colors = markdownColor(),
 *     typography = markdownTypography()
 * )
 * ```
 *
 * @param content The markdown content string with `<thinking>` tags
 * @param colors The color scheme for rendering
 * @param typography The typography settings
 * @param modifier The modifier to apply to the container
 * @param padding The padding configuration
 * @param dimens The dimension settings
 * @param flavour The markdown flavor descriptor
 * @param parser The markdown parser
 * @param imageTransformer The image transformer
 * @param annotator The markdown annotator
 * @param extendedSpans The extended spans configuration
 * @param inlineContent The inline content configuration
 * @param components The custom components (thinking support added automatically)
 * @param animations The animation configurations
 * @param referenceLinkHandler The reference link handler
 * @param fadeEffect Whether to enable fade effect
 * @param fadeLength The fade effect length
 * @param loading The loading composable
 * @param success The success composable
 * @param error The error composable
 */
@Composable
fun MarkdownWithThinking(
    content: String,
    colors: MarkdownColors,
    typography: MarkdownTypography,
    modifier: Modifier = Modifier.fillMaxSize(),
    padding: MarkdownPadding = markdownPadding(),
    dimens: MarkdownDimens = markdownDimens(),
    flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
    parser: MarkdownParser = MarkdownParser(flavour),
    imageTransformer: ImageTransformer = NoOpImageTransformerImpl(),
    annotator: MarkdownAnnotator = markdownAnnotator(),
    extendedSpans: MarkdownExtendedSpans = markdownExtendedSpans(),
    inlineContent: MarkdownInlineContent = markdownInlineContent(),
    components: MarkdownComponents = markdownComponents(),
    animations: MarkdownAnimations = markdownAnimations(),
    referenceLinkHandler: ReferenceLinkHandler = ReferenceLinkHandlerImpl(),
    fadeEffect: Boolean = false,
    fadeLength: Int = 20,
    loading: @Composable (modifier: Modifier) -> Unit = { androidx.compose.foundation.layout.Box(modifier) },
    success: @Composable (state: State.Success, components: MarkdownComponents, modifier: Modifier) -> Unit = { state, components, modifier ->
        MarkdownSuccess(state = state, components = components, modifier = modifier)
    },
    error: @Composable (modifier: Modifier) -> Unit = { androidx.compose.foundation.layout.Box(modifier) },
) {
    val processedContent = processThinkingTags(content)
    
    // Create components with custom thinking handler
    val componentsWithThinking = markdownComponentsWithThinking(
        customHandler = components.custom
    )
    
    Markdown(
        content = processedContent,
        colors = colors,
        typography = typography,
        modifier = modifier,
        padding = padding,
        dimens = dimens,
        flavour = flavour,
        parser = parser,
        imageTransformer = imageTransformer,
        annotator = annotator,
        extendedSpans = extendedSpans,
        inlineContent = inlineContent,
        components = componentsWithThinking,
        animations = animations,
        referenceLinkHandler = referenceLinkHandler,
        fadeEffect = fadeEffect,
        fadeLength = fadeLength,
        loading = loading,
        success = success,
        error = error
    )
}