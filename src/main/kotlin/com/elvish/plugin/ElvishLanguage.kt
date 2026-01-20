package com.elvish.plugin

import com.intellij.lang.Language

class ElvishLanguage private constructor() : Language("Elvish") {
    companion object {
        @JvmStatic
        val INSTANCE = ElvishLanguage()
    }
}
