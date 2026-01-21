package com.elvish.plugin.run

import com.elvish.plugin.ElvishFileType
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * Produces Elvish run configurations from context (right-click menu).
 * Enables "Run <filename>" in project tree and editor context menus.
 */
class ElvishRunConfigurationProducer : LazyRunConfigurationProducer<ElvishRunConfiguration>() {

    override fun getConfigurationFactory(): ConfigurationFactory {
        return ElvishConfigurationType().configurationFactories[0]
    }

    override fun isConfigurationFromContext(
        configuration: ElvishRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val elvishFile = getElvishFile(context) ?: return false
        val filePath = elvishFile.virtualFile?.path ?: return false
        return configuration.scriptPath == filePath
    }

    override fun setupConfigurationFromContext(
        configuration: ElvishRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val elvishFile = getElvishFile(context) ?: return false
        val virtualFile = elvishFile.virtualFile ?: return false

        // Set script path
        configuration.scriptPath = virtualFile.path

        // Set configuration name to filename without extension
        configuration.name = virtualFile.nameWithoutExtension

        // Set working directory to containing directory
        val parentPath = virtualFile.parent?.path
        if (parentPath != null) {
            configuration.workingDirectory = parentPath
        }

        // Set source element for navigation
        sourceElement.set(elvishFile)

        return true
    }

    /**
     * Gets the Elvish file from the context.
     * Works for both project tree selection and editor context.
     */
    private fun getElvishFile(context: ConfigurationContext): PsiFile? {
        val location = context.location ?: return null
        val psiElement = location.psiElement

        // Check if we're directly on a file or within a file
        val psiFile = when {
            psiElement is PsiFile -> psiElement
            else -> psiElement.containingFile
        }

        // Verify it's an Elvish file
        return psiFile?.takeIf { it.fileType == ElvishFileType }
    }
}
