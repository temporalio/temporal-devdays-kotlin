---
slug: swap-the-call
type: challenge
title: 1. Swap the Call
teaser: Replace an in-process Activity call with a call across a Nexus boundary. One
  word changes at the call site.
notes:
- type: text
  contents: |-
    # Two teams, one process

    Payments calls a compliance check on every transaction. The check is the
    Compliance team's code, but it runs inside the Payments Worker.

    The Compliance team already has its own Worker, its own Namespace, and a Nexus
    Endpoint in front of it. It is running right now, waiting for a call that never
    comes.

    You are about to send it one.
tabs:
- title: Code
  type: service
  hostname: workshop
  path: /?folder=/root/workshop
  port: 8080
- title: Payments Worker
  type: terminal
  hostname: workshop
  workdir: /root/workshop/exercise
- title: Compliance Worker
  type: terminal
  hostname: workshop
  cmd: tmux new-session -A -s compliance -c /root/workshop/solution
- title: Terminal
  type: terminal
  hostname: workshop
  workdir: /root/workshop/exercise
- title: Temporal UI
  type: service
  hostname: workshop
  path: /
  port: 8233
- title: Architecture
  type: service
  hostname: workshop
  path: /monolith-architecture.html
  port: 8090
difficulty: intermediate
timelimit: 600
enhanced_loading: null
---

# Where You Start

Open the [button label="Architecture" background="#444CE7"](tab-5) tab. One Payments
Worker runs both teams' code. Step 2, the compliance check, is the Compliance team's
Activity running in the Payments process.

The [button label="Compliance Worker" background="#444CE7"](tab-2) tab shows the
Compliance team's own Worker, already running in `compliance-namespace` behind the Nexus
Endpoint `compliance-endpoint`.

# Swap the Stub (TODO 1a and 1b)

In the [button label="Code" background="#444CE7"](tab-0) tab, open:

`exercise/src/main/kotlin/payments/temporal/PaymentProcessingWorkflowImpl.kt`

Mind the `exercise/` prefix. The same file exists under `solution/` if you get stuck.

- **TODO 1a:** replace the `complianceActivity` Activity stub with a
  `complianceService` Nexus Service stub. The comment shows its shape, and the imports
  are already there.
- **TODO 1b:** change `complianceActivity` to `complianceService` at the call site. One
  word.

The editor highlights Kotlin but has no autocomplete, so copy the shape from the comment.

# Delete the Coupling (TODO 1c)

Open `exercise/src/main/kotlin/payments/temporal/PaymentsWorkerApp.kt` and delete the
line under **TODO 1c**. It registers the Compliance Activity on the Payments Worker.

Notice the line above it that you did not have to write: the Worker maps
`ComplianceNexusService` to the Endpoint `compliance-endpoint`. The Workflow names the
contract. The Worker names the Endpoint.

# Run It

In the [button label="Payments Worker" background="#444CE7"](tab-1) tab:

```bash,run
./gradlew paymentsWorker
```

Wait for `Payments Worker started`, then in the
[button label="Terminal" background="#444CE7"](tab-3) tab:

```bash,run
./gradlew starter
```

```bash,nocopy
  TXN-A   Result: COMPLETED             Risk: LOW
  TXN-B   Result: DECLINED_COMPLIANCE   Risk: HIGH
```

Same results as the monolith. The business logic did not change. Where it runs did.

# See the Boundary

In the [button label="Temporal UI" background="#444CE7"](tab-4) tab, open
`payment-TXN-A` in `payments-namespace`. Its Event History shows **Nexus Operation
Scheduled** and **Nexus Operation Completed** where an Activity used to be.

Switch the Namespace selector to `compliance-namespace`. The `ComplianceWorkflow` runs
are there, in the other team's Namespace.

If a payment sits in **Running** and never finishes, the Payments Worker tab will show
`endpoint "..." not found`. Check the Endpoint name, restart the Worker, and run the
starter again.

Click **Check**.
