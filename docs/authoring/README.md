# Authoring cost records

One file per Authoring, holding two figures: the Deck cost and the Track cost.
Measure each part over the window it was built in:

```sh
node scripts/authoring-cost.mjs -Users-nikk-source-temporal-kotlin-temporal-devdays-kotlin \
  --part deck --since 2026-10-01 --until 2026-10-03 --label "<Workshop>" \
  > docs/authoring/<slug>-deck.md

node scripts/authoring-cost.mjs -Users-nikk-source-temporal-kotlin-temporal-devdays-kotlin \
  --part track --since 2026-10-04 --label "<Workshop>" \
  > docs/authoring/<slug>-track.md
```

Put each total in the README index's **Deck cost ($)** and **Track cost ($)**
columns.

The slug is the **working directory** under `~/.claude/projects`, so work an
Authoring from one repo, editing any other by absolute path. Otherwise its cost
splits across transcripts. Transcripts are local and age out: **run the script
at the end of the Authoring**, not weeks later.

The script prices only models listed in its `RATES` table and warns about any
other model it finds. Add a model's published rates there before trusting a
total.

Figures are agent tokens only — a floor. Fill in the human-time table by hand.
