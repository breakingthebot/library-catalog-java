# Library Catalog

Java command-line library catalog that models books and members, supports interactive checkout commands, and persists catalog state to a flat file.

## Stack
- Java 21+ compatible source level
- Maven
- JUnit 5
- Executable jar packaging via Maven JAR Plugin

## Setup
1. Install JDK 21 or a compatible newer JDK.
2. Clone the repository.
3. Open a terminal in the project root.
4. Use the committed Maven wrapper scripts instead of requiring a separate Maven install.

## Environment Variables
This project currently requires no environment variables. See `.env.example`.

## Running Locally
Run the Maven test suite:

```powershell
.\mvnw.cmd test
```

Build the executable jar:

```powershell
.\mvnw.cmd -q package
```

Run the CLI help:

```powershell
java -jar target/library-catalog.jar help
```

Print the packaged application version:

```powershell
java -jar target/library-catalog.jar --version
```

Invalid commands now return a stable non-zero exit code with a user-facing error message instead of a raw stack trace.

Bootstrap a missing catalog with sample data only on first run:

```powershell
java -jar target/library-catalog.jar bootstrap --data temp-catalog.txt
```

Seed a new catalog:

```powershell
java -jar target/library-catalog.jar seed
```

Add a book and member:

```powershell
java -jar target/library-catalog.jar add-book book-010 "Domain-Driven Design" "Eric Evans"
java -jar target/library-catalog.jar add-member member-010 "Jamie Cross"
```

Checkout and inspect the catalog:

```powershell
java -jar target/library-catalog.jar checkout book-010 member-010
java -jar target/library-catalog.jar list-books
java -jar target/library-catalog.jar list-members
java -jar target/library-catalog.jar find-book "domain"
java -jar target/library-catalog.jar find-member "jamie"
java -jar target/library-catalog.jar loan-report
```

## CI
GitHub Actions runs `./mvnw -q package` on every push to `main` and every pull request targeting `main`.

## Deployed
Not deployed. This is a local Java command-line project.

## Architecture Notes
This build now uses a conventional Maven project layout instead of an ad hoc compile script and custom test runner. The application code lives under `src/main/java`, the tests live under `src/test/java`, and JUnit 5 now drives the test suite. On top of that, the Maven build now produces a runnable jar, so the project is no longer just source you can build; it is a CLI artifact you can package and launch directly with `java -jar`. The internal Java packages now use a real namespace, `com.breakingthebot.librarycatalog`, which makes the codebase look and behave like a conventional Java project instead of a temporary scaffold.

## Notes
- The project now uses Maven with JUnit 5 instead of the earlier custom test harness.
- The default catalog file is `data/library-catalog.txt`.
- Any command can target a different file with `--data <path>`.
- Commands currently supported: `help`, `version`, `--version`, `bootstrap`, `seed`, `add-book`, `add-member`, `checkout`, `return`, `list-books`, `list-members`, `find-book`, `find-member`, and `loan-report`.
- Continuous integration lives in `.github/workflows/java-ci.yml` and runs `./mvnw -q package` on JDK 21.
- CLI boundary errors now return stable exit codes and clear messages with a `help` hint.
- `bootstrap` seeds sample data only when the target catalog file does not already exist; it never overwrites an existing file.
- The primary distributable artifact is `target/library-catalog.jar`.
- The code now uses the `com.breakingthebot.librarycatalog` package namespace across production and test code.
- `--version` reads Maven metadata from the packaged artifact and prints the current application version.
