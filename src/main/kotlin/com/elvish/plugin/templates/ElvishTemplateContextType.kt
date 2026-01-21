package com.elvish.plugin.templates

import com.elvish.plugin.ElvishLanguage
import com.intellij.codeInsight.template.TemplateActionContext
import com.intellij.codeInsight.template.TemplateContextType

/**
 * Defines the context in which Elvish live templates are available.
 * Templates will only appear and expand in Elvish files (.elv).
 */
class ElvishTemplateContextType : TemplateContextType("Elvish") {

    override fun isInContext(templateActionContext: TemplateActionContext): Boolean {
        val file = templateActionContext.file
        return file.language == ElvishLanguage.INSTANCE
    }
}
