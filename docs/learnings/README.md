# Learnings

Consolidated learnings from development. Updated by Claude and Ralph after each task.

## Structure

| File | Description |
|------|-------------|
| [elvish.md](elvish.md) | Elvish language syntax, features, operators |
| [intellij-plugin.md](intellij-plugin.md) | IntelliJ plugin core patterns |
| [lsp.md](lsp.md) | LSP integration specifics |
| [editor.md](editor.md) | Editor features (folding, structure, breadcrumbs) |
| [run-configs.md](run-configs.md) | Run configurations and execution |
| [templates.md](templates.md) | File and live templates |
| [testing.md](testing.md) | Testing patterns and utilities |
| [build.md](build.md) | Build, Gradle, environment setup |

Each file has its own **Gotchas** section at the bottom for topic-specific mistakes to avoid.

## How This Works

```
┌─────────────────────────────────────────┐
│  Start task                             │
│    ↓                                    │
│  Read relevant learnings/*.md files     │
│    ↓                                    │
│  Do work, discover patterns             │
│    ↓                                    │
│  Write new learnings to appropriate file│
│    ↓                                    │
│  Next task benefits from knowledge      │
└─────────────────────────────────────────┘
```

## Adding New Learnings

1. Identify the appropriate file based on topic
2. Add under the relevant section
3. Keep entries concise (1-2 lines)
4. Focus on actionable insights, not obvious things
