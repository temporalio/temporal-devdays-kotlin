package workshop

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import java.util.UUID
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val name = args.firstOrNull() ?: "Temporal"
    val client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs())
    val workflow = client.newWorkflowStub(
        GreetingWorkflow::class.java,
        WorkflowOptions.newBuilder()
            .setTaskQueue(TASK_QUEUE)
            .setWorkflowId("greeting-${UUID.randomUUID()}")
            .build(),
    )
    println(workflow.run(GreetingInput(name)))
    exitProcess(0)
}
