# Ralph - Autonomous Development Loop

Ralph is an autonomous development agent that iterates through stories from a PRD until complete.

## Quick Start

```bash
# Start or continue development
./scripts/ralph/ralph.elv

# Resume current story
./scripts/ralph/ralph.elv --resume

# Reset state and start fresh
./scripts/ralph/ralph.elv --reset

# See all options
./scripts/ralph/ralph.elv --help
```

## How It Works

1. **Pick Story** - Selects next story respecting dependencies
2. **Create Branch** - Creates `feat/story-X.Y.Z` from `dev`
3. **Implement** - Runs Claude to implement the story
4. **Verify** - Runs `./gradlew build` to verify
5. **Self-Review** - Agent reviews work and iterates
6. **Create PR** - Opens PR targeting `dev`
7. **Merge** - Merges PR and cleans up branch
8. **Repeat** - Moves to next story

## Files

| File | Purpose |
|------|---------|
| `scripts/ralph/ralph.elv` | Main script |
| `scripts/ralph/prd.json` | Stories with acceptance criteria |
| `scripts/ralph/prompt.md` | Agent instructions template |
| `scripts/ralph/progress.txt` | Development log |
| `scripts/ralph/state.json` | Current story tracking (local only) |

## Options

| Option | Description |
|--------|-------------|
| `--max-iterations <n>` | Max iterations before auto-stop (default: 15) |
| `--base-branch <name>` | Base branch for new stories (default: dev) |
| `--resume` | Resume from last state |
| `--reset` | Reset state and start fresh |
| `-q, --quiet` | Hide Claude output, show only status |
| `-h, --help` | Show help |

## Story Format (prd.json)

```json
{
  "stories": [
    {
      "id": "STORY-001",
      "phase": 1,
      "epic": 1,
      "story_number": 1,
      "title": "Story title",
      "acceptance_criteria": ["Criterion 1", "Criterion 2"],
      "dependencies": [],
      "passes": false
    }
  ]
}
```

## Workflow

```
┌─────────────────────────────────────────────────┐
│                  RALPH LOOP                      │
├─────────────────────────────────────────────────┤
│  1. Read state.json (or pick next story)        │
│  2. Create branch feat/story-X.Y.Z from dev     │
│  3. Run Claude with prompt.md                   │
│  4. Claude implements + runs ./gradlew build    │
│  5. Self-review cycle until satisfied           │
│  6. Create PR → Merge → Delete branch           │
│  7. Sync local dev with remote                  │
│  8. Pause (20s) → Continue or stop              │
│  9. Repeat until all stories complete           │
└─────────────────────────────────────────────────┘
```

## Interrupting

- Press `n` at the pause prompt to stop after current story
- Press `Ctrl+C` to interrupt immediately (can resume later)
- Run with `--resume` to continue from where you left off

## Troubleshooting

**Ralph stuck on a story:**
```bash
./scripts/ralph/ralph.elv --reset
```

**Build failing:**
```bash
./gradlew clean build
```

**Branch conflicts:**
Ralph always syncs from remote using `fetch + reset --hard`, so local changes on `dev` are discarded. Work on feature branches only.
