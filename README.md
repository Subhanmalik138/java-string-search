# java-string-search

Three classic single-pattern string search algorithms behind one
interface, with a CLI that can run them side by side. The point of the
project is not just the algorithms — it is that they are interchangeable:
same contract, same edge-case semantics, and a test that proves they
never disagree with each other.

## What it does

- **KMP** (`kmp`) — linear O(n + m) time via the prefix function; never
  re-scans matched text.
- **Boyer-Moore-Horspool** (`bmh`) — skips whole windows using only the
  bad-character rule; sublinear on typical text, O(n * m) worst case.
- **Rabin-Karp** (`rabin-karp`) — O(1) rolling hash per window, with a
  full comparison on every hash match so collisions cost time, never
  correctness.
- All three return every occurrence, including overlapping ones, in
  ascending order. Empty patterns, empty texts, and patterns longer
  than the text match nothing.
- A CLI that searches a file or stdin, with `--ignore-case` and an
  `all` mode that runs every algorithm and warns if they disagree.

## Design decisions

1. **One interface, three implementations.** Callers code against
   `SearchAlgorithm`, so the choice of algorithm is a one-line change
   and the cross-check test can iterate over all of them. That is also
   what makes the `all` CLI mode trivial.
2. **Empty pattern matches nothing, by design.** The "matches at every
   position" convention is mathematically tidy and useless in a search
   tool; returning no matches is the least surprising behaviour for a
   CLI user piping logs through it.
3. **Case folding lives in the CLI, not the algorithms.** Folding
   inside each algorithm would duplicate locale handling three times
   and muddy the complexity story. The algorithms stay exact and
   case-sensitive; `Main` folds both strings with `Locale.ROOT` when
   `--ignore-case` is passed.
4. **Rabin-Karp verifies every hash hit.** Skipping verification would
   be faster and wrong on collisions. The hash is only a filter; the
   character comparison is the decision.
5. **BMH uses a map for its shift table, not a 65,536-entry array.**
   Short patterns keep a small table, and the full `char` range works
   without paying for an alphabet that never appears.

## Build and run

Requires JDK 17+.

```bash
./build.sh            # compile + build out/string-search.jar
./build.sh test       # compile + run the JUnit 5 suite
```

The test run uses the JUnit Platform console standalone jar (downloaded
from Maven Central on first use, or set `JUNIT_JAR` to point at one you
already have).

## Usage

```bash
java -jar out/string-search.jar kmp ERROR server.log
# kmp: 3 matches at 12, 480, 1021

echo "mississippi" | java -jar out/string-search.jar all issi
# kmp: 2 matches at 1, 4
# bmh: 2 matches at 1, 4
# rabin-karp: 2 matches at 1, 4

java -jar out/string-search.jar --ignore-case bmh error server.log
```

From Java code:

```java
SearchAlgorithm search = new KnuthMorrisPratt();
search.findAll("aaaaa", "aaa");   // [0, 1, 2]
```

## Tech stack

- Java 17+ (no external dependencies in the main code)
- JUnit 5 for tests
