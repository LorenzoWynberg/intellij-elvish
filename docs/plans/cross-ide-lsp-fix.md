# Cross-IDE LSP Support Fix

**Date:** 2026-01-20
**Status:** Draft
**Priority:** High

## Problem Statement

The plugin's LSP integration is broken:
1. Uses `com.intellij.modules.ultimate` which only works in IntelliJ IDEA Ultimate
2. Doesn't work in other JetBrains IDEs (WebStorm, PyCharm, GoLand, Rider, RustRover, etc.)
3. LSP features appear minimal or non-functional even in Ultimate

## Research Findings

### JetBrains LSP Module History

| Version | Module | Availability |
|---------|--------|--------------|
| 2023.2+ | `com.intellij.modules.ultimate` | IntelliJ IDEA Ultimate only |
| 2024.2+ | `com.intellij.modules.lsp` | All paid JetBrains IDEs |
| 2025.3+ | `com.intellij.modules.lsp` | **ALL users** (unified distribution) |

### Current State (2026)

As of IntelliJ IDEA 2025.3:
- **Community Edition no longer exists** - unified distribution
- **LSP is free for everyone** - no subscription required
- All JetBrains IDEs support LSP

### Sources

- [The Unified IntelliJ IDEA](https://blog.jetbrains.com/idea/2025/12/intellij-idea-unified-release/)
- [LSP API Available to All](https://blog.jetbrains.com/platform/2025/09/the-lsp-api-is-now-available-to-all-intellij-idea-users-and-plugin-developers/)
- [JetBrains LSP Documentation](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html)
- [IntelliJ Platform 2025.3 for Plugin Developers](https://blog.jetbrains.com/platform/2025/11/intellij-platform-2025-3-what-plugin-developers-should-know/)

## Current Plugin Configuration

**plugin.xml (problematic):**
```xml
<depends optional="true" config-file="lsp-support.xml">com.intellij.modules.ultimate</depends>
```

**lsp-support.xml:**
```xml
<idea-plugin>
    <extensions defaultExtensionNs="com.intellij">
        <platform.lsp.serverSupportProvider
                implementation="com.elvish.plugin.lsp.ElvishLspServerSupportProvider"/>
    </extensions>
</idea-plugin>
```

## Proposed Changes

### 1. Fix Module Dependency

**Change in plugin.xml:**
```xml
<!-- OLD (broken) -->
<depends optional="true" config-file="lsp-support.xml">com.intellij.modules.ultimate</depends>

<!-- NEW (works everywhere) -->
<depends optional="true" config-file="lsp-support.xml">com.intellij.modules.lsp</depends>
```

### 2. Update Minimum Version

**In build.gradle.kts:**
```kotlin
intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "243"  // 2024.3+ for stable LSP API
            untilBuild = provider { null }  // No upper limit
        }
    }
}
```

### 3. Debug LSP Server Startup

Check if LSP is actually starting:
- Add logging to `ElvishLspServerSupportProvider.fileOpened()`
- Verify `elvish -lsp` works from terminal
- Check IDE logs for LSP-related errors

### 4. Verify LSP Descriptor Configuration

Review `ElvishLspServerDescriptor`:
- Ensure `isSupportedFile()` matches `.elv` files
- Verify `createCommandLine()` builds correct command
- Check if custom settings (elvish path) are being used

## Implementation Steps

1. [ ] Change `com.intellij.modules.ultimate` to `com.intellij.modules.lsp` in plugin.xml
2. [ ] Update `sinceBuild` to `243` (2024.3) in build.gradle.kts
3. [ ] Add debug logging to LSP provider
4. [ ] Test in sandbox IDE:
   - Open .elv file
   - Check if LSP server starts (process list)
   - Test completion (type `$` or command name)
   - Test hover (mouse over function)
   - Test diagnostics (introduce syntax error)
5. [ ] Test in multiple JetBrains IDEs if possible
6. [ ] Update documentation

## Testing Checklist

### Basic Functionality
- [ ] Plugin loads without errors
- [ ] `.elv` files recognized with Elvish icon
- [ ] Syntax highlighting works

### LSP Features
- [ ] LSP server process starts (`elvish -lsp`)
- [ ] Code completion works (after `$`, after command names)
- [ ] Hover documentation appears
- [ ] Diagnostics shown for errors
- [ ] Go to definition works
- [ ] Find references works

### Cross-IDE Testing
- [ ] IntelliJ IDEA (unified)
- [ ] WebStorm
- [ ] PyCharm
- [ ] GoLand
- [ ] Other JetBrains IDEs (if available)

## Potential Issues

### 1. Elvish Not in PATH
- LSP won't start if `elvish` binary not found
- Need clear error message
- Settings panel should allow custom path

### 2. LSP Server Crashes
- Elvish LSP might crash on certain files
- Need error handling and restart logic

### 3. Old IDE Versions
- Users on pre-2024.3 won't have `com.intellij.modules.lsp`
- Either: require 2024.3+ OR fall back to no LSP on old versions

## Decision: Minimum IDE Version

**Recommendation:** Require 2024.3+ (sinceBuild = 243)

**Rationale:**
- LSP module is stable in 2024.3+
- Most users update IDEs regularly
- Simplifies plugin maintenance
- 2025.3 unified distribution makes LSP free for all anyway

## Files to Modify

| File | Change |
|------|--------|
| `src/main/resources/META-INF/plugin.xml` | Change module dependency |
| `build.gradle.kts` | Update sinceBuild |
| `src/main/kotlin/.../lsp/ElvishLspServerSupportProvider.kt` | Add logging |
| `src/main/kotlin/.../lsp/ElvishLspServerDescriptor.kt` | Review configuration |
| `docs/CHANGELOG.md` | Document fix |
| `docs/activity/2026-01-20.md` | Log changes |

## Success Criteria

1. LSP features work in IntelliJ IDEA
2. LSP features work in at least one other JetBrains IDE
3. Clear error message if Elvish not installed
4. Documentation updated
