# Track cost: Nexus

Measured 2026-10-06 to 2026-10-07.

| Scope | Cost | Active | Messages | Transcripts |
|---|--:|--:|--:|--:|
| Main session | $29.58 | 0.6 h | 293 | 1 |
| Subagents | $1.03 | 0.0 h | 25 | 2 |
| **Total** | **$30.62** | — | 318 | 3 |

> Active time is summed gaps under 5 minutes, so an idle session does not inflate it.
> Main and subagent time overlap when they run concurrently — do not add the two.

## Main session — by model

| Model | Msgs | Input | Output | Thinking | Cache write | Cache read | Cost |
|---|--:|--:|--:|--:|--:|--:|--:|
| `claude-opus-5-5` | 290 | 590 | 407,135 | 77,406 | 1,315,976 | 54,541,841 | $29.58 |
| `<synthetic>` | 3 | 0 | 0 | 0 | 0 | 0 | n/a |

## Subagents — by model

| Model | Msgs | Input | Output | Thinking | Cache write | Cache read | Cost |
|---|--:|--:|--:|--:|--:|--:|--:|
| `claude-opus-5-5` | 25 | 50 | 6,843 | 159 | 136,298 | 1,077,150 | $1.03 |

## Human time

_Not measured. Fill in by hand._

| Activity | Hours |
|---|--:|
| Review and correction | |
| Sandbox / Instruqt debugging | |

> The agent figures above are a **floor**, not the Track cost.

## Scope note

One session, worked from a different repo directory, so the transcripts live under
`-Users-nikk-source-temporal-ruby-temporal-devdays-ruby`. It covers the whole
Authoring, including the planning interview (started as a Ruby port before
switching to Kotlin) and the repo scaffolding committed to `main` beforehand. The
Track cost therefore slightly overstates the Track alone.
