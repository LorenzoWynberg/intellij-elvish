# Ralph.elv Improvements and Potential Issues

## Critical Bugs to Fix

### 1. Line 505 - Incorrect Pipe Syntax
```elvish
# Current (BROKEN):
var file-size = (wc -c < $output-file | str:trim-space (one))

# Fixed:
var file-size = (wc -c < $output-file | one | str:trim-space)
```
The `one` command needs to receive piped input, not be passed as an argument to `str:trim-space`.

### 2. Lines 153-159 - Use `has-external` Instead of `command -v`
```elvish
# Current (fragile):
try {
  command -v $cmd > /dev/null 2>&1
} catch {
  ralph-error "Required command '"$cmd"' not found in PATH"
  exit 1
}

# Improved (more idiomatic):
if (not (has-external $cmd)) {
  ralph-error "Required command '"$cmd"' not found in PATH"
  exit 1
}
```
`has-external` is the proper Elvish way to check for external commands.

### 3. Line 231 - Potential Empty Split Crash
```elvish
# Current (can crash if info is empty):
var parts = [(str:split "\t" $info)]
var phase = $parts[0]

# Improved (with validation):
var parts = [(str:split "\t" $info)]
if (< (count $parts) 3) {
  ralph-error "Failed to parse story info for "$story-id
  continue  # or return
}
var phase = $parts[0]
```

## Improvements for Robustness

### 4. State File Initialization (Line 174)
```elvish
# Current (using echo):
echo '{"version":1,...}' > $state-file

# Improved (more idiomatic, less error-prone):
var initial-state = [
  &version=(num 1)
  &current_story=$nil
  &status="idle"
  &branch=$nil
  &started_at=$nil
  &last_updated=$nil
  &attempts=(num 0)
  &error=$nil
  &checkpoints=[]
]
put $initial-state | to-json > $state-file
```

### 5. Add Timeout for Claude Execution
```elvish
# Current (can hang indefinitely):
echo $iteration-prompt | claude --dangerously-skip-permissions --print > $output-file 2>&1

# Improved (with timeout):
try {
  timeout 1800 bash -c 'echo "$1" | claude --dangerously-skip-permissions --print' _ $iteration-prompt > $output-file 2>&1
} catch {
  ralph-error "Claude timed out after 30 minutes"
  # Handle timeout...
}
```

### 6. Better Error Messages for jq Failures
```elvish
# Add validation after jq calls:
fn get-story-info {|story-id|
  var sid = $story-id
  var pf = $prd-file
  var query = ".stories[] | select(.id == \""$sid"\") | \"\\(.phase)\\t\\(.epic)\\t\\(.story_number)\""
  var result = (jq -r $query $pf | slurp | str:trim-space)
  if (eq $result "") {
    fail "Story "$sid" not found in PRD"
  }
  put $result
}
```

### 7. Branch Existence Check - Include Remote
```elvish
fn branch-exists {|branch|
  try {
    # Check local
    git -C $project-root rev-parse --verify "refs/heads/"$branch > /dev/null 2>&1
    put $true
  } catch {
    try {
      # Check remote
      git -C $project-root rev-parse --verify "refs/remotes/origin/"$branch > /dev/null 2>&1
      put $true
    } catch {
      put $false
    }
  }
}
```

## Feature Improvements

### 8. Add Dry-Run Mode
```elvish
var dry-run = $false

# In arg parsing:
} elif (eq $arg "--dry-run") {
  set dry-run = $true
  set i = (+ $i 1)

# Usage:
if $dry-run {
  ralph-status "DRY RUN: Would create branch "$branch-name
} else {
  git -C $project-root checkout -b $branch-name > /dev/null 2>&1
}
```

### 9. Add Story Skip Functionality
```elvish
var skip-story = ""

# In arg parsing:
} elif (eq $arg "--skip") {
  var next-idx = (+ $i 1)
  set skip-story = $args[$next-idx]
  set i = (+ $i 2)

# In get-next-story, filter out skipped story
```

