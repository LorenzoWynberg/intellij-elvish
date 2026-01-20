# intellij-elvish

Elvish language support for JetBrains IDEs.

## Features

- File type recognition for `.elv` files
- Syntax highlighting (via LSP)
- Code completion, diagnostics, hover docs (via Elvish LSP)

## Requirements

### For Users
- JetBrains IDE 2024.3+
- [Elvish](https://elv.sh) installed and available in PATH

### For Development
- **Java 21** (required for building)
- Gradle 8.11+

## Building

```bash
# Ensure Java 21 is active
java -version  # Should show 21+

# Build the plugin
./gradlew build

# Run IDE with plugin for testing
./gradlew runIde
```

## Installation

1. Build the plugin: `./gradlew buildPlugin`
2. In your JetBrains IDE: Settings → Plugins → ⚙️ → Install Plugin from Disk
3. Select `build/distributions/intellij-elvish-*.zip`

## Resources

- [Elvish Shell](https://elv.sh)
- [Elvish Language Reference](https://elv.sh/ref/language.html)
