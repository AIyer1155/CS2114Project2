# Controlled Chaos: Smart Playlist Manager
## CS 2114 Project 2 – Deliverable 3 Prototype + Tests

Group 11: Advay, Krishanth, Tyler

## How to Compile and Run

All .java files are in one folder. The tests need JUnit 4.13.2 and Hamcrest 1.3 (junit-4.13.2.jar, hamcrest-core-1.3.jar) in that folder or on the classpath. On Windows, use ; instead of : between jar names.

Compile everything

javac -cp junit-4.13.2.jar:hamcrest-core-1.3.jar *.java

Run the program (builds a playlist, adds rules, validates, edits, validates again, then feeds in bad input)

java PlaylistApp

Run the tests

java -cp .:junit-4.13.2.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore SongTest PlaylistTest PlaylistRuleTest ValidationResultTest

Expected: OK (80 tests). The four test classes can also be run from an IDE as JUnit 4 tests.
## Reflection

### What Changed From Specification?

**Singly-Linked vs. Doubly-Linked**
- Spec called for doubly-linked (prev/next pointers) for O(1) `nextSong()`/`previousSong()` navigation
- Provided LinkedChain is singly-linked (only next pointer)
- **Resolution**: Kept the provided singly-linked version and deferred nextSong/previousSong to final build.

**LinkedChain Methods Used**
- Spec expected `add()` at O(1), but `add()` prepends to the front
- Used `addToEnd()` instead (O(n)) to keep songs in insertion order

**Class Structure**
- All classes built as specified: Song, PlaylistRule (abstract), four concrete rules, Playlist, ValidationResult
- The spec's GenericLinkedChain is the provided LinkedChain
- `ValidationResult.getViolations()` returns an array instead of a List, since LinkedChain only exposes `toArray()`
- Added `PlaylistApp`, a driver with a `main` method, so the MVP path can be run end to end without the (not yet built) console UI

**LinkedChain Reuse: three element types through one generic class**
- `LinkedChain<Song>` holds the songs in `Playlist`
- `LinkedChain<PlaylistRule>` holds the rules in `Playlist`
- `LinkedChain<ValidationResult.Violation>` holds the violations in `ValidationResult`
- There are no type-specific copies. All three use the same provided `LinkedChain<T>`, and only through its public methods.

**Validation Model**
- Playlist.validate() checks every song against every rule
- NoRepeatArtist needs the previous song, so validate() tracks it and does that check directly
- 16 of the spec's 18 bad-input cases are handled. The other two are for nextSong/previousSong
- Validation lives where the spec put it. Constructors check their own fields (`Song`, `Playlist`, `BpmRange`, `MoodConsistency`). `Playlist` checks null songs and rules, duplicate song ids, and duplicate rule types before anything reaches the LinkedChain.
- **Added during the prototype:** each rule's `validate(Song)` now throws `NullPointerException` for a null song, and `ValidationResult.addViolation` / `Violation` reject a null song or rule type. Before, these crashed with an unexplained NPE further down. Now they fail immediately with a clear message, consistent with the rest of the app.

**Tests**
- Every public app method has at least one normal case and one bad-input case. The tests also cover empty collections (validate/remove/getViolations on an empty playlist or result) and removing a song or rule that isn't there.
- 80 tests, all passed.

### What Changed in LinkedChain?

No modifications. We use addToEnd(), remove(), contains(), size(), isEmpty(), and toArray(). A doubly-linked version is planned for final build.

### What Happened to Our Unknowns?

From the scope doc:
1. **"How do we store two types in the same collection?"** Generic LinkedChain<T> `songs` and `rules` use the same class.
2. **"How do we validate without blocking?"** validate() is called explicitly after songs are added.
3. **"How do we handle duplicate rules?"** addRule() throws IllegalArgumentException if a rule of that type already exists.
4. **"How do we represent rule violations?"** ValidationResult holds a list of Violation (song, rule type) pairs.

### What Doesn't Work Yet?

**Not implemented:**
- nextSong() / previousSong() (deferred to Phase 2, O(n) with a singly-linked list)
- Mode 2 playlist generation (out of scope for MVP)
- Live suggestions during add (O(n² × r) cost)
- Collaborative playlists (out of scope)

**Working:**
- addSong, removeSong, addRule, removeRule, validate
- 16 of 18 bad-input cases from the spec (the two for nextSong/previousSong are deferred)
- All 4 rule types: NoRepeatArtist, BpmRange, NoExplicit, MoodConsistency
- `PlaylistApp` runs the full MVP path
- 80 JUnit tests across 4 test files, all passing

**Known limitation:**
- `removeRule` matches by object identity, because `PlaylistRule` doesn't override `equals`. You must pass the same rule instance you added. Removing "the BpmRange rule" by type isn't supported yet.

### Big-O of App Methods

n = songs, r = rules, v = violations.

