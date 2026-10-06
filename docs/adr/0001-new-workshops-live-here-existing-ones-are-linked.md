# New Kotlin Workshops live in this repo; existing ones are linked, not moved

Some Kotlin Workshops already exist in their own topic repos, with Tracks and
publishing pipelines that work. This repo indexes and links to those rather than
copying or moving them. Every Workshop created for the Kotlin DevDays program
that has no existing home — whether a Port from another language or written new
for a customer request — lives directly in this repo, code, Track and Deck
together.

This follows `temporal-devdays-python` (its ADR 0001) and inverts the default of
`temporal-devdays-ts`, where a language repo holds Decks and an index, and code
living in it is the recorded exception. Kotlin content is expected to be built
per customer request faster than Temporal-owned topic repos appear for it, so
the exception would quickly become the norm.

"No existing home" is the test, not "new to Kotlin". A Port whose Workshop
already lives in a Temporal-owned topic repo with other language siblings and a
Track pipeline (for example `edu-nexus-code`) goes into that repo as a new
`kotlin/` sibling, and is linked from here. Everything else lands here.

A shorter Workshop built from an existing one — a different Duration, fewer
Challenges, the same scenario — is a Workshop in its own right with its own
Track, so it has no existing home and lands here even when its source is
linked. The index links both.

## Considered Options

Mirroring the TypeScript rule — topic repo first, this repo only as a fallback —
was rejected: most new Kotlin Workshops have no topic repo, and creating one per
Workshop means standing up a repository, CI and package permissions for each.

Consolidating every Kotlin Workshop here, including existing ones, was rejected:
it duplicates content that already has an owner and a working pipeline, and
splits a Workshop's Kotlin version away from its other languages.

## Consequences

This repo carries code, Gradle builds, Sandbox Image builds and Instruqt Track
publishing from the start, and needs its own track CI pipeline.

The index must make the split visible: for each Workshop, whether it lives here
or where it is linked to.
