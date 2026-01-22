# Quick Context

Fast reference for AI agents. For details, see linked docs.

## Project

JetBrains plugin for Elvish shell language support. Uses `elvish -lsp` for language intelligence.

## Commands

```bash
./gradlew build        # Build + test (required before PR)
./gradlew runIde       # Test in sandbox IDE
./gradlew buildPlugin  # Create distribution ZIP
```

## Key Paths

| Path | Purpose |
|------|---------|
| `src/main/kotlin/com/elvish/plugin/` | Plugin source |
| `src/main/resources/META-INF/plugin.xml` | Plugin manifest |
| `src/main/resources/textmate/` | TextMate grammar |
| `docs/learnings/` | Topic-specific learnings |
| `docs/activity/` | Daily activity logs |

## Branching

- `main` - Production (tagged releases)
- `dev` - Integration (PRs merge here)
- `feat/description` - Features
- `fix/description` - Bug fixes

See [CONTRIBUTING.md](CONTRIBUTING.md) for full workflow.

## Commits

Format: `type: Description` (e.g., `feat: Add dark mode`)

Types: `feat`, `fix`, `refactor`, `docs`, `chore`

No "Co-Authored-By" lines.

## Before PR

1. `./gradlew build` passes
2. Update relevant docs
3. Update `docs/activity/YYYY-MM-DD.md`

## Learnings Quick Index

| Topic | File | TL;DR |
|-------|------|-------|
| Elvish | [elvish.md](learnings/elvish.md) | Syntax: `catch e`, `for x $list`, `{|params|}` |
| LSP | [lsp.md](learnings/lsp.md) | `modules.lsp`, stdio, diagnostics pushed |
| Plugin | [intellij-plugin.md](learnings/intellij-plugin.md) | Language, TextMate, Settings patterns |
| Editor | [editor.md](learnings/editor.md) | Folding, structure, breadcrumbs, TODO |
| Run | [run-configs.md](learnings/run-configs.md) | RunConfiguration, gutter icons |
| Templates | [templates.md](learnings/templates.md) | File templates, live templates |
| Testing | [testing.md](learnings/testing.md) | LSP tests, ProcessBuilder |
| Build | [build.md](learnings/build.md) | Java 17+, Gradle commands |
