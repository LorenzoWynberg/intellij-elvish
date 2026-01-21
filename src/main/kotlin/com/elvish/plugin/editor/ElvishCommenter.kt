package com.elvish.plugin.editor

import com.intellij.lang.Commenter

/**
 * Commenter for Elvish files.
 *
 * Elvish uses # for line comments (like most shell languages).
 * There are no block comments in Elvish.
 */
class ElvishCommenter : Commenter {

    override fun getLineCommentPrefix(): String = "# "

    override fun getBlockCommentPrefix(): String? = null

    override fun getBlockCommentSuffix(): String? = null

    override fun getCommentedBlockCommentPrefix(): String? = null

    override fun getCommentedBlockCommentSuffix(): String? = null
}
