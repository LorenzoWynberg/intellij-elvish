package com.elvish.plugin

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object ElvishFileType : LanguageFileType(ElvishLanguage.INSTANCE) {
    override fun getName(): String = "Elvish"
    override fun getDescription(): String = "Elvish shell script"
    override fun getDefaultExtension(): String = "elv"
    override fun getIcon(): Icon = ElvishIcons.FILE
}
