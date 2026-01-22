# Marketing & Launch Plan

## Feature Ideas for Future Releases

### High Value (LSP-powered)
| Feature | Description | Effort |
|---------|-------------|--------|
| **Find Usages** | Find all references to a variable/function | Low - LSP supports it |
| **Rename Refactoring** | Rename symbol across files | Low - LSP supports it |
| **Semantic Highlighting** | Richer colors using LSP semantic tokens | Medium |
| **Inlay Hints** | Show parameter names inline | Medium |
| **Signature Help** | Show function signatures while typing | Low - LSP supports it |

### Editor Enhancements
| Feature | Description | Effort |
|---------|-------------|--------|
| **More Live Templates** | `pipe`, `try`, `each`, `peach`, `defer` | Low |
| **Surround With** | Wrap selection in `{ }`, `try { }`, etc. | Low |
| **Smart Enter** | Auto-continue pipelines, lists | Medium |
| **TODO Highlighting** | Highlight TODO/FIXME in comments | Low |
| **Spell Checking** | Check spelling in comments/strings | Low |

### Advanced Features
| Feature | Description | Effort |
|---------|-------------|--------|
| **REPL Integration** | Interactive Elvish shell in IDE | High |
| **Module Browser** | Browse/search Elvish modules | Medium |
| **Debugger** | Step through Elvish scripts | Very High |
| **Package Manager** | Install/manage Elvish packages | Medium |
| **rc.elv Editor** | Special support for config file | Low |

### Nice to Have
| Feature | Description | Effort |
|---------|-------------|--------|
| **Color Preview** | Show color swatches for ANSI codes | Low |
| **Path Completion** | Autocomplete file paths | Medium |
| **Environment Variables** | Show env var values on hover | Medium |
| **Shell History** | Browse command history | Medium |

---

## Marketplace Listing

### Plugin Name
**Elvish Language Support**

### Tagline
> Full-featured Elvish shell support with LSP-powered intelligence

### Short Description (400 chars)
```
Syntax highlighting, code completion, diagnostics, and navigation for Elvish shell scripts.
Powered by Elvish's built-in LSP server for accurate, real-time intelligence.
Includes run configurations, live templates, code folding, and structure view.
Works with all JetBrains IDEs 2024.3+.
```

### Full Description
```markdown
# Elvish Language Support

Comprehensive support for the [Elvish shell](https://elv.sh) in JetBrains IDEs.

## Features

### Intelligent Editing
- **Code Completion** - Variables, functions, modules, and builtins
- **Real-time Diagnostics** - Syntax errors as you type
- **Hover Documentation** - Quick docs for any symbol
- **Go to Definition** - Jump to function/variable declarations

### Editor Features
- **Syntax Highlighting** - Full TextMate grammar support
- **Code Folding** - Collapse functions, lambdas, and blocks
- **Structure View** - Navigate functions and variables
- **Breadcrumbs** - See your location in nested code
- **Brace Matching** - Highlight matching brackets

### Productivity
- **Run Configurations** - Execute scripts with one click
- **Gutter Icons** - Run button next to fn definitions
- **Live Templates** - Quick snippets (fn, if, for, use)
- **File Templates** - New Elvish Script/Module wizards
- **Commenter** - Toggle comments with Cmd+/

## Requirements
- JetBrains IDE 2024.3 or later
- [Elvish](https://elv.sh) installed (for LSP features)

## Getting Started
1. Install Elvish: `brew install elvish` (macOS) or see [installation guide](https://elv.sh/get/)
2. Open any `.elv` file
3. The LSP server starts automatically

## Links
- [Elvish Shell](https://elv.sh)
- [GitHub Repository](https://github.com/LorenzoWynberg/intellij-elvish)
- [Report Issues](https://github.com/LorenzoWynberg/intellij-elvish/issues)
```

### Tags
`elvish`, `shell`, `scripting`, `lsp`, `syntax-highlighting`, `code-completion`

### Category
**Languages**

---

## Screenshots Needed

1. **Hero Shot** - Editor with syntax highlighting + completion popup
2. **Diagnostics** - Red squiggles with error tooltip
3. **Hover Docs** - Hover over builtin showing documentation
4. **Structure View** - Side panel with functions/variables
5. **Run Configuration** - Run dialog with Elvish config
6. **Go to Definition** - Ctrl+click navigation
7. **Live Templates** - Template expansion in action

### Screenshot Specs
- 1280x800 or 1600x1000 recommended
- Dark theme (Darcula) preferred
- Clean, focused content
- Highlight the feature with annotations if needed

---

## Launch Checklist

### Pre-Launch
- [ ] Build final plugin: `./gradlew buildPlugin`
- [ ] Test on IntelliJ IDEA Community
- [ ] Test on PyCharm Community
- [ ] Test on WebStorm (trial)
- [ ] Create JetBrains Hub account
- [ ] Prepare vendor info (name, email, URL)
- [ ] Take all screenshots
- [ ] Write changelog for v1.0.0

### Marketplace Submission
- [ ] Upload plugin ZIP
- [ ] Fill in all metadata
- [ ] Add screenshots
- [ ] Set pricing (Free)
- [ ] Submit for review

### Post-Launch
- [ ] Announce on Reddit r/elvish
- [ ] Post on Elvish Discord/Matrix
- [ ] Tweet/post about it
- [ ] Add badge to GitHub README
- [ ] Monitor reviews and respond

---

## Version Roadmap

### v1.0.0 (Launch)
- File type recognition
- Syntax highlighting
- LSP integration (completion, diagnostics, hover, go-to-def)
- Run configurations
- Live/file templates
- Editor features (folding, structure, breadcrumbs)

### v1.1.0 (Fast Follow)
- Find usages
- Rename refactoring
- Signature help
- More live templates

### v1.2.0 (Polish)
- Semantic highlighting
- Inlay hints
- TODO highlighting
- Spell checking

### v2.0.0 (Advanced)
- REPL integration
- Module browser
- rc.elv special support

---

## Metrics to Track

- Downloads per week
- Active users (via anonymous stats if enabled)
- GitHub stars
- Marketplace rating
- Issue volume

---

## Community Outreach

### Elvish Community
- [Elvish GitHub Discussions](https://github.com/elves/elvish/discussions)
- Elvish Matrix/IRC channels
- r/elvish subreddit

### JetBrains Community
- JetBrains Plugin Developers Slack
- IntelliJ Plugin Development forum

### Content Ideas
- Blog post: "Building an LSP-powered JetBrains Plugin"
- Tutorial: "Getting Started with Elvish in IntelliJ"
- Video: Quick demo of features
