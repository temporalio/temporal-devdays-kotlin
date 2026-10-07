# temporal-devdays-kotlin

Every Temporal DevDays Workshop in Kotlin: what exists, where each piece lives,
and what it cost to author.

All created via code. Use or repurpose the content to fit your next Dev Day!

## Workshops

| Workshop | Level | Stack | Deck | Code + Track | Lab | Duration | Author | Model | Deck cost ($) | Track cost ($) |
|---|---|---|---|---|---|--:|---|---|--:|--:|
| Nexus | Advanced | Kotlin · Nexus | Pending | [`workshops/nexus-10min/`](workshops/nexus-10min/) | Pending | 10 min | Nikolay Advolodkin | Opus 5.5 (100%) | Pending | [$30.62](docs/authoring/nexus-10min-track.md) |
| Decouple a Monolith with Nexus | Advanced | Kotlin · Nexus | [`edu-nexus-code/kotlin/slides`](https://github.com/temporalio/edu-nexus-code/tree/main/kotlin/slides) | [`edu-nexus-code/kotlin`](https://github.com/temporalio/edu-nexus-code/tree/main/kotlin) | Pending | 2 h | Nikolay Advolodkin | — | — | — |

A Workshop created for this program lives here, under `workshops/<slug>/` with
its Deck in `decks/<slug>/`. A Workshop that already has a Temporal-owned topic
repo stays there and is linked from the **Code + Track** column; its Deck may
still live here. See [ADR 0001](docs/adr/0001-new-workshops-live-here-existing-ones-are-linked.md).

Costs are agent tokens only, a floor, not the full cost. **Model** names every
model that spent tokens on the Authoring, with its share of the dollars, so two
costs can be compared like for like. See [`docs/authoring/`](docs/authoring/)
for how they are measured. Linked Workshops were authored outside this program
and show `—`.

**Every PR that adds a Deck or a Workshop adds its row here in the same PR.**
Fill in what the PR contains and mark the rest `Pending`. The **README index**
check runs `scripts/check-readme-index.sh` on every PR and fails it when a row is
missing; run it locally before opening the PR.

## Viewing a Deck

Every Deck is a self-contained [Slidev](https://sli.dev) project with the theme
vendored in. You need Node 18 or newer.

```bash
cd decks/<slug>
npm install
npm run dev
```

That opens `http://localhost:3030`; presenter mode is at `/presenter/`. Every
slide carries delivery notes there. `npm run export` produces a PDF.

## Starting a new Workshop

```bash
cp -R workshops/_template workshops/<slug>
cp -R decks/_template decks/<slug>
```

Then rename `workshop-slug` in both `settings.gradle.kts` files, `Shared.kt`
and the Deck's `package.json`, and add a row to the table above.

## Running a Workshop's code

You need JDK 21 and the [Temporal CLI](https://docs.temporal.io/cli). Gradle
comes from the wrapper in each tree.

```bash
temporal server start-dev            # terminal 1
cd workshops/<slug>/solution         # or exercise
./gradlew worker                     # terminal 2
./gradlew starter                    # terminal 3
./gradlew test                       # no server needed
```

The Solution's tests must pass. The Exercise's tests fail until its TODOs are
done. A Workshop's own `README.md` overrides these steps when it needs more
than one Worker or Namespace.

## Vocabulary and decisions

[`CONTEXT.md`](CONTEXT.md) is the glossary. [`docs/adr/`](docs/adr/) records the
decisions behind this layout.
