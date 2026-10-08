package compliance.temporal

import compliance.ComplianceChecker
import compliance.temporal.activity.ComplianceActivityImpl
import compliance.temporal.workflow.ComplianceWorkflowImpl
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.worker.WorkerFactory

/**
 * [GIVEN] The Compliance team's Worker. Handles Nexus requests from Payments.
 */
fun main() {
    // Client: scoped to the Compliance team's own Namespace.
    val client = WorkflowClient.newInstance(
        WorkflowServiceStubs.newLocalServiceStubs(),
        WorkflowClientOptions.newBuilder()
            .setNamespace(ComplianceShared.NAMESPACE)
            .build(),
    )

    // Worker: polls the Task Queue the Endpoint points at.
    val factory = WorkerFactory.newInstance(client)
    val worker = factory.newWorker(ComplianceShared.TASK_QUEUE)

    // The Nexus handler is what makes this team callable from Payments.
    worker.registerWorkflowImplementationTypes(ComplianceWorkflowImpl::class.java)
    worker.registerActivitiesImplementations(ComplianceActivityImpl(ComplianceChecker()))
    worker.registerNexusServiceImplementation(ComplianceNexusServiceImpl())

    factory.start()

    println("=========================================================")
    println("  Compliance Worker started on: ${ComplianceShared.TASK_QUEUE}")
    println("  Namespace: ${ComplianceShared.NAMESPACE}")
    println("  Registered: ComplianceWorkflow, ComplianceActivity, ComplianceNexusService")
    println("=========================================================")
}
