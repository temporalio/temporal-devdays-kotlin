# Nexus in 10 minutes

**Level:** Advanced · **Duration:** 10 min · **Track:** `nexus-kotlin-10min`

A payments monolith runs the Compliance team's code inside the Payments Worker.
The Compliance team already runs its own Worker in its own Namespace behind a
Nexus Endpoint. The learner swaps the call from an Activity stub to a Nexus
stub, deletes the coupling, then takes Compliance down and watches payments wait
instead of fail.

Built from the two-hour [Decouple a Monolith with Nexus](https://github.com/temporalio/edu-nexus-code/tree/main/kotlin)
Workshop, keeping its scenario and code. Setup does everything the two-hour
version teaches by hand: the Endpoint, the contract and the handler are given.

## Challenges

| # | Challenge | Time | Learner does | Check |
|---|---|--:|---|---|
| 1 | Swap the Call | 5 min | TODO 1a, 1b, 1c; runs the Payments Worker and the starter | A completed payment has a Nexus Operation in its history |
| 2 | Break It | 5 min | Stops the Compliance Worker, sends payments, restarts it | Always passes |

The TODOs:

| TODO | File (under `exercise/src/main/kotlin/`) | Change |
|---|---|---|
| 1a | `payments/temporal/PaymentProcessingWorkflowImpl.kt` | Activity stub becomes a `ComplianceNexusService` stub with a 10-minute `scheduleToCloseTimeout` |
| 1b | same file | `complianceActivity` becomes `complianceService` at the call site |
| 1c | `payments/temporal/PaymentsWorkerApp.kt` | Delete the `ComplianceActivityImpl` registration |

The unfinished Exercise is a working monolith, so payments complete before and
after. Only the Nexus Operation in the history tells them apart, which is what
the test and the Challenge 1 check assert on.

## Layout

```
exercise/     learner starts here (the monolith)
solution/     completed counterpart
instruqt/     the Track: track.yml, config.yml, track_scripts/, 01-swap-the-call/, 02-break-it/
sandbox/      the Sandbox Image Dockerfile
diagrams/     the architecture diagram, served on port 8090
```

In the sandbox the Compliance Worker runs from `solution/` in a `tmux` session
named `compliance`, which the Compliance Worker tab attaches to. Running it from
`solution/` means nothing the learner types in `exercise/` can stop the other
team's service from compiling.

## Running it locally

You need JDK 21 and the [Temporal CLI](https://docs.temporal.io/cli).

```bash
temporal server start-dev
temporal operator namespace create --namespace payments-namespace
temporal operator namespace create --namespace compliance-namespace
temporal operator nexus endpoint create --name compliance-endpoint \
  --target-namespace compliance-namespace --target-task-queue compliance-risk

cd solution                      # or exercise
./gradlew complianceWorker       # terminal 2
./gradlew paymentsWorker         # terminal 3
./gradlew starter                # terminal 4
./gradlew test                   # no server needed
```

The Solution's tests run both Workers against an in-process test server joined
by a real Nexus Endpoint. The Exercise's tests fail until TODO 1 is done.

## Publishing the Track

1. Build and push the Sandbox Image by hand; the commands are at the top of
   [`sandbox/Dockerfile`](sandbox/Dockerfile). Make the GHCR package public, since
   Instruqt pulls anonymously.
2. Pin the digest in [`instruqt/config.yml`](instruqt/config.yml). Any change to
   `exercise/`, `solution/` or `diagrams/` needs a rebuild and a re-pin.
3. `cd instruqt && instruqt track push` the first time, and commit the ids and
   checksum it writes back. After that, merging to `main` pushes the Track through
   `.github/workflows/nexus-10min-track.yml` once `INSTRUQT_TOKEN` is set.
4. Test end to end with `instruqt track test --skip-fail-check`. Challenge 2's
   check always passes, which the default test protocol reads as a failure.
