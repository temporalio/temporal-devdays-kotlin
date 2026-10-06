package workshop

import io.temporal.activity.ActivityOptions
import io.temporal.workflow.Workflow
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod
import java.time.Duration

@WorkflowInterface
interface GreetingWorkflow {
    @WorkflowMethod
    fun run(input: GreetingInput): String
}

class GreetingWorkflowImpl : GreetingWorkflow {

    private val activities: GreetingActivities = Workflow.newActivityStub(
        GreetingActivities::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(10))
            .build(),
    )

    override fun run(input: GreetingInput): String {
        // TODO 1: Call the composeGreeting Activity and return its result.
        return activities.composeGreeting(input)
    }
}
