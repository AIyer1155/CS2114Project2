# Controlled Chaos: Smart Playlist Manager
## CS 2114 Project 2 – Deliverable 3 Prototype + Tests

Group 11: Advay, Krishanth, Tyler

## Reflection

### What Changed From Specification?

**Singly-Linked vs. Doubly-Linked**
- Spec called for doubly-linked (prev/next pointers) for O(1) `nextSong()`/`previousSong()` navigation
- Provided LinkedChain is singly-linked (only next pointer)
- **Resolution**: Kept the provided singly-linked version and deferred nextSong/previousSong to Phase 2.

**LinkedChain Methods Used**
- Spec promised `add()` at O(1), but `add()` prepends to the front
- Used `addToEnd()` instead (O(n)) to keep songs in insertion order

**Class Structure**
- All classes built as specified: Song, PlaylistRule (abstract), four concrete rules, Playlist, ValidationResult
- The spec's GenericLinkedChain is the provided LinkedChain
- `ValidationResult.getViolations()` returns an array instead of a List, since LinkedChain only exposes `toArray()`
- LinkedChain is used for both songs and rules

**Validation Model**
- Playlist.validate() checks every song against every rule
- NoRepeatArtist needs the previous song, so validate() tracks it and does that check directly
- 16 of the spec's 18 bad-input cases are handled; the other two are for nextSong/previousSong

### What Changed in LinkedChain?

No modifications. We use addToEnd(), remove(), contains(), size(), isEmpty(), and toArray(). A doubly-linked version is planned for Phase 2.

### What Happened to Our Unknowns?

From the scope doc:
1. **"How do we store two types in the same collection?"** Generic LinkedChain<T>; `songs` and `rules` use the same class.
2. **"How do we validate without blocking?"** validate() is called explicitly after songs are added.
3. **"How do we handle duplicate rules?"** addRule() throws IllegalArgumentException if a rule of that type already exists.
4. **"How do we represent rule violations?"** ValidationResult holds a list of Violation (song, rule type) pairs.

### What Doesn't Work Yet?

**Not implemented:**
- nextSong() / previousSong() (deferred to Phase 2; O(n) with a singly-linked list)
- Mode 2 playlist generation (out of scope for MVP)
- Live suggestions during add (O(n² × r) cost)
- Collaborative playlists (out of scope)

**Working:**
- addSong, removeSong, addRule, removeRule, validate
- 16 of 18 bad-input cases from the spec (the two for nextSong/previousSong are deferred)
- All 4 rule types: NoRepeatArtist, BpmRange, NoExplicit, MoodConsistency
- 44 tests in PlaylistTest, all passing

---

## Next Steps

### What's Left to Finish the MVP?

1. **nextSong() / previousSong() Implementation** (Phase 2)
   - Current: LinkedChain is singly-linked; navigation is O(n)
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

### Task Assignments (Remaining Work)

| Task | Owner | Timeline |
|------|-------|----------|
| Phase 2a: Doubly-Linked LinkedChain | Advay | Oct 15 |
| Phase 2b: nextSong/previousSong | Advay | Oct 18 |
| Phase 3: File I/O + persistence | Tyler | Oct 18 |
| Phase 4: Console UI + menu loop | Krishanth | Oct 20 |
| Integration testing | All | Oct 22 |
| Final submission | All | Oct 23 (lab session) |

---

## Attribution

| Class / File | Owner | Contribution |
|--------------|-------|--------------|
| Song.java | Advay | Wrote; added null/BPM validation |
| PlaylistRule.java | Advay | Drafted abstract base class |
| NoRepeatArtist.java | Tyler | Implemented; noted context dependency |
| BpmRange.java | Advay | Wrote with minBpm/maxBpm bounds |
| NoExplicit.java | Tyler | Simple boolean flag validator |
| MoodConsistency.java | Advay | Wrote with null safety |
| ValidationResult.java | Advay | Designed; Violation inner class |
| Playlist.java | Advay | Orchestrator, validate() logic, all bad-input handling |
| PlaylistTest.java | Tyler | 44 tests; normal and bad-input cases |
| LinkedChain.java | CS 2114 Staff | Provided; used as-is |
| README.md | Tyler | Reflection, next steps, attribution |

---

## GenAI Use

**How GenAI was used:**
- Helped structure test cases (edge cases: empty collection, duplicates, nulls)
- Generated boilerplate for rule subclasses
- Suggested Big-O analysis justifications for LinkedChain methods
- Caught design issue: singly-linked LinkedChain doesn't support O(1) nextSong/previousSong

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

## Project Structure

```
LinkedChain.java        provided by CS 2114 staff
Song.java
PlaylistRule.java       abstract base class
NoRepeatArtist.java
BpmRange.java
NoExplicit.java
MoodConsistency.java
ValidationResult.java   result + Violation
Playlist.java
PlaylistTest.java       tests
README.md
```

To run the tests:

```
javac *.java
java PlaylistTest
```

