# Library Catalog

Java command-line library catalog that models books and members, supports checkout rules, and persists catalog state to a flat file.

## Stack
- Java 24
- Standard library only
- Plain `javac` / `java` workflow for iteration 1

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

Run the application:

```powershell
java -cp out src.LibraryCatalogApplication
```

Run the tests:

```powershell
java -cp out tests.TestRunner
```

## Deployed
Not deployed. This is a local Java command-line project.

## Architecture Notes
This first build is the core of a small library system. It has separate model classes for books and members, a service that owns the checkout rules, and a persistence service that saves the catalog into a plain-text file so the state survives between runs. I kept the files small and focused so each class has one job and the next iteration can add commands, search, or better storage without rewriting the core.

## Notes
- Iteration 1 uses a custom lightweight test runner because Maven is not installed locally.
- The default catalog file is `data/library-catalog.txt`, and the CLI also accepts a custom output path as its first argument.
