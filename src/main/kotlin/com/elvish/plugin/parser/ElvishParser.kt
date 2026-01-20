package com.elvish.plugin.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.tree.IElementType

/**
 * Minimal parser for Elvish shell language.
 * Creates a flat AST structure without syntax validation.
 *
 * This parser intentionally does NOT validate Elvish syntax - it accepts
 * all token sequences. Real language intelligence (diagnostics, completion,
 * etc.) is provided by the Elvish LSP server.
 *
 * The lexer provides syntax highlighting; this parser just creates the
 * minimal PSI structure needed for the IDE to function.
 */
class ElvishParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        parseFile(builder)
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseFile(builder: PsiBuilder) {
        while (!builder.eof()) {
            parseStatement(builder)
        }
    }

    private fun parseStatement(builder: PsiBuilder) {
        // Skip whitespace and newlines between statements
        while (!builder.eof() && isWhitespaceOrNewline(builder.tokenType)) {
            builder.advanceLexer()
        }

        if (builder.eof()) return

        val marker = builder.mark()

        // Consume tokens until end of statement (newline, semicolon, or EOF)
        // Handle nested structures (braces, brackets, parens) to avoid breaking mid-expression
        var braceDepth = 0
        var bracketDepth = 0
        var parenDepth = 0

        while (!builder.eof()) {
            val tokenType = builder.tokenType

            when (tokenType) {
                ElvishTokenTypes.LBRACE -> braceDepth++
                ElvishTokenTypes.RBRACE -> braceDepth--
                ElvishTokenTypes.LBRACKET -> bracketDepth++
                ElvishTokenTypes.RBRACKET -> bracketDepth--
                ElvishTokenTypes.LPAREN -> parenDepth++
                ElvishTokenTypes.RPAREN -> parenDepth--
            }

            // Statement ends at newline/semicolon when not inside nested structure
            if (braceDepth == 0 && bracketDepth == 0 && parenDepth == 0) {
                if (tokenType == ElvishTokenTypes.NEWLINE ||
                    tokenType == ElvishTokenTypes.SEMICOLON) {
                    builder.advanceLexer()
                    break
                }
            }

            // Don't go negative on depth (handles unbalanced closing)
            if (braceDepth < 0) braceDepth = 0
            if (bracketDepth < 0) bracketDepth = 0
            if (parenDepth < 0) parenDepth = 0

            builder.advanceLexer()
        }

        marker.done(ElvishElementTypes.STATEMENT)
    }

    private fun isWhitespaceOrNewline(tokenType: IElementType?): Boolean {
        return tokenType == ElvishTokenTypes.WHITE_SPACE ||
               tokenType == ElvishTokenTypes.NEWLINE
    }
}
