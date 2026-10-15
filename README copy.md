# Controlled Chaos: Smart Playlist Manager
## CS 2114 Project 2, Deliverable 3: Prototype + Tests

Group 11: Advay, Krishanth, Tyler

## How to Compile and Run

All `.java` files are in one folder with `junit-4.13.2.jar` and `hamcrest-core-1.3.jar`. If the jars are missing:
```
curl -O https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
curl -O https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar
```

**Compile**
```
javac -cp junit-4.13.2.jar:hamcrest-core-1.3.jar *.java
```

**Run the program.** It builds a playlist, adds rules, validates, edits, validates again, then feeds in bad input.
```
java PlaylistApp
```

**Run the tests.** Expected output: `OK (80 tests)`.
```
java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore SongTest PlaylistTest PlaylistRuleTest ValidationResultTest
```

## Reflection


### What changed from our specification?

1. **`nextSong()`, `previousSong()`, and `currentPlaybackIndex` are not built.** The spec made them O(1) by assuming a doubly-linked chain we would write. The provided `LinkedChain` is singly-linked (a first node, no tail, no `prev`), so we deferred them.
2. **The spec's `GenericLinkedChain<T>` became the provided `LinkedChain<T>`.** The spec had an appending `add()` in O(1), an `iterator()`, and `NullPointerException` on null. The provided class has a prepending `add()`, an O(n) `addToEnd()`, no iterator, `toArray(T[])`, and `IllegalArgumentException` on null. We use `addToEnd()` to keep insertion order and `toArray()` to loop. `Playlist` checks null before calling the chain, so callers get the `NullPointerException` the spec names.
3. **`getViolations()` returns `Violation[]`, not `List<Violation>`,** because `LinkedChain` only offers `toArray`.
4. **The `NoRepeatArtist` check lives in `Playlist.validate()`, not in the rule.** `validate(Song)` cannot see the previous song, so the rule always returns true and `Playlist.validate()` compares artists using a `prevSong` variable. This is the one bad-input check not caught where the spec said. 
5. **Duplicate songs follow the spec, not the scope.** The scope accepted exact duplicates; spec case #2 rejects a repeated `id`, and we built the spec. 
6. **Mood is a nullable `String`,** as the spec allows. The scope's fixed tag list is not enforced. A song with no mood fails `MoodConsistency` instead of crashing. 
7. **Added null checks during the prototype.** Each rule's `validate(Song)` throws `NullPointerException` for a null song, and `addViolation` and `Violation` reject a null song or rule type. Before, they failed later with an unclear NPE. 
8. **Added `PlaylistApp`** (a fixed script that runs the MVP path) and **moved the tests to JUnit 4,** split into `SongTest`, `PlaylistTest`, `PlaylistRuleTest`, and `ValidationResultTest`.

### What changed in the LinkedChain?

Nothing; it is the provided file, unmodified. The app classes call only `addToEnd`, `remove(T)`, `contains`, `size`, `isEmpty`, and `toArray`, and no application logic lives in it.

One class stores three element types, with no type-specific copies: `LinkedChain<Song>` and `LinkedChain<PlaylistRule>` in `Playlist`, and `LinkedChain<Violation>` in `ValidationResult`.

Leaving it unchanged means `addToEnd` is O(n) and there is no `prev` pointer. Whether to modify it for the final is the first Next Steps item.

### What happened to our unknowns?

| Unknown from the scope | Status |
|---|---|
| Reject or replace a duplicate rule type | **Resolved: reject.** `addRule` throws `IllegalArgumentException`. Tested in `testAddDuplicateRuleType`. *[GenAI suggested; Group agreed]* |
| Where BPM and mood values come from | **Partly resolved.** The caller supplies them in code (hard-coded in `PlaylistApp` and the tests). Still open: user-entered values once there is an input loop. |
| How to generate a playlist (Mode 2) | **Open.** Not built. Idea: shuffle, call `validate()`, retry until it passes. |

### What doesn't work yet?

