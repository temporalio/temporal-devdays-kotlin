# AGENTS.md

This repo holds the Kotlin half of the Temporal DevDays program: Workshops
(Deck, Track, Exercise and Solution) that are assembled into customer Dev Days.

Read [`CONTEXT.md`](CONTEXT.md) before writing anything, and use its terms
exactly — Workshop, Dev Day, Track, Challenge, Deck, Exercise, Solution, Level,
Authoring, Port. Read [`docs/adr/`](docs/adr/) before moving content between
repos.

## House rules

- **No customer names**, anywhere: code, Decks, Tracks, ADRs, commit messages.
  Refer to "a customer Dev Day" and a date instead.
- **One SDK per Workshop.** A Workshop teaches Kotlin on the Temporal Java SDK
  and never names the other SDKs or other-language versions of itself.
- **Exercise and Solution are structurally identical.** Same files, same names,
  same tests. The Exercise differs only where a numbered TODO is left undone.
- **TODOs are numbered** (`TODO 1`, `TODO 2a`, `TODO 2b`) and match the Track's
  Challenge assignments. Stretch TODOs are marked `STRETCH` and nothing later
  depends on them.
- **Every Workshop has one Level**: Foundations or Advanced.
- **Solutions are proven**: `./gradlew test` in `solution/` must pass, using
  `TestWorkflowEnvironment` rather than a live server.
- **Every code slide is a Code Walkthrough** (`layout: code-walkthrough`): one
  code block, one note per highlight, in execution order. Notes are one or two
  sentences; the full explanation goes in the presenter notes.
- **Every PR that adds a Deck or a Workshop adds its README row** in the same
  PR. Fill in what the PR contains, mark the rest `Pending`, and run
  `bash scripts/check-readme-index.sh`. The README index check fails the PR
  otherwise.
- New Workshops start from `workshops/_template/` and `decks/_template/`.

## Layout

```
workshops/<slug>/
  exercise/    learner starts here
  solution/    completed counterpart
  instruqt/    the Track
  sandbox/     the Sandbox Image build
decks/<slug>/  the Slidev Deck
docs/adr/      decisions
docs/authoring/ cost records
scripts/       tooling
```

## Toolchain

Kotlin on JDK 21 with the Gradle wrapper, one Gradle build per Exercise and
Solution tree, `io.temporal:temporal-sdk`, tests on JUnit Jupiter with
`io.temporal:temporal-testing`. Decks are Slidev on Node 18+.
