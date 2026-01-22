# Elvish Ecosystem

Companion tools from the Elvish community. These are **shell/terminal tools** that work alongside this IDE plugin.

> Source: [awesome-elvish](https://github.com/elves/awesome-elvish)

## Shell Completions

Enhance tab-completion in your terminal (not IDE - the plugin uses LSP for that):

| Tool | Description |
|------|-------------|
| [carapace-bin](https://github.com/rsteube/carapace-bin) | Completions for 400+ commands. Highly recommended. |
| [elvish-completions](https://github.com/zzamboni/elvish-completions) | Completions for cd, git, vcsh |
| [elvish-bash-completion](https://github.com/aca/elvish-bash-completion) | Convert any bash completion to Elvish |

## Prompt Themes

Customize your terminal prompt:

| Tool | Description |
|------|-------------|
| [Starship](https://starship.rs) | Cross-shell, minimal, fast prompt (Rust) |
| [oh-my-posh](https://ohmyposh.dev) | Cross-shell, highly configurable prompt (Go) |
| [chain](https://github.com/zzamboni/elvish-themes) | Configurable Elvish prompt with Git support |
| [powerline](https://github.com/muesli/elvish-libs) | Powerline style prompt |

## Directory Navigation

| Tool | Description |
|------|-------------|
| [zoxide](https://github.com/ajeetdsouza/zoxide) | Smart cd that learns your habits |
| [direlv](https://github.com/tesujimath/direlv) | Directory-specific environments (like direnv) |
| [dir module](https://github.com/zzamboni/elvish-modules) | Directory stack, `cd -` support |

## Development Environments

| Tool | Description |
|------|-------------|
| [virtualenv](https://github.com/tesujimath/bash-env-elvish) | Python virtualenv support |
| [nvm](https://github.com/tesujimath/bash-env-elvish) | Node Version Manager support |
| [mamba](https://github.com/iandol/elvish-modules) | Conda/mamba/micromamba support |
| [nix](https://github.com/zzamboni/elvish-modules) | Nix package manager utilities |
| [elvish.nix](https://github.com/tesujimath/elvish.nix) | Nix support for Elvish packages |

## Testing Frameworks

| Tool | Description |
|------|-------------|
| [elvish-tap](https://github.com/tesujimath/elvish-tap) | Test Anything Protocol (TAP) for Elvish |
| [velvet](https://github.com/giancosta86/velvet) | Functional testing framework |

## Utility Modules

| Module | Description |
|--------|-------------|
| [bang-bang](https://github.com/zzamboni/elvish-modules) | `!!` and `!$` keybindings |
| [alias](https://github.com/zzamboni/elvish-modules) | Persistent aliases, bash-style parsing |
| [long-running-notifications](https://github.com/zzamboni/elvish-modules) | Notify when long commands finish |
| [spinners](https://github.com/zzamboni/elvish-modules) | Progress spinners for scripts |
| [terminal-title](https://github.com/zzamboni/elvish-modules) | Auto-set terminal title |
| [iterm2](https://github.com/zzamboni/elvish-modules) | iTerm2 shell integration (macOS) |

## Configuration Examples

Learn from well-documented configs:

| Resource | Description |
|----------|-------------|
| [dot_elvish](https://github.com/zzamboni/dot_elvish) | Comprehensive, well-documented rc.elv |
| [oh-my-elvish](https://github.com/darcy-shen/oh-my-elvish) | User-friendly configuration framework |

## Installing Packages

Elvish has a built-in package manager:

```elvish
# Install a package
use epm
epm:install github.com/zzamboni/elvish-modules

# Use a module from the package
use github.com/zzamboni/elvish-modules/alias
```

## See Also

- [Elvish Official Docs](https://elv.sh/ref/)
- [awesome-elvish](https://github.com/elves/awesome-elvish) - Full community resource list
- [Elvish Discord](https://elv.sh/ref/community.html) - Community chat
