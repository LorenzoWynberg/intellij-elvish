package com.elvish.plugin.editor

import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.lang.ASTNode
import com.intellij.lang.folding.FoldingBuilderEx
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.FoldingGroup
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/**
 * Folding builder for Elvish files.
 *
 * Provides folding for:
 * - Function definitions: fn name {|args| ... } folds to fn name {...}
 * - Lambda expressions: {|args| ... } folds to {...}
 * - Control flow blocks: if/for/while/try blocks
 * - Multi-line lists: [a b c ...] across lines
 * - Multi-line maps: [&key=value ...] across lines
 */
class ElvishFoldingBuilder : FoldingBuilderEx(), DumbAware {

    override fun buildFoldRegions(
        root: PsiElement,
        document: Document,
        quick: Boolean
    ): Array<FoldingDescriptor> {
        val descriptors = mutableListOf<FoldingDescriptor>()

        collectFoldRegions(root, document, descriptors)

        return descriptors.toTypedArray()
    }

    private fun collectFoldRegions(
        element: PsiElement,
        document: Document,
        descriptors: MutableList<FoldingDescriptor>
    ) {
        val node = element.node ?: return
        val tokenType = node.elementType

        when (tokenType) {
            ElvishTokenTypes.LBRACE -> {
                // Find matching closing brace and create fold region
                val foldRange = findMatchingBraceRange(element)
                if (foldRange != null && isMultiLine(foldRange, document)) {
                    val context = determineBraceContext(element)
                    val placeholderText = getPlaceholderForBrace(context)
                    val group = FoldingGroup.newGroup("elvish-brace-${element.textOffset}")
                    descriptors.add(FoldingDescriptor(node, foldRange, group, placeholderText))
                }
            }
            ElvishTokenTypes.LBRACKET -> {
                // Find matching closing bracket for lists/maps
                val foldRange = findMatchingBracketRange(element)
                if (foldRange != null && isMultiLine(foldRange, document)) {
                    val isMap = isMapLiteral(element)
                    val placeholderText = if (isMap) "[&...]" else "[...]"
                    val group = FoldingGroup.newGroup("elvish-bracket-${element.textOffset}")
                    descriptors.add(FoldingDescriptor(node, foldRange, group, placeholderText))
                }
            }
        }

        // Recursively process children
        var child = element.firstChild
        while (child != null) {
            collectFoldRegions(child, document, descriptors)
            child = child.nextSibling
        }
    }

    private fun findMatchingBraceRange(openBrace: PsiElement): TextRange? {
        var depth = 1
        var current = openBrace.nextSibling

        while (current != null) {
            val tokenType = current.node?.elementType
            when (tokenType) {
                ElvishTokenTypes.LBRACE -> depth++
                ElvishTokenTypes.RBRACE -> {
                    depth--
                    if (depth == 0) {
                        return TextRange(openBrace.textOffset, current.textOffset + current.textLength)
                    }
                }
            }
            current = current.nextSibling
        }

        return null
    }

    private fun findMatchingBracketRange(openBracket: PsiElement): TextRange? {
        var depth = 1
        var current = openBracket.nextSibling

        while (current != null) {
            val tokenType = current.node?.elementType
            when (tokenType) {
                ElvishTokenTypes.LBRACKET -> depth++
                ElvishTokenTypes.RBRACKET -> {
                    depth--
                    if (depth == 0) {
                        return TextRange(openBracket.textOffset, current.textOffset + current.textLength)
                    }
                }
            }
            current = current.nextSibling
        }

        return null
    }

    private fun isMultiLine(range: TextRange, document: Document): Boolean {
        val startLine = document.getLineNumber(range.startOffset)
        val endLine = document.getLineNumber(range.endOffset)
        return endLine > startLine
    }

    /**
     * Determines the context of a brace to provide appropriate placeholder text.
     */
    private enum class BraceContext {
        FUNCTION,      // fn name { ... }
        LAMBDA,        // { |args| ... } or just { ... }
        IF_BLOCK,      // if cond { ... }
        ELIF_BLOCK,    // elif cond { ... }
        ELSE_BLOCK,    // else { ... }
        FOR_BLOCK,     // for x container { ... }
        WHILE_BLOCK,   // while cond { ... }
        TRY_BLOCK,     // try { ... }
        CATCH_BLOCK,   // catch e { ... }
        FINALLY_BLOCK, // finally { ... }
        GENERIC        // unknown context
    }

