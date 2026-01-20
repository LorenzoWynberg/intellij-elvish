package com.elvish.plugin

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class ElvishFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ElvishLanguage.INSTANCE) {
    override fun getFileType(): FileType = ElvishFileType
    override fun toString(): String = "Elvish File"
}
