# Changelog

All notable changes to the Elvish JetBrains plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - Unreleased

### Added

#### Core
- Syntax highlighting via custom lexer with full token support
- File type registration for `.elv` files with custom icon
- LSP integration for all JetBrains IDEs (completion, diagnostics, hover, go-to-definition)
- TextMate bundle for syntax highlighting
- Settings panel for Elvish binary path configuration
- Minimal parser for PSI tree structure
- Error handling with user notification when Elvish binary is missing
- Plugin icons for JetBrains Marketplace (light and dark themes)

#### Editor Enhancements
- **Commenter**: Toggle line comments with `Ctrl+/` (`Cmd+/` on Mac) using `# ` prefix
- **Brace Matching**: Highlight matching braces `{}`, brackets `[]`, and parentheses `()`
- **Code Folding**: Collapse function bodies, lambda expressions, control flow blocks, and multi-line lists/maps
- **Structure View**: Navigate functions (`fn`), variables (`var`), and imports (`use`) in the Structure tool window
- **Breadcrumbs**: Show current code context (function name, control flow blocks) at top of editor
- **TODO Highlighting**: Recognize `TODO`, `FIXME`, `XXX`, `HACK`, `BUG` in comments for the TODO tool window
- **Spell Checking**: Enable spell checking in comments and string literals

#### Run Configuration
- **Run Configuration Type**: Run `.elv` scripts from Run > Edit Configurations dialog
- **Configuration Editor**: Configure script path, arguments, working directory, and environment variables
- **Script Execution**: Execute scripts with output in Run tool window (supports stop/re-run)
- **Gutter Run Icons**: Green play icon at line 1 of `.elv` files for quick execution
- **Context Menu Integration**: Right-click on `.elv` files to run (project tree and editor)

#### Templates
- **File Templates**: New > Elvish Script and New > Elvish Module menu options
  - Elvish Script: Includes shebang (`#!/usr/bin/env elvish`) and comment header
  - Elvish Module: Includes example function definition for reusable modules
- **Live Templates**: 15 code snippets for common Elvish patterns
  - Function: `fn` (function definition)
  - Control Flow: `if`, `ife`, `for`, `while`, `try`, `tryf`
  - Common Patterns: `use`, `var`, `set`, `each`, `peach`, `lambda`, `map`, `list`

### Notes
- Cross-IDE support: Works in all JetBrains IDEs 2024.3+ (IntelliJ, WebStorm, PyCharm, etc.)
- LSP features are free for all users
