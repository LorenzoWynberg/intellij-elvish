package com.elvish.plugin.actions

import com.elvish.plugin.ElvishFileType
import com.elvish.plugin.ElvishIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

/**
 * Action for creating new Elvish script files from the "New" context menu.
 */
class CreateElvishFileAction : CreateFileFromTemplateAction(
    "Elvish Script",
    "Create a new Elvish shell script",
    ElvishIcons.FILE
), DumbAware {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("New Elvish Script")
            .addKind("Elvish Script", ElvishIcons.FILE, "Elvish Script")
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String {
        return "Create Elvish Script $newName"
    }
}
