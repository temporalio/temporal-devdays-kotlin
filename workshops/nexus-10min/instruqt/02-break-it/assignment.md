---
slug: break-it
type: challenge
title: 2. Break It
teaser: Take the Compliance Worker down, send payments anyway, and watch them wait
  instead of fail.
notes:
- type: text
  contents: |-
    # The Compliance team is deploying

    You just moved compliance into another team's process. That team ships on
    Fridays.

    An HTTP call would return a connection error and you would be writing retry
    logic. This is not an HTTP call.
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
difficulty: basic
timelimit: 600
enhanced_loading: null
---

# Start Payments

In the [button label="Payments Worker" background="#444CE7"](tab-1) tab:

```bash,run
./gradlew paymentsWorker
```

# Take Compliance Down

Go to the [button label="Compliance Worker" background="#444CE7"](tab-2) tab and press
**Ctrl+C**.

Compliance is offline. Payments has no idea.

# Send Payments Anyway

In the [button label="Terminal" background="#444CE7"](tab-3) tab:

```bash,run
./gradlew starter
```

After 20 seconds the starter gives up waiting and reports both payments as
`STILL RUNNING`. Not failed.

# Look at What Did Not Happen

In the [button label="Temporal UI" background="#444CE7"](tab-4) tab, open
`payment-TXN-A` in `payments-namespace`.

Status is **Running**. The Event History ends at **Nexus Operation Scheduled** with no
completion. No connection error, no retry loop you wrote. The 10-minute
`scheduleToCloseTimeout` from TODO 1a is the whole outage budget.

# Bring Compliance Back

In the [button label="Compliance Worker" background="#444CE7"](tab-2) tab, press the
up arrow and Enter, or run:

```bash,run
./gradlew complianceWorker
```

Watch the Temporal UI. TXN-A completes and TXN-B is declined, exactly as if nothing had
happened. It can take up to a minute: the Operation retries with backoff while the
handler is down, so it does not resume the instant the Worker returns.

**The payments never failed. They waited.**

Click **Check**.

# What You Did

| Piece | What it did |
|---|---|
| `Workflow.newNexusServiceStub` | Replaced the Activity stub. One word changed at the call site |
| `NexusServiceOptions` on the Worker | Put the Endpoint name on the Worker, not the Workflow |
| Deleting `ComplianceActivityImpl` | Took Compliance code out of the Payments process |
| `scheduleToCloseTimeout` | Carried the payment through the outage |

Compliance now has its own Namespace, Task Queue and deployment schedule, and Payments
kept working while it was down.
