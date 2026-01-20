# JetBrains Elvish Plugin - Implementation Plan

## Overview

Create a JetBrains IDE plugin that provides syntax highlighting and language support for [Elvish shell](https://elv.sh) files (`.elv`) using the built-in Elvish LSP server (`elvish -lsp`).

## Architecture Decision

**Approach: JetBrains Official LSP API**

As of 2025, JetBrains provides an official LSP API available to all IntelliJ users. This is the recommended approach over third-party solutions like LSP4IJ.

**Why this approach:**
- Official support from JetBrains
- Available in all IntelliJ-based IDEs (2023.2+)
- Simpler dependency management
- Better long-term maintainability

## Elvish Language Server

Elvish has a **built-in language server** that runs via:
```bash
elvish -lsp
```

The LSP communicates via stdio (standard input/output).

---

## Implementation Phases

### Phase 1: Project Setup

**1.1 Create Gradle Project Structure**
```
jetbrains-elvish/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── src/
    └── main/
        ├── kotlin/
        │   └── com/
        │       └── elvish/
        │           └── plugin/
        ├── resources/
        │   └── META-INF/
        │       └── plugin.xml
        └── icons/
```

**1.2 Configure build.gradle.kts**
```kotlin
plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "com.elvish"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.3")
        bundledPlugin("com.intellij.modules.lang")
        instrumentationTools()
    }
}

tasks {
    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("251.*")
    }
}
```

**1.3 Configure plugin.xml**
```xml
<idea-plugin>
    <id>com.elvish.plugin</id>
    <name>Elvish</name>
    <vendor>Your Name</vendor>
    <description>
        Elvish shell language support with LSP integration.
        Provides syntax highlighting, code completion, and diagnostics for .elv files.
    </description>

    <depends>com.intellij.modules.platform</depends>
    <depends>com.intellij.modules.lang</depends>
    <depends>com.intellij.modules.lsp</depends>

    <extensions defaultExtensionNs="com.intellij">
        <!-- File Type -->
        <fileType
            name="Elvish"
            implementationClass="com.elvish.plugin.ElvishFileType"
            fieldName="INSTANCE"
            language="Elvish"
            extensions="elv"/>

        <!-- Language -->
        <lang.parserDefinition
            language="Elvish"
            implementationClass="com.elvish.plugin.ElvishParserDefinition"/>

        <!-- LSP Server Support -->
        <platform.lsp.serverSupportProvider
            implementation="com.elvish.plugin.lsp.ElvishLspServerSupportProvider"/>

        <!-- Icons -->
        <iconProvider
            implementation="com.elvish.plugin.ElvishIconProvider"/>
    </extensions>
</idea-plugin>
```

---

### Phase 2: File Type Registration

**2.1 Create ElvishFileType.kt**
```kotlin
package com.elvish.plugin

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object ElvishFileType : LanguageFileType(ElvishLanguage.INSTANCE) {
    override fun getName(): String = "Elvish"
    override fun getDescription(): String = "Elvish shell script"
    override fun getDefaultExtension(): String = "elv"
    override fun getIcon(): Icon = ElvishIcons.FILE
}
```

**2.2 Create ElvishLanguage.kt**
```kotlin
package com.elvish.plugin

import com.intellij.lang.Language

class ElvishLanguage private constructor() : Language("Elvish") {
    companion object {
        val INSTANCE = ElvishLanguage()
    }
}
```

**2.3 Create ElvishIcons.kt**
```kotlin
package com.elvish.plugin

import com.intellij.openapi.util.IconLoader

object ElvishIcons {
    val FILE = IconLoader.getIcon("/icons/elvish.svg", ElvishIcons::class.java)
}
```

---

### Phase 3: LSP Integration (Core Feature)

**3.1 Create ElvishLspServerSupportProvider.kt**
```kotlin
package com.elvish.plugin.lsp

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.LspServerSupportProvider.LspServerStarter

class ElvishLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(
        project: Project,
        file: VirtualFile,
        serverStarter: LspServerStarter
    ) {
        if (file.extension == "elv") {
            serverStarter.ensureServerStarted(ElvishLspServerDescriptor(project))
        }
    }
}
```

**3.2 Create ElvishLspServerDescriptor.kt**
```kotlin
package com.elvish.plugin.lsp

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.customization.LspServerWidgetItem
import com.elvish.plugin.ElvishIcons

class ElvishLspServerDescriptor(project: Project) :
    ProjectWideLspServerDescriptor(project, "Elvish") {

    override fun isSupportedFile(file: VirtualFile): Boolean {
        return file.extension == "elv"
    }

    override fun createCommandLine(): GeneralCommandLine {
        // Uses the elvish binary from PATH
        // Users must have elvish installed
        return GeneralCommandLine("elvish", "-lsp")
    }

    override fun createLspServerWidgetItem(
        lspServer: LspServer,
        currentFile: VirtualFile?
    ): LspServerWidgetItem {
        return LspServerWidgetItem(
            lspServer,
            currentFile,
            ElvishIcons.FILE,
            null // No settings configurable for now
        )
    }
}
```

---

### Phase 4: Basic Syntax Highlighting (Fallback)

Even with LSP, having basic syntax highlighting provides immediate visual feedback. This can be done via TextMate grammars or a custom lexer.

**Option A: TextMate Grammar (Recommended for simplicity)**

Create `src/main/resources/textmate/elvish.tmLanguage.json`:
```json
{
  "name": "Elvish",
  "scopeName": "source.elvish",
  "fileTypes": ["elv"],
  "patterns": [
    { "include": "#comments" },
    { "include": "#strings" },
    { "include": "#variables" },
    { "include": "#keywords" },
    { "include": "#functions" },
    { "include": "#numbers" },
    { "include": "#operators" }
  ],
  "repository": {
    "comments": {
      "match": "#.*$",
      "name": "comment.line.elvish"
    },
    "strings": {
      "patterns": [
        {
          "begin": "\"",
          "end": "\"",
          "name": "string.quoted.double.elvish",
          "patterns": [
            { "match": "\\\\.", "name": "constant.character.escape.elvish" }
          ]
        },
        {
          "begin": "'",
          "end": "'",
          "name": "string.quoted.single.elvish",
          "patterns": [
            { "match": "''", "name": "constant.character.escape.elvish" }
          ]
        }
      ]
    },
    "variables": {
      "match": "\\$[\\w:~-]+",
      "name": "variable.other.elvish"
    },
    "keywords": {
      "match": "\\b(if|elif|else|while|for|try|catch|finally|fn|var|set|del|use|pragma|and|or|coalesce|break|continue|return|fail)\\b",
      "name": "keyword.control.elvish"
    },
    "functions": {
      "match": "\\b(put|echo|print|pprint|repr|show|slurp|from-json|to-json|from-lines|to-lines|all|one|take|drop|count|range|repeat|keys|values|assoc|dissoc|has-key|has-value|each|peach|filter|map|reduce|order|compare|eq|not-eq|is|not|bool|not|num|exact-num|float64|str|styled|to-string|wcswidth|base|chr|ord|randint|rand|time|sleep|path-abs|path-base|path-clean|path-dir|path-ext|tilde-abbr|cd|pwd|dir-history|exec|exit|external|has-external|search-external|nop|call|resolve|eval|use-mod|deprecate)\\b",
      "name": "support.function.elvish"
    },
    "numbers": {
      "match": "\\b(0x[0-9a-fA-F]+|0o[0-7]+|0b[01]+|[0-9]+\\.?[0-9]*([eE][+-]?[0-9]+)?|\\.[0-9]+([eE][+-]?[0-9]+)?)\\b",
      "name": "constant.numeric.elvish"
    },
    "operators": {
      "match": "[|<>&;]|>>|<>",
      "name": "keyword.operator.elvish"
    }
  }
}
```

Register in plugin.xml:
```xml
<extensions defaultExtensionNs="com.intellij">
    <textmate.bundleProvider
        implementation="com.elvish.plugin.ElvishTextMateBundleProvider"/>
</extensions>
```

---

### Phase 5: Parser Definition (Minimal)

For IDE features that require AST, create a minimal parser:

**5.1 ElvishParserDefinition.kt**
```kotlin
package com.elvish.plugin

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class ElvishParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(ElvishLanguage.INSTANCE)
    }

    override fun createLexer(project: Project?): Lexer = ElvishLexer()
    override fun createParser(project: Project?): PsiParser = ElvishParser()
    override fun getFileNodeType(): IFileElementType = FILE
    override fun getCommentTokens(): TokenSet = TokenSet.EMPTY
    override fun getStringLiteralElements(): TokenSet = TokenSet.EMPTY
    override fun createElement(node: ASTNode): PsiElement = TODO()
    override fun createFile(viewProvider: FileViewProvider): PsiFile =
        ElvishFile(viewProvider)
}
```

---

### Phase 6: Settings & Configuration

**6.1 ElvishSettings.kt** - Allow users to configure the elvish executable path
```kotlin
package com.elvish.plugin.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@State(
    name = "ElvishSettings",
    storages = [Storage("elvish.xml")]
)
@Service(Service.Level.PROJECT)
class ElvishSettings : PersistentStateComponent<ElvishSettings.State> {
    data class State(
        var elvishPath: String = "elvish"
    )

    private var state = State()

    override fun getState(): State = state
    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(project: Project): ElvishSettings =
            project.service()
    }
}
```

**6.2 ElvishConfigurable.kt** - Settings UI
```kotlin
package com.elvish.plugin.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class ElvishConfigurable(private val project: Project) : Configurable {
    private var pathField: TextFieldWithBrowseButton? = null

    override fun getDisplayName(): String = "Elvish"

    override fun createComponent(): JComponent {
        val settings = ElvishSettings.getInstance(project)

        return panel {
            group("Language Server") {
                row("Elvish executable:") {
                    pathField = textFieldWithBrowseButton(
                        "Select Elvish Executable",
                        project,
                        FileChooserDescriptorFactory.createSingleFileDescriptor()
                    ).component
                    pathField?.text = settings.state.elvishPath
                }
                row {
                    comment("Path to the elvish binary. Leave as 'elvish' to use PATH.")
                }
            }
        }
    }

    override fun isModified(): Boolean {
        val settings = ElvishSettings.getInstance(project)
        return pathField?.text != settings.state.elvishPath
    }

    override fun apply() {
        val settings = ElvishSettings.getInstance(project)
        settings.state.elvishPath = pathField?.text ?: "elvish"
    }
}
```

---

## Phase Summary

| Phase | Description | Priority |
|-------|-------------|----------|
| 1 | Project Setup | Required |
| 2 | File Type Registration | Required |
| 3 | LSP Integration | Required (Core) |
| 4 | TextMate Syntax Highlighting | Recommended |
| 5 | Parser Definition | Optional |
| 6 | Settings & Configuration | Recommended |

---

## Expected LSP Features

Based on Elvish's built-in LSP, users should get:
- Code completion
- Diagnostics (errors/warnings)
- Hover documentation
- Go to definition
- Find references

---

## File Structure (Final)

```
jetbrains-elvish/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── docs/
│   └── plans/
│       └── implementation-plan.md
└── src/
    └── main/
        ├── kotlin/
        │   └── com/
        │       └── elvish/
        │           └── plugin/
        │               ├── ElvishFileType.kt
        │               ├── ElvishLanguage.kt
        │               ├── ElvishIcons.kt
        │               ├── ElvishIconProvider.kt
        │               ├── ElvishFile.kt
        │               ├── ElvishParserDefinition.kt
        │               ├── ElvishLexer.kt
        │               ├── ElvishParser.kt
        │               ├── lsp/
        │               │   ├── ElvishLspServerSupportProvider.kt
        │               │   └── ElvishLspServerDescriptor.kt
        │               └── settings/
        │                   ├── ElvishSettings.kt
        │                   └── ElvishConfigurable.kt
        └── resources/
            ├── META-INF/
            │   ├── plugin.xml
            │   └── pluginIcon.svg
            ├── icons/
            │   └── elvish.svg
            └── textmate/
                └── elvish.tmLanguage.json
```

---

## Prerequisites for Users

1. **Elvish installed** - Users must have `elvish` in their PATH (or configure the path in settings)
2. **JetBrains IDE 2024.3+** - For full LSP support

---

## Testing Checklist

- [ ] Plugin loads without errors
- [ ] `.elv` files are recognized with correct icon
- [ ] LSP server starts when opening `.elv` file
- [ ] Syntax highlighting works
- [ ] Code completion works
- [ ] Diagnostics appear for errors
- [ ] Hover shows documentation
- [ ] Settings page allows configuring elvish path

---

## Resources

- [Elvish Shell Documentation](https://elv.sh/learn/)
- [Elvish Language Reference](https://elv.sh/ref/language.html)
- [Elvish Command Reference](https://elv.sh/ref/command.html)
- [JetBrains LSP API Documentation](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html)
- [JetBrains LSP Blog Post](https://blog.jetbrains.com/platform/2025/09/the-lsp-api-is-now-available-to-all-intellij-idea-users-and-plugin-developers/)
- [IntelliJ Platform Plugin SDK](https://plugins.jetbrains.com/docs/intellij/custom-language-support.html)
- [LSP4IJ (Alternative)](https://github.com/redhat-developer/lsp4ij)
