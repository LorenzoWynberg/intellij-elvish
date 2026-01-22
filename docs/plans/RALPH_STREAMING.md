# Ralph Streaming Output Plan

## Problem

When Ralph runs in AFK mode with `--print` flag, the terminal goes blank - no streaming output. You have no visibility into:
- Is Claude working?
- Is it stuck?
- Did something break?

You only find out when it finishes (or doesn't).

## Solution

Use Claude's `--output-format stream-json` with `jq` filtering to get real-time readable output while still capturing the final result for completion detection.

## Implementation

### Key Components

1. **Stream JSON format**: Claude outputs every message as JSON as it happens
2. **jq filtering**: Extract just the readable text from the verbose JSON stream
3. **tee to file**: Capture full output for completion signal detection
4. **grep line buffering**: Filter out non-JSON noise lines

### jq Filters

```bash
# Extract streaming text from assistant messages
stream_text='select(.type == "assistant").message.content[]? | select(.type == "text").text // empty | gsub("\n"; "\r\n") | . + "\r\n\n"'

# Extract final result for completion check
final_result='select(.type == "result").result // empty'
```

### Stream Filter Breakdown

| Part | Purpose |
|------|---------|
| `select(.type == "assistant")` | Only Claude's responses |
| `.message.content[]?` | Get content array |
| `select(.type == "text").text` | Extract text portions |
| `gsub("\n"; "\r\n")` | Fix line endings (cursor return bug) |
| `. + "\r\n\n"` | Add spacing between messages |

### Data Pipeline

```
Claude (stream-json)
  → grep '^{' (filter non-JSON lines)
  → tee $tmpfile (capture for later)
  → jq (stream filter → terminal)
```

## Changes to ralph.elv

### Current Approach (lines ~557-577)

```elvish
# Currently runs in quiet mode, captures to file
timeout $claude-timeout bash -c 'claude --dangerously-skip-permissions --print < "$1"' _ $prompt-tmp > $output-file 2>&1
```

### New Approach

```elvish
# Stream with real-time output
timeout $claude-timeout bash -c '
  tmpfile=$(mktemp)
  trap "rm -f $tmpfile" EXIT

  stream_text='\''select(.type == "assistant").message.content[]? | select(.type == "text").text // empty | gsub("\n"; "\r\n") | . + "\r\n\n"'\''

  claude --dangerously-skip-permissions --print --output-format stream-json < "$1" \
    | grep --line-buffered "^{" \
    | tee "$tmpfile" \
    | jq --unbuffered -rj "$stream_text"

  # Copy captured output for signal detection
  cp "$tmpfile" "$2"
' _ $prompt-tmp $output-file
```

### Considerations

1. **jq dependency**: Need to add `jq` to the dependency check (already have it)
2. **Quiet mode toggle**: Keep `--quiet` flag to disable streaming if desired
3. **Output file**: Still need to capture for `<story-complete>` signal detection
4. **Final result extraction**: Use `final_result` filter to get completion text

## Testing Plan

1. Run Ralph with streaming enabled
2. Verify real-time output appears in terminal
3. Verify completion signals still detected (`<story-complete>`, `<promise>COMPLETE</promise>`)
4. Test `--quiet` flag still works (disables streaming)
5. Test with long-running tasks to ensure no buffer issues

## Alternatives Considered

1. **Polling log file**: Tail the output file periodically - adds complexity, not real-time
2. **Named pipes**: More complex, potential blocking issues
3. **Background process with PTY**: Overkill for this use case

## References

- [Original article on Ralph streaming](https://www.yoursite.com/ralph-streaming)
- Claude `--output-format stream-json` documentation
- jq unbuffered streaming: `--unbuffered` flag

## Status

- [x] Implement streaming in ralph.elv
- [x] Add quiet mode toggle for streaming (`-q` / `--quiet` disables streaming)
- [ ] Test completion signal detection (manual testing needed)
- [ ] Update RALPH.md documentation
