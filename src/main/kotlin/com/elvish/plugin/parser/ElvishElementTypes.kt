package com.elvish.plugin.parser

import com.elvish.plugin.ElvishLanguage
import com.intellij.psi.tree.IElementType
import com.intellij.psi.tree.IFileElementType

/**
 * Element types for the Elvish PSI tree.
 * Minimal set - just enough for IDE functionality.
 * Real language intelligence is provided by LSP.
 */
object ElvishElementTypes {
    // File element type - the root of the PSI tree
    @JvmField
    val FILE = IFileElementType("ELVISH_FILE", ElvishLanguage.INSTANCE)

    // Generic statement - parser groups tokens into statements
    @JvmField
    val STATEMENT = ElvishElementType("STATEMENT")
}

/**
 * Custom IElementType for Elvish AST nodes.
 */
class ElvishElementType(debugName: String) : IElementType(debugName, ElvishLanguage.INSTANCE) {
    override fun toString(): String = "ElvishElementType.${super.toString()}"
}
