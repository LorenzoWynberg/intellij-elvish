package com.elvish.plugin.highlighting

import com.elvish.plugin.parser.ElvishLexer
import com.elvish.plugin.parser.ElvishTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class ElvishSyntaxHighlighter : SyntaxHighlighterBase() {

    companion object {
        // Comments
        val COMMENT = createTextAttributesKey("ELVISH_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)

        // Strings
        val STRING = createTextAttributesKey("ELVISH_STRING", DefaultLanguageHighlighterColors.STRING)

        // Numbers
        val NUMBER = createTextAttributesKey("ELVISH_NUMBER", DefaultLanguageHighlighterColors.NUMBER)

        // Keywords
        val KEYWORD = createTextAttributesKey("ELVISH_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)

        // Variables
        val VARIABLE = createTextAttributesKey("ELVISH_VARIABLE", DefaultLanguageHighlighterColors.LOCAL_VARIABLE)

        // Constants
        val CONSTANT = createTextAttributesKey("ELVISH_CONSTANT", DefaultLanguageHighlighterColors.CONSTANT)

        // Operators
        val OPERATOR = createTextAttributesKey("ELVISH_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN)

        // Punctuation
        val BRACES = createTextAttributesKey("ELVISH_BRACES", DefaultLanguageHighlighterColors.BRACES)
        val BRACKETS = createTextAttributesKey("ELVISH_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS)
        val PARENTHESES = createTextAttributesKey("ELVISH_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES)
        val SEMICOLON = createTextAttributesKey("ELVISH_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON)
        val COMMA = createTextAttributesKey("ELVISH_COMMA", DefaultLanguageHighlighterColors.COMMA)

        // Identifiers
        val IDENTIFIER = createTextAttributesKey("ELVISH_IDENTIFIER", DefaultLanguageHighlighterColors.IDENTIFIER)

        // Bad character
        val BAD_CHARACTER = createTextAttributesKey("ELVISH_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER)

        // Token to attributes mapping
        private val COMMENT_KEYS = arrayOf(COMMENT)
        private val STRING_KEYS = arrayOf(STRING)
        private val NUMBER_KEYS = arrayOf(NUMBER)
        private val KEYWORD_KEYS = arrayOf(KEYWORD)
        private val VARIABLE_KEYS = arrayOf(VARIABLE)
        private val CONSTANT_KEYS = arrayOf(CONSTANT)
        private val OPERATOR_KEYS = arrayOf(OPERATOR)
        private val BRACES_KEYS = arrayOf(BRACES)
        private val BRACKETS_KEYS = arrayOf(BRACKETS)
        private val PARENTHESES_KEYS = arrayOf(PARENTHESES)
        private val SEMICOLON_KEYS = arrayOf(SEMICOLON)
        private val COMMA_KEYS = arrayOf(COMMA)
        private val IDENTIFIER_KEYS = arrayOf(IDENTIFIER)
        private val BAD_CHARACTER_KEYS = arrayOf(BAD_CHARACTER)
        private val EMPTY_KEYS = emptyArray<TextAttributesKey>()
    }

    override fun getHighlightingLexer(): Lexer = ElvishLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> {
        return when (tokenType) {
            // Comments
            ElvishTokenTypes.COMMENT -> COMMENT_KEYS

            // Strings
            ElvishTokenTypes.SINGLE_QUOTED_STRING,
            ElvishTokenTypes.DOUBLE_QUOTED_STRING -> STRING_KEYS

            // Numbers
            ElvishTokenTypes.INTEGER,
            ElvishTokenTypes.FLOAT,
            ElvishTokenTypes.HEX_NUMBER,
            ElvishTokenTypes.OCTAL_NUMBER,
            ElvishTokenTypes.BINARY_NUMBER -> NUMBER_KEYS

            // Keywords
            ElvishTokenTypes.IF,
            ElvishTokenTypes.ELIF,
            ElvishTokenTypes.ELSE,
            ElvishTokenTypes.WHILE,
            ElvishTokenTypes.FOR,
            ElvishTokenTypes.TRY,
            ElvishTokenTypes.CATCH,
            ElvishTokenTypes.FINALLY,
            ElvishTokenTypes.BREAK,
            ElvishTokenTypes.CONTINUE,
            ElvishTokenTypes.RETURN,
            ElvishTokenTypes.FN,
            ElvishTokenTypes.VAR,
            ElvishTokenTypes.SET,
            ElvishTokenTypes.TMP,
            ElvishTokenTypes.DEL,
            ElvishTokenTypes.USE,
            ElvishTokenTypes.PRAGMA,
            ElvishTokenTypes.AND,
            ElvishTokenTypes.OR,
            ElvishTokenTypes.COALESCE -> KEYWORD_KEYS

            // Variables
            ElvishTokenTypes.VARIABLE -> VARIABLE_KEYS

            // Constants
            ElvishTokenTypes.TRUE,
            ElvishTokenTypes.FALSE,
            ElvishTokenTypes.NIL -> CONSTANT_KEYS

            // Operators
            ElvishTokenTypes.PIPE,
            ElvishTokenTypes.EQ,
            ElvishTokenTypes.NE,
            ElvishTokenTypes.LT,
            ElvishTokenTypes.GT,
            ElvishTokenTypes.LE,
            ElvishTokenTypes.GE,
            ElvishTokenTypes.PLUS,
            ElvishTokenTypes.MINUS,
            ElvishTokenTypes.STAR,
            ElvishTokenTypes.SLASH,
            ElvishTokenTypes.PERCENT,
            ElvishTokenTypes.ASSIGN,
            ElvishTokenTypes.RANGE,
            ElvishTokenTypes.RANGE_INCLUSIVE,
            ElvishTokenTypes.AMPERSAND -> OPERATOR_KEYS

            // Braces
            ElvishTokenTypes.LBRACE,
            ElvishTokenTypes.RBRACE -> BRACES_KEYS

            // Brackets
            ElvishTokenTypes.LBRACKET,
            ElvishTokenTypes.RBRACKET -> BRACKETS_KEYS

            // Parentheses
            ElvishTokenTypes.LPAREN,
            ElvishTokenTypes.RPAREN -> PARENTHESES_KEYS

            // Semicolon
            ElvishTokenTypes.SEMICOLON -> SEMICOLON_KEYS

            // Comma
            ElvishTokenTypes.COMMA -> COMMA_KEYS

            // Identifiers
            ElvishTokenTypes.IDENTIFIER -> IDENTIFIER_KEYS

            // Bad character
            ElvishTokenTypes.BAD_CHARACTER -> BAD_CHARACTER_KEYS

            else -> EMPTY_KEYS
        }
    }
}
