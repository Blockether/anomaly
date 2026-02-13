# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [v1.0.1] - 2026-02-13

### Changed
- Add support for clojurescript
- Update release date for version 1.0.0
- Housekeeping

## [1.0.0] - 2026-02-13

### Added
- Initial release of anomaly library
- Standardized error categories based on Cognitect anomalies
- Support for all anomaly categories: unavailable, interrupted, incorrect, forbidden, unauthorized, not-found, conflict, fault, busy, unsupported
- HTTP status code mapping for each anomaly category
- Convenience functions for throwing anomalies (`not-found!`, `forbidden!`, `incorrect!`, etc.)
- Predicate functions for anomaly detection (`anomaly?`, `client-error?`, `server-error?`)
- `http-status` function to get HTTP status code from anomaly maps

[Unreleased]: https://github.com/Blockether/anomaly/compare/v1.0.1...HEAD
[1.0.0]: https://github.com/Blockether/anomaly/releases/tag/v1.0.0
[v1.0.1]: https://github.com/Blockether/anomaly/releases/tag/v1.0.1
