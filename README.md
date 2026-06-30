# Library Catalog

Java command-line library catalog that models books and members, supports interactive checkout commands, and persists catalog state to a flat file.

## Stack
- Java 21+ compatible source level
- Maven
- JUnit 5

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

Package the application classes:

```powershell
.\mvnw.cmd -q -DskipTests package
```

Run the CLI help:

```powershell
java -cp target/classes src.LibraryCatalogApplication help
```

Invalid commands now return a stable non-zero exit code with a user-facing error message instead of a raw stack trace.

Seed a new catalog:

```powershell
java -cp target/classes src.LibraryCatalogApplication seed
```

Add a book and member:

```powershell
java -cp target/classes src.LibraryCatalogApplication add-book book-010 "Domain-Driven Design" "Eric Evans"
java -cp target/classes src.LibraryCatalogApplication add-member member-010 "Jamie Cross"
```

Checkout and inspect the catalog:

```powershell
java -cp target/classes src.LibraryCatalogApplication checkout book-010 member-010
java -cp target/classes src.LibraryCatalogApplication list-books
java -cp target/classes src.LibraryCatalogApplication list-members
java -cp target/classes src.LibraryCatalogApplication find-book "domain"
java -cp target/classes src.LibraryCatalogApplication find-member "jamie"
java -cp target/classes src.LibraryCatalogApplication loan-report
```

## CI
GitHub Actions runs `./mvnw -q test` on every push to `main` and every pull request targeting `main`.

## Deployed
Not deployed. This is a local Java command-line project.

## Architecture Notes
This build now uses a conventional Maven project layout instead of an ad hoc compile script and custom test runner. The application code lives under `src/main/java`, the tests live under `src/test/java`, and JUnit 5 now drives the test suite. That makes the project easier for a Java team to open, understand, and run in a standard way while keeping the existing domain, CLI, search, and reporting logic intact.

## Notes
- The project now uses Maven with JUnit 5 instead of the earlier custom test harness.
- The default catalog file is `data/library-catalog.txt`.
- Any command can target a different file with `--data <path>`.
- Commands currently supported: `help`, `seed`, `add-book`, `add-member`, `checkout`, `return`, `list-books`, `list-members`, `find-book`, `find-member`, and `loan-report`.
- Continuous integration lives in `.github/workflows/java-ci.yml` and runs `./mvnw -q test` on JDK 21.
- CLI boundary errors now return stable exit codes and clear messages with a `help` hint.
