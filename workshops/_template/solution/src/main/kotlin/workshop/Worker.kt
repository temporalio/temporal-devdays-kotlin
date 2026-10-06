package workshop

import io.temporal.client.WorkflowClient
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.worker.WorkerFactory

fun main() {
    val client = WorkflowClient.newInstance(WorkflowServiceStubs.newLocalServiceStubs())
    val factory = WorkerFactory.newInstance(client)
    val worker = factory.newWorker(TASK_QUEUE)

    worker.registerWorkflowImplementationTypes(GreetingWorkflowImpl::class.java)
    worker.registerActivitiesImplementations(GreetingActivitiesImpl())

    factory.start()
    println("Worker listening on task queue '$TASK_QUEUE'")
}
