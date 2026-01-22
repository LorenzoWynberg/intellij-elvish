# CLAUDE.md

Instructions for Claude Code when working in this repository.

## Quick Reference

```bash
./gradlew build        # Build and test
./gradlew runIde       # Run sandbox IDE
./scripts/ralph/ralph.elv  # Run autonomous dev loop
```

## Activity Logging (Required)

**Always update the activity log when working on this project.**

- Log location: `docs/activity/YYYY-MM-DD.md`
- Create new file if today's log doesn't exist
- Update throughout the session with changes made
- See `docs/activity/README.md` for template and guidelines

Example: `docs/activity/2026-01-20.md`

## Learnings (Read & Write)

**Always check relevant learnings before starting work, and add new learnings when you discover something.**

- Location: `docs/learnings/`
- Files organized by topic: `elvish.md`, `lsp.md`, `editor.md`, etc.
- Each file has a **Gotchas** section for common mistakes
- Add new learnings to the appropriate file after completing work

This creates a feedback loop where knowledge accumulates across sessions.

## Documentation

- [Development Guide](docs/DEVELOPMENT.md) - Build, architecture, debugging
- [Contributing Guide](docs/CONTRIBUTING.md) - Branching, commits, PRs
- [Ralph Guide](docs/RALPH.md) - Autonomous development loop
- [Changelog](docs/CHANGELOG.md) - Version history
- [Learnings](docs/learnings/) - Consolidated patterns and gotchas

## Project Overview

JetBrains plugin for Elvish shell language support. Uses Elvish's built-in LSP (`elvish -lsp`) for completions, diagnostics, hover, and go-to-definition.

## Key Files

| Path | Purpose |
|------|---------|
| `src/main/kotlin/com/elvish/plugin/` | Plugin source code |
| `src/main/resources/META-INF/plugin.xml` | Plugin manifest |
| `src/main/resources/textmate/` | TextMate grammar |
| `scripts/ralph/` | Autonomous dev loop |

## Workflow

Use [Ralph](docs/RALPH.md) for autonomous development, or follow manual workflow:

1. Create branch from `dev`
2. Implement changes
3. Run `./gradlew build`
4. Create PR targeting `dev`

## Branching

- `dev` - Integration branch, PRs merge here
- `main` - Stable releases only
- `feat/story-X.Y.Z` - Feature branches

## Commits

- One commit per logical change
- Push after each commit
- No "Co-Authored-By" lines
- Format: `type: STORY-XXX - Description`