- `nextSong()` and `previousSong()` (spec cases #17 and #18). We handle 16 of the spec's 18 bad-input cases.
- Mode 2 playlist generation and live position suggestions, which the spec's Revised Scope moved out of the MVP.
- Input-loop cases from the scope: re-prompting on a non-numeric BPM or an invalid mood tag. The spec replaced them with exceptions.
- An interactive menu. `PlaylistApp` always runs the same script.
- **Known limitation:** `removeRule` matches by object identity because `PlaylistRule` has no `equals`. `removeRule(new BpmRange(80, 140))` does not remove an existing `BpmRange`.

**Tests.** All 80 pass. Every public method that takes input has a normal case and a bad-input case. No-argument methods get a normal case plus an edge case, such as `toString` on an empty playlist. The suite also covers empty collections and removing a song or rule that isn't there. Nothing tests `nextSong` or `previousSong` because they don't exist.

### Big-O of the app methods

n = songs, r = rules, v = violations.

| Method | Spec | Actual | Why |
|---|---|---|---|
| `Playlist.addSong` | O(1) | **O(n)** | `contains()` scan plus `addToEnd()` |
| `Playlist.removeSong` | O(n) | O(n) | `remove(T)` scans |
| `Playlist.containsSong` | n/a | O(n) | `contains()` scans |
| `Playlist.getSongCount`, `getRuleCount` | O(1) | O(1) | `size()` |
| `Playlist.addRule` | O(r) | O(r) | type scan plus `addToEnd()` |
| `Playlist.removeRule` | O(r) | O(r) | `remove(T)` scans |
| `Playlist.validate` | O(n × r × c) | **O(n·r + v²)** | n·r checks; each violation costs O(v) via `addToEnd()`. Worst case, every song breaks every rule: O((n·r)²). |
| `ValidationResult.addViolation` | n/a | O(v) | `addToEnd()` |
| `ValidationResult.getViolations` | n/a | O(v) | `toArray()` |
| Rule `validate(Song)` | O(1) | O(1) | compares fields of one song |

The spec's O(1) `addSong` assumed an appending `add()`, which the provided class doesn't have.

---

## Next Steps

### What is left to finish the MVP? (in order)

| # | Item | Owner |
|---|------|-------|
| 1 | **Decide how `LinkedChain` supports navigation:** (a) add a tail and `prev` pointer, which the Deliverable 2 handout allowed, or (b) leave it and track `currentPlaybackIndex` with `get(i)` in O(n). Then build `nextSong()`, `previousSong()`, and the responses for cases #17 and #18 (return `null` or throw `NoSuchElementException`). | Advay |
| 2 | Bring Big-O back toward the spec. With a tail pointer, `addSong` and `addViolation` become O(1); otherwise revise the spec's estimates. | Advay |
| 3 | Make `removeRule` work by type: give `PlaylistRule` an `equals` based on `getType()`, or add `removeRule(String type)`. Update `testRemoveRuleNotInPlaylist`, which relies on identity matching. | Tyler |
| 4 | Turn `PlaylistApp` into an interactive text menu: add and remove songs and rules, run `validate()`, print violations, re-prompt on bad input. | Krishanth |

### Which stretch goals will we attempt?

| Stretch goal | Decision | Owner |
|---|---|---|
| Mode 2: generate a playlist from the song library | **Committed**, after items 1 to 4; reuses `validate()` | Krishanth |
| Live position suggestions | **Dropped** (O(n² × r × c) per add) | n/a |
| GUI | **Dropped** | n/a |
| Multiple playlists | **Dropped** | n/a |
| Collaborative playlists, smart shuffle | **Dropped** | n/a |

### What new tests will we need?

| Method or case | Tests |
|---|---|
| `nextSong()` | normal: moves forward; bad: at the end, and on an empty playlist (#17) |
| `previousSong()` | normal: moves backward; bad: at the start, and on an empty playlist (#18) |
| `removeRule` by type | normal: removes that type; bad: type not present returns false, null throws |
| `addSong`, `addViolation` after any `LinkedChain` change | existing order, duplicate, and null tests still pass |
| Text menu | invalid choice, non-numeric BPM, and empty input re-prompt without crashing |
| Mode 2 | normal: valid playlist of the requested length; bad: length larger than the library, length of 0 or less, unsatisfiable rules (reports which failed) |

---

## Attribution

| File | Team member(s) | How |
|---|---|---|
| `LinkedChain.java` | CS 2114 staff | Provided; unmodified |
| `Song.java` | Krishanth | First version GenAI-generated; revised (nullable mood) and reviewed |
| `PlaylistRule.java` | Advay | First version GenAI-generated; revised (null contract) and reviewed |
| `NoRepeatArtist.java` | Tyler | First version GenAI-generated; context dependency found while implementing |
| `BpmRange.java` | Advay | First version GenAI-generated; reviewed |
| `NoExplicit.java` | Tyler | First version GenAI-generated; reviewed |
| `MoodConsistency.java` | Krishanth | First version GenAI-generated; revised (null-safe `validate`) |
| `ValidationResult.java` | Advay | First version GenAI-generated; revised (null checks on `Violation`) |
| `Playlist.java` | Advay | First version GenAI-generated; revised and reviewed; owns `validate()` and bad-input handling |
| `PlaylistApp.java` | Krishanth | Wrote the MVP-path driver |
| `PlaylistTest.java` | Tyler, Advay | Original tests converted to JUnit 4 and extended |
| `SongTest.java` | Advay | Split from the original tests; added getter, `equals`, `toString` cases |
| `PlaylistRuleTest.java` | Whole team | Split from the original tests; added null-song cases for each rule |
| `ValidationResultTest.java` | Advay | Split from the original tests; added empty, null, and `Violation` cases |
| `README.md` | Tyler | Reflection, next steps, attribution; checked against the rubric |

### How we used GenAI

We used Claude Code to generate the first version of every app class and the original tests from our Deliverable 2 spec and the provided `LinkedChain.java`. The team then reviewed, ran, and edited the code, and gave all files back to GenAI to review for errors and check the README against the rubric.

- **Helped:** it noticed `LinkedChain` is singly-linked while our spec assumed doubly-linked; it suggested rejecting duplicate rules instead of replacing them; it produced the edge-case tests (empty playlist, no rules, removing something not present, boundary BPM).
- **Did not work:** the first test file used JUnit annotations that would not compile where it was generated, so it was briefly a plain runner before JUnit 4. One generated test had a wrong expected violation count, which we found by running it. The first README draft listed work outside our scope (file saving, a console UI, custom rules), so we rewrote it from the scope and spec.
- **Kept:** the rule classes and the `Playlist.validate()` design. **Changed:** the null handling, the tests, and the plan for `LinkedChain`.