    private fun determineBraceContext(openBrace: PsiElement): BraceContext {
        // Look backwards to find the keyword that precedes this brace
        var prev = openBrace.prevSibling

        // Skip whitespace and other tokens to find the relevant keyword
        while (prev != null) {
            val tokenType = prev.node?.elementType

            // Skip whitespace and newlines
            if (tokenType == ElvishTokenTypes.WHITE_SPACE ||
                tokenType == ElvishTokenTypes.NEWLINE) {
                prev = prev.prevSibling
                continue
            }

            // Check for control flow keywords
            when (tokenType) {
                ElvishTokenTypes.FN -> return BraceContext.FUNCTION
                ElvishTokenTypes.IF -> return BraceContext.IF_BLOCK
                ElvishTokenTypes.ELIF -> return BraceContext.ELIF_BLOCK
                ElvishTokenTypes.ELSE -> return BraceContext.ELSE_BLOCK
                ElvishTokenTypes.FOR -> return BraceContext.FOR_BLOCK
                ElvishTokenTypes.WHILE -> return BraceContext.WHILE_BLOCK
                ElvishTokenTypes.TRY -> return BraceContext.TRY_BLOCK
                ElvishTokenTypes.CATCH -> return BraceContext.CATCH_BLOCK
                ElvishTokenTypes.FINALLY -> return BraceContext.FINALLY_BLOCK
                // If we hit an identifier after fn, keep looking
                ElvishTokenTypes.IDENTIFIER -> {
                    // Could be function name - continue looking for fn keyword
                    prev = prev.prevSibling
                    continue
                }
                // If we hit a RPAREN, might be output capture or condition
                ElvishTokenTypes.RPAREN -> {
                    prev = prev.prevSibling
                    continue
                }
                // If we hit a variable, could be in a for loop or function param
                ElvishTokenTypes.VARIABLE -> {
                    prev = prev.prevSibling
                    continue
                }
                else -> {
                    // For any other token, it's likely a lambda or generic block
                    return BraceContext.LAMBDA
                }
            }
        }

        // No preceding keyword found - treat as lambda
        return BraceContext.LAMBDA
    }

    private fun getPlaceholderForBrace(context: BraceContext): String {
        return when (context) {
            BraceContext.FUNCTION -> "{...}"
            BraceContext.LAMBDA -> "{...}"
            BraceContext.IF_BLOCK -> "{...}"
            BraceContext.ELIF_BLOCK -> "{...}"
            BraceContext.ELSE_BLOCK -> "{...}"
            BraceContext.FOR_BLOCK -> "{...}"
            BraceContext.WHILE_BLOCK -> "{...}"
            BraceContext.TRY_BLOCK -> "{...}"
            BraceContext.CATCH_BLOCK -> "{...}"
            BraceContext.FINALLY_BLOCK -> "{...}"
            BraceContext.GENERIC -> "{...}"
        }
    }

    /**
     * Checks if a bracket denotes a map literal (starts with &).
     */
    private fun isMapLiteral(openBracket: PsiElement): Boolean {
        var next = openBracket.nextSibling

        // Skip whitespace
        while (next != null) {
            val tokenType = next.node?.elementType
            if (tokenType == ElvishTokenTypes.WHITE_SPACE ||
                tokenType == ElvishTokenTypes.NEWLINE) {
                next = next.nextSibling
                continue
            }

            // Check if next meaningful token is &
            return tokenType == ElvishTokenTypes.AMPERSAND
        }

        return false
    }

    override fun getPlaceholderText(node: ASTNode): String {
        return when (node.elementType) {
            ElvishTokenTypes.LBRACE -> "{...}"
            ElvishTokenTypes.LBRACKET -> "[...]"
            else -> "..."
        }
    }

    override fun isCollapsedByDefault(node: ASTNode): Boolean {
        // Keep everything expanded by default
        // Users can collapse manually as needed
        return false
    }
}
