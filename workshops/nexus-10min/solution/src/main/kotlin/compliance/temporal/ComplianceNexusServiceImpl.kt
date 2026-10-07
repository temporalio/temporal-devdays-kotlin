package compliance.temporal

import compliance.domain.ComplianceRequest
import compliance.domain.ComplianceResult
import compliance.temporal.workflow.ComplianceWorkflow
import io.nexusrpc.handler.OperationHandler
import io.nexusrpc.handler.OperationImpl
import io.nexusrpc.handler.ServiceImpl
import io.temporal.client.WorkflowOptions
import io.temporal.nexus.Nexus
import io.temporal.nexus.WorkflowHandle
import io.temporal.nexus.WorkflowRunOperation
import shared.nexus.ComplianceNexusService

/**
 * [GIVEN] Nexus Service handler. Receives cross-team calls from Payments.
 *
 * @ServiceImpl links this class to the contract so Temporal can route Operations to it.
 */
@ServiceImpl(service = ComplianceNexusService::class)
class ComplianceNexusServiceImpl {

    /**
     * Async handler. Backs the Operation with a Workflow in the Compliance team's own
     * Namespace. fromWorkflowHandle binds the Operation to the Workflow ID, so a retried
     * Operation re-attaches to the existing Workflow instead of starting a duplicate.
     */
    @OperationImpl
    fun checkCompliance(): OperationHandler<ComplianceRequest, ComplianceResult> =
        WorkflowRunOperation.fromWorkflowHandle { _, _, input ->
            val client = Nexus.getOperationContext().workflowClient
            val wf = client.newWorkflowStub(
                ComplianceWorkflow::class.java,
                WorkflowOptions.newBuilder()
                    .setTaskQueue(ComplianceShared.TASK_QUEUE)
                    .setWorkflowId("compliance-${input.transactionId}")
                    .build(),
            )
            WorkflowHandle.fromWorkflowMethod(wf::run, input)
        }
}
