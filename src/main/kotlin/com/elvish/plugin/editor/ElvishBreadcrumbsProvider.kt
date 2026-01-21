package com.elvish.plugin.editor

import com.elvish.plugin.ElvishLanguage
import com.elvish.plugin.parser.ElvishElementTypes
import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider

/**
 * Provides breadcrumbs navigation for Elvish files.
 * Shows the current code context (function name, block type) at the top of the editor.
 */
class ElvishBreadcrumbsProvider : BreadcrumbsProvider {

    override fun getLanguages(): Array<Language> = arrayOf(ElvishLanguage.INSTANCE)

    override fun acceptElement(element: PsiElement): Boolean {
        // Accept statements that contain structural elements (fn, if, for, while, try)
        if (element.node?.elementType != ElvishElementTypes.STATEMENT) {
            return false
        }

        val structuralType = getStructuralType(element)
        return structuralType != null
    }

    override fun getElementInfo(element: PsiElement): String {
        val structuralType = getStructuralType(element) ?: return ""

        return when (structuralType) {
            StructuralType.FUNCTION -> getFunctionInfo(element)
            StructuralType.IF -> "if"
            StructuralType.ELIF -> "elif"
            StructuralType.ELSE -> "else"
            StructuralType.FOR -> "for"
            StructuralType.WHILE -> "while"
            StructuralType.TRY -> "try"
            StructuralType.CATCH -> "catch"
            StructuralType.FINALLY -> "finally"
            StructuralType.LAMBDA -> "λ"
        }
    }

    override fun getElementTooltip(element: PsiElement): String? {
        val structuralType = getStructuralType(element) ?: return null

        return when (structuralType) {
            StructuralType.FUNCTION -> {
                val name = extractFunctionName(element)
                if (name != null) "Function: $name" else "Anonymous function"
            }
            StructuralType.IF -> "If block"
            StructuralType.ELIF -> "Elif block"
            StructuralType.ELSE -> "Else block"
            StructuralType.FOR -> "For loop"
            StructuralType.WHILE -> "While loop"
            StructuralType.TRY -> "Try block"
            StructuralType.CATCH -> "Catch block"
            StructuralType.FINALLY -> "Finally block"
            StructuralType.LAMBDA -> "Lambda expression"
        }
    }

    /**
     * Gets the display info for a function definition.
     * Returns "fn name" for named functions or "fn" for anonymous functions.
     */
    private fun getFunctionInfo(element: PsiElement): String {
        val name = extractFunctionName(element)
        return if (name != null) "fn $name" else "fn"
    }

    /**
     * Extracts the function name from a function definition statement.
     * Returns null for anonymous functions.
     */
    private fun extractFunctionName(element: PsiElement): String? {
        val tokens = collectTokens(element)

        // Look for: fn identifier
        for (i in 0 until tokens.size - 1) {
            if (tokens[i].node?.elementType == ElvishTokenTypes.FN) {
                val nextToken = tokens[i + 1]
                if (nextToken.node?.elementType == ElvishTokenTypes.IDENTIFIER) {
                    return nextToken.text
                }
            }
        }

        return null
    }

    /**
     * Determines the structural type of a statement element.
     * Returns null if the element is not a structural element.
     */
    private fun getStructuralType(element: PsiElement): StructuralType? {
        val tokens = collectTokens(element)
        if (tokens.isEmpty()) return null

        val firstToken = tokens.firstOrNull() ?: return null
        val tokenType = firstToken.node?.elementType

        return when (tokenType) {
            ElvishTokenTypes.FN -> StructuralType.FUNCTION
            ElvishTokenTypes.IF -> StructuralType.IF
            ElvishTokenTypes.ELIF -> StructuralType.ELIF
            ElvishTokenTypes.ELSE -> StructuralType.ELSE
            ElvishTokenTypes.FOR -> StructuralType.FOR
            ElvishTokenTypes.WHILE -> StructuralType.WHILE
            ElvishTokenTypes.TRY -> StructuralType.TRY
            ElvishTokenTypes.CATCH -> StructuralType.CATCH
            ElvishTokenTypes.FINALLY -> StructuralType.FINALLY
            else -> {
                // Check if this is a lambda (starts with {)
                if (tokenType == ElvishTokenTypes.LBRACE) {
                    // Check if it has parameters (starts with {|)
                    if (tokens.size >= 2 && tokens[1].node?.elementType == ElvishTokenTypes.PIPE) {
                        StructuralType.LAMBDA
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        }
    }

    /**
     * Collects all meaningful tokens from an element (skipping whitespace).
     */
    private fun collectTokens(element: PsiElement): List<PsiElement> {
        val tokens = mutableListOf<PsiElement>()
        var child = element.firstChild

        while (child != null) {
            val tokenType = child.node?.elementType
            if (tokenType != ElvishTokenTypes.WHITE_SPACE &&
                tokenType != ElvishTokenTypes.NEWLINE) {
                tokens.add(child)
            }
            child = child.nextSibling
        }

        return tokens
    }

    /**
     * Enum representing the types of structural elements that can appear in breadcrumbs.
     */
    private enum class StructuralType {
        FUNCTION,
        IF,
        ELIF,
        ELSE,
        FOR,
        WHILE,
        TRY,
        CATCH,
        FINALLY,
        LAMBDA
    }
}
