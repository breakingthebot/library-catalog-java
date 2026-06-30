# Changelog

## 2026-06-30
- Scaffolded the project with `.gitignore`, MIT `LICENSE`, `.env.example`, `README.md`, and `CHANGELOG.md`.
- Added Java domain models for books, members, and catalog state snapshots.
- Added an in-memory catalog service with checkout and return rules.
- Added a flat-file persistence service for saving and loading catalog state.
- Added a CLI entry point that demonstrates registration, checkout, save, and reload.
- Added a lightweight custom Java test runner with coverage for models, services, and utility encoding logic.
- Replaced the demo-only entry point with an interactive CLI parser and command execution layer.
- Added `help`, `seed`, `add-book`, `add-member`, `checkout`, `return`, `list-books`, and `list-members` commands.
- Added tests for CLI parsing, console formatting, and persisted command workflows.
- Added a GitHub Actions workflow that compiles the project and runs `tests.TestRunner` on pushes and pull requests.
- Added `find-book` and `find-member` commands for case-insensitive catalog search across persisted data.
- Added search-specific utility and tests for parser, formatter, service, and CLI search behavior.
- Added a `loan-report` command that lists active loans as book/member pairs from persisted checkout state.
- Added a loan read model and tests for report formatting, parser support, service derivation, and CLI output.
- Migrated the project to standard Maven layout with `src/main/java` and `src/test/java`.
- Replaced the custom test runner with JUnit 5 and added a `pom.xml` for standardized builds.
- Updated GitHub Actions to run the Maven test suite on JDK 21.
- Added a top-level application runner that translates parse, validation, and IO failures into stable user-facing CLI errors.
- Added tests for successful execution, unknown commands, and business-rule failures at the application boundary.
- Added a `bootstrap` command that seeds sample data only when the target catalog file is missing.
- Added tests for bootstrap parsing, first-run initialization, and idempotent skip behavior for existing files.
- Added Maven JAR plugin configuration so packaging produces a runnable `target/library-catalog.jar`.
- Updated CI and local docs to validate and launch the executable jar with `java -jar`.
- Renamed Java packages from `src.*` and `tests.*` to the real application namespace `com.breakingthebot.librarycatalog.*`.
- Updated the runnable jar manifest to use the renamed main application class.