### 10. Improve Progress Reporting
```elvish
fn show-progress {
  var pf = $prd-file
  var total = (jq '.stories | length' $pf)
  var complete = (jq '[.stories[] | select(.passes == true)] | length' $pf)
  var pct = (/ (* $complete 100) $total)
  ralph-status "Progress: "$complete"/"$total" stories ("$pct"%)"
}
```

### 11. Add Checkpoint/Rollback
```elvish
fn save-checkpoint {|name|
  var checkpoint = [
    &name=$name
    &timestamp=(date -u '+%Y-%m-%dT%H:%M:%SZ')
    &commit=(git -C $project-root rev-parse HEAD | slurp | str:trim-space)
    &branch=(current-branch)
  ]
  var state = (read-state)
  set state[checkpoints] = [$@state[checkpoints] $checkpoint]
  write-state $state
  ralph-success "Checkpoint saved: "$name
}

fn rollback-to-checkpoint {|name|
  var state = (read-state)
  for cp $state[checkpoints] {
    if (eq $cp[name] $name) {
      git -C $project-root reset --hard $cp[commit]
      ralph-success "Rolled back to checkpoint: "$name
      return
    }
  }
  ralph-error "Checkpoint not found: "$name
}
```

### 12. Better Signal Handling
```elvish
# Add cleanup on interrupt
fn cleanup {
  ralph-warn "Interrupted! Saving state..."
  var state = (read-state)
  set state[status] = "interrupted"
  write-state $state
}

# At script start (if possible - Elvish signal handling is limited):
# set-env ELVISH_PROMPT_STALE_THRESHOLD 0
```

## Things That Could Trip Up Claude/Ralph

### 1. The Run Configuration Bug
The `ElvishRunConfiguration.kt` has a `ClassCastException` bug (missing `getOptionsClass()` override). When Ralph runs `./gradlew runIde` to test, this will crash. **This needs to be fixed in Kotlin first.**

### 2. PRD Dependency Cycles
If the PRD has circular dependencies, `get-next-story` will loop forever returning `$nil`. Add cycle detection:
```elvish
fn check-dependency-cycle {
  # Implement topological sort or cycle detection
}
```

### 3. Git State Corruption
If Claude crashes mid-commit, git state could be corrupted. Add:
```elvish
fn ensure-clean-git-state {
  var status = (git -C $project-root status --porcelain | slurp)
  if (not (eq $status "")) {
    ralph-warn "Git has uncommitted changes"
    # Optionally: git stash or abort
  }
}
```

### 4. Prompt Template Variables
If `{{VARIABLE}}` placeholders in prompt.md don't all get replaced, Claude might be confused. Validate all replacements succeeded.

### 5. Long-Running Stories
If a story takes multiple iterations, state could become stale. Consider:
- Adding state version checks
- Periodic state refresh
- Maximum attempts per story before marking blocked

## Code Style Improvements

### Use `nop` for Intentionally Ignored Output
```elvish
# Instead of:
git fetch > /dev/null 2>&1

# Use:
git fetch 2>&1 | nop
```

### Use `defer` for Cleanup (if available)
```elvish
fn with-temp-file {|callback|
  var f = (mktemp)
  defer { rm -f $f }
  $callback $f
}
```

### Group Related Functions
Consider splitting ralph.elv into modules:
- `ralph-git.elv` - Git operations
- `ralph-state.elv` - State management
- `ralph-ui.elv` - Output formatting
- `ralph.elv` - Main entry point

## Summary of Critical Fixes

1. **Line 505**: Fix pipe syntax for `str:trim-space`
2. **Lines 153-159**: Use `has-external` instead of `command -v`
3. **Line 231**: Add validation for empty split results
4. **Add timeout**: Prevent infinite Claude hangs
5. **Fix run configuration**: Add `getOptionsClass()` in Kotlin

## Recommended Priority

1. Fix the Kotlin run configuration bug first (blocking)
2. Fix line 505 pipe syntax bug
3. Add `has-external` checks
4. Add timeout for Claude
5. Improve error handling throughout