| Method | Big-O | Why |
|--------|-------|-----|
| `Playlist.addSong` | O(n) | `contains()` duplicate check + `addToEnd()` |
| `Playlist.removeSong` | O(n) | `remove(T)` scans the chain |
| `Playlist.containsSong` | O(n) | `contains()` scans the chain |
| `Playlist.getSongCount` / `getRuleCount` | O(1) | `size()` |
| `Playlist.addRule` | O(r) | `toArray()` + type scan + `addToEnd()` |
| `Playlist.removeRule` | O(r) | `remove(T)` scans the chain |
| `Playlist.validate` | O(n·r + v²) | n·r rule checks. Each violation is `addToEnd()`'d, which is O(v), but v is at most n·r and usually small. |
| `ValidationResult.addViolation` | O(v) | `addToEnd()` |
| `ValidationResult.getViolations` | O(v) | `toArray()` |
| Rule `validate(Song)` | O(1) | Compares fields on one song |

**Compared to the spec:** the spec estimated O(1) for adding a song, assuming `add()`. We use `addToEnd()` plus a duplicate check, so `addSong` is O(n). `validate` matches the spec's O(n·r) for the rule checks.

---

## Next Steps

### What's Left to Finish the MVP?

1. **nextSong() / previousSong() Implementation** (Phase 2)
   - Current: LinkedChain is singly-linked, navigation is O(n)
   - Option A: Keep as-is, document O(n) in spec revision
   - Option B: Switch to doubly-linked LinkedChain (add prev pointer)
   - **Owner: Advay** (data structure)

2. **Playlist State Persistence** (Phase 3)
   - Save/load playlists to/from file
   - Serialization of Song and Rule objects
   - **Owner: Tyler** (file I/O, serialization)

3. **Console UI** (Phase 4)
   - Menu-driven interface
   - Add songs, add rules, run validation, show results
   - **Owner: Krishanth** (user interface)

### Which Stretch Goals?

| Goal | Status | Owner |
|------|--------|-------|
| Mode 2 Playlist Generation | **Dropping** | — |
| Live Suggestions | **Dropping** | — |
| Collaborative Playlists | **Dropping** | — |
| Custom Rule Builder | **Considering** | Tyler |
| Export Validation Report | **Committed** | Tyler |

### New Tests Needed?

For remaining MVP work:

| Method | Tests Needed |
|--------|--------------|
| nextSong() | Normal case (next element exists), edge case (at end of playlist) |
| previousSong() | Normal case (prev element exists), edge case (at start of playlist) |
| File I/O load() | Valid file, corrupt file, missing file |
| File I/O save() | Save and reload, data integrity check |
| Console UI menu | Parse user input, execute commands, handle invalid input |



## Attribution

| Class / File | Owner | Contribution |
|--------------|-------|--------------|
| Song.java | Krishanth | Wrote and added null/BPM validation |
| PlaylistRule.java | Advay | Drafted abstract base class |
| NoRepeatArtist.java | Tyler | Implemented and noted context dependency |
| BpmRange.java | Advay | Wrote with minBpm/maxBpm bounds |
| NoExplicit.java | Tyler | Simple boolean flag validator |
| MoodConsistency.java | Krishanth | Wrote with null safety |
| ValidationResult.java | Advay | Designed and violation inner class |
| Playlist.java | Advay | Orchestrator, validate() logic, all bad-input handling |
| PlaylistApp.java | Krishanth | Wrote MVP-path driver|
| PlaylistTest.java | Tyler, Advay | Test case for Playlist Methods
| SongTest.java | Advay | Split out of the original tests and added getter/equals/toString cases |
| PlaylistRuleTest.java | Whole team | Split out of the original tests and added null-song cases for each rule |
| ValidationResultTest.java | Advay | Split out of the original tests and added empty/null/Violation cases |
| LinkedChain.java | CS 2114 Staff | Provided and used as-is (unmodified) |
| README.md | Tyler | Reflection, next steps, attribution |
All files were given to GenAI for review and corrections. 
---

## GenAI Use

**How GenAI was used:**
- Helped structure test cases (edge cases: empty collection, duplicates, nulls)
- Suggested Big-O analysis justifications for LinkedChain methods
- Caught design issue: singly-linked LinkedChain doesn't support O(1) nextSong/previousSong
- Checked that all files for any and all errors and that the README matched rubric specs. 

**What we kept:**
- All rule implementations as designed
- Playlist.validate() logic (custom for spec requirements)
- Test framework and assertions

**What we changed:**
- Deferred doubly-linked list to Phase 2 after discovering singly-linked was provided
- Moved nextSong/previousSong to stretch goals (O(n) cost with singly-linked)
- Added explicit context tracking for NoRepeatArtist validation

**What didn't work:**
- GenAI suggestion to auto-replace duplicate rules (we throw an exception instead so behavior is predictable)
- Mode 2 playlist generation (decided too complex for MVP)

---

