# Changelog

All notable changes to the Elvish JetBrains plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Syntax highlighting via custom lexer with full token support
- File type registration for `.elv` files
- LSP integration for Ultimate edition (completion, diagnostics, hover)
- TextMate bundle as fallback highlighting
- Settings panel for Elvish configuration
- Minimal parser for PSI tree structure

### Fixed
- Parser no longer generates false syntax errors
- Simplified parser accepts all valid Elvish syntax
- TextMate bundle properly packaged with language configuration

## [0.1.0] - TBD

### Added
- Initial release
- Basic file type support
- Syntax highlighting
- LSP integration (Ultimate only)
