# Library Catalog

Java command-line library catalog that models books and members, supports interactive checkout commands, and persists catalog state to a flat file.

## Stack
- Java 24
- Standard library only
- Plain `javac` / `java` workflow for iterations 1-2

## Setup
1. Install JDK 24 or a compatible recent JDK.
2. Clone the repository.
3. Open a terminal in the project root.

## Environment Variables
This project currently requires no environment variables. See `.env.example`.

## Running Locally
Compile the app and tests:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

Run the CLI help:

```powershell
java -cp out src.LibraryCatalogApplication help
```

Seed a new catalog:

```powershell
java -cp out src.LibraryCatalogApplication seed
```

Add a book and member:

```powershell
java -cp out src.LibraryCatalogApplication add-book book-010 "Domain-Driven Design" "Eric Evans"
java -cp out src.LibraryCatalogApplication add-member member-010 "Jamie Cross"
```

Checkout and inspect the catalog:

```powershell
java -cp out src.LibraryCatalogApplication checkout book-010 member-010
java -cp out src.LibraryCatalogApplication list-books
java -cp out src.LibraryCatalogApplication list-members
```

Run the tests:

```powershell
java -cp out tests.TestRunner
```

## CI
GitHub Actions runs the same compile and test commands on every push to `main` and every pull request targeting `main`.

## Deployed
Not deployed. This is a local Java command-line project.

## Architecture Notes
This build turns the library system into an actual command-line tool instead of a fixed demo. The domain and persistence layers from the first iteration stay intact, and a dedicated CLI parser plus an application service now load the saved catalog, run one command, and persist changes back to disk. That structure keeps command handling separate from business rules, so future work like search, reporting, or a different UI can reuse the same catalog core.

## Notes
- Iteration 1 uses a custom lightweight test runner because Maven is not installed locally.
- The default catalog file is `data/library-catalog.txt`.
- Any command can target a different file with `--data <path>`.
- Commands currently supported: `help`, `seed`, `add-book`, `add-member`, `checkout`, `return`, `list-books`, and `list-members`.
- Continuous integration lives in `.github/workflows/java-ci.yml` and uses JDK 24.
