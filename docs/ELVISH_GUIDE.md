# Elvish Language Quick Reference

This guide covers the Elvish shell language syntax and patterns used in ralph.elv.

## Variables

```elvish
# Declaration with var
var name = "value"
var count = (num 10)

# Assignment with set (variable must exist)
set name = "new value"

# Variable access with $ prefix
echo $name
put $count
```

## Data Types

### Strings
```elvish
# Single quotes - literal (no escapes)
var s1 = 'hello\nworld'  # Contains literal backslash-n

# Double quotes - with escapes
var s2 = "hello\nworld"  # Contains actual newline

# Barewords - simple text without quotes
echo hello
```

### Numbers
```elvish
var decimal = 42
var hex = 0xFF
var octal = 0o77
var binary = 0b1010
var float = 3.14
var rational = 1/2

# Convert string to number
var n = (num "42")
```

### Lists
```elvish
var list = [a b c]
var first = $list[0]        # Indexing (0-based)
var last = $list[-1]        # Negative indexing
var slice = $list[0..2]     # Slicing (exclusive end)

# Spread operator
var combined = [$@list more items]
```

### Maps
```elvish
var map = [&key=value &name=Alice &age=30]
var val = $map[key]         # Access by key

# Nested maps
var nested = [&outer=[&inner=value]]
put $nested[outer][inner]
```

## Functions

### Lambda (Anonymous Functions)
```elvish
# Basic lambda
var greet = {|name| echo "Hello "$name }
$greet Alice

# With optional/named parameters
var func = {|&opt=default| echo $opt }

# Rest parameters with @
var variadic = {|@args| put $@args }
```

### Named Functions
```elvish
fn greet {|name|
  echo "Hello "$name
}

fn add {|a b|
  + $a $b  # Returns the sum
}
```

## Control Flow

### If/Elif/Else
```elvish
if (eq $x 1) {
  echo "one"
} elif (eq $x 2) {
  echo "two"
} else {
  echo "other"
}
```

### For Loop
```elvish
# Iterate over list
for item [a b c] {
  echo $item
}

# With else (runs if container is empty)
for item $list {
  echo $item
} else {
  echo "empty list"
}
```

### While Loop
```elvish
var i = 0
while (< $i 10) {
  echo $i
  set i = (+ $i 1)
}
```

## Error Handling

### Try/Catch/Finally
```elvish
try {
  # Code that might fail
  risky-command
} catch e {
  # Handle error - $e is the exception
  echo "Error: "$e
} finally {
  # Always runs
  cleanup
}
```

### Exception Capture
```elvish
# Capture exception without control flow disruption
var result = ?(risky-command)
if (not-eq $result $ok) {
  echo "Command failed"
}
```

## Output and Capture

### Output Commands
```elvish
put $value      # Structured output (preserves type)
echo "text"     # Text output with newline
print "text"    # Text output without newline
```

### Capture Output
```elvish
# Capture command output as value(s)
var result = (command arg1 arg2)

# Capture with slurp (as single string)
var text = (cat file.txt | slurp)

# Capture multiple values
var values = [(some-command)]
```

## Pipelines

```elvish
# Standard pipe (stdout)
cat file.txt | grep pattern

# Value pipe (structured data)
put [1 2 3] | each {|x| * $x 2 }

# Parallel processing
put [1 2 3] | peach {|x| slow-operation $x }
```

## String Operations (str: module)

```elvish
use str

str:trim-space "  hello  "       # "hello"
str:split "," "a,b,c"            # [a b c]
str:join "," [a b c]             # "a,b,c"
str:replace &max=-1 "old" "new" $text  # Replace all
str:has-prefix $s "pre"          # Boolean check
str:has-suffix $s "suf"          # Boolean check
```

## Path Operations (path: module)

```elvish
use path

path:join $dir $file             # Join path components
path:dir "/foo/bar/file.txt"     # "/foo/bar"
path:base "/foo/bar/file.txt"    # "file.txt"
path:is-regular $path            # Check if regular file
path:is-dir $path                # Check if directory
```

## Regular Expressions (re: module)

```elvish
use re

re:match 'pattern' $string       # Returns bool
re:find 'pattern' $string        # Returns match info
re:replace 'pattern' replacement $string
```

## Common Patterns

### Checking Command Existence
```elvish
try {
  command -v some-cmd > /dev/null 2>&1
} catch {
  echo "Command not found"
}
```

### Reading JSON
```elvish
var data = (cat file.json | from-json)
put $data[key]
```

### Writing JSON
```elvish
var map = [&key=value]
put $map | to-json > file.json
```

### Working with External Commands
```elvish
# Suppress output
git status > /dev/null 2>&1

# Get exit status
try {
  some-command
} catch e {
  echo "Failed: "$e[reason]
}
```

### Iterating with Index
```elvish
var list = [a b c]
var i = 0
for item $list {
  echo $i": "$item
  set i = (+ $i 1)
}
```

## Gotchas and Tips

1. **No `&&` or `||`**: Use `and`/`or` commands or `try`/`catch`
2. **Variables must be declared**: Use `var` before first use, `set` for reassignment
3. **Quotes matter**: Barewords, single quotes, and double quotes behave differently
4. **Capture output**: Use `(command)` not backticks
5. **Spreading lists**: Use `$@list` to expand list elements as separate arguments
6. **Numeric comparison**: Use `<`, `>`, `<=`, `>=`, `==` (not `-lt`, `-gt` like bash)
7. **String comparison**: Use `eq`, `not-eq` for equality
8. **Boolean values**: `$true`, `$false`, not `true`/`false` barewords
9. **Nil value**: `$nil` (not `null` or `nil`)
10. **Return values**: Last expression value is implicitly returned, or use `put`
