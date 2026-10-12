package payments.temporal

import compliance.ComplianceChecker
import compliance.temporal.activity.ComplianceActivityImpl
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.serviceclient.WorkflowServiceStubs
import io.temporal.worker.Worker
import io.temporal.worker.WorkerFactory
import io.temporal.worker.WorkflowImplementationOptions
import io.temporal.workflow.NexusServiceOptions
import payments.PaymentGateway
import payments.Shared
import payments.temporal.activity.PaymentActivityImpl

/**
 * The Payments team's Worker.
 */
fun main() {
    // Client: scoped to the Payments team's own Namespace.
    val client = WorkflowClient.newInstance(
        WorkflowServiceStubs.newLocalServiceStubs(),
        WorkflowClientOptions.newBuilder()
            .setNamespace(Shared.PAYMENTS_NAMESPACE)
            .build(),
    )

    val factory = WorkerFactory.newInstance(client)
    val worker = factory.newWorker(Shared.TASK_QUEUE)
    registerPayments(worker, Shared.COMPLIANCE_ENDPOINT)

    factory.start()

    println("=========================================================")
    println("  Payments Worker started on: ${Shared.TASK_QUEUE}")
    println("  Namespace: ${Shared.PAYMENTS_NAMESPACE}")
    println("  Nexus: ComplianceNexusService -> ${Shared.COMPLIANCE_ENDPOINT}")
    println("=========================================================")
}

/**
 * Registers everything the Payments Worker runs. Shared with the tests, so the
 * Worker under test is the Worker you run.
 */
fun registerPayments(worker: Worker, complianceEndpoint: String) {
    // [GIVEN] The Endpoint name lives here, on the Worker, not in the Workflow. This is
    // the seam that lets Compliance move Namespace or Task Queue without Payments
    // changing a line of Workflow code.
    worker.registerWorkflowImplementationTypes(
        WorkflowImplementationOptions.newBuilder()
            .setNexusServiceOptions(
                mapOf(
                    "ComplianceNexusService" to NexusServiceOptions.newBuilder()
                        .setEndpoint(complianceEndpoint)
                        .build()
                )
            )
            .build(),
        PaymentProcessingWorkflowImpl::class.java,
    )

    worker.registerActivitiesImplementations(PaymentActivityImpl(PaymentGateway()))

    // ── TODO 1c ──────────────────────────────────────────────────────────────────
    // Delete the line below. It registers the Compliance team's Activity on the
    // Payments Worker, inside the Payments process. That is the coupling. Once TODO 1a
    // and 1b send the check over Nexus, nothing here calls it any more.
    //
    // Deleting it is the decoupling. Everything else was wiring. (The two compliance
    // imports at the top become unused; leave them or delete them.)
    worker.registerActivitiesImplementations(ComplianceActivityImpl(ComplianceChecker()))
}
