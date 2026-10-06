---
layout: code-walkthrough
heading: The Workflow
notes:
  - "One note per highlight, in the order the code <strong>runs</strong>"
  - "A note is one or two sentences. The full explanation goes in the presenter notes"
  - "Notes are HTML: use <code>&lt;code&gt;</code> and <code>&lt;strong&gt;</code>, not markdown"
---

::code::

```kotlin {all|1|2|4-5}
class GreetingWorkflowImpl : GreetingWorkflow {
    private val activities = Workflow.newActivityStub(GreetingActivities::class.java, options)

    override fun run(input: GreetingInput): String =
        activities.composeGreeting(input)
}
```

<!--
Code Walkthrough: one code block, one highlight and one note per click. Click 0
shows the whole block; each `|` range after `all` is one click and one entry in
`notes`, so the two lists must be the same length.

Source: say which file and lines the block quotes, and what was trimmed.

- **Build 1 - ...** The full explanation of the first highlight.
-->
