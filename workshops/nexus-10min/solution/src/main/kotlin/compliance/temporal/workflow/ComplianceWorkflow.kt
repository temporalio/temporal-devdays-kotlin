package compliance.temporal.workflow

import compliance.domain.ComplianceRequest
import compliance.domain.ComplianceResult
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

/**
 * [GIVEN] Compliance Workflow. The Compliance team's side of the Nexus Operation.
 *
 * Runs the automated compliance check and returns the verdict.
 */
@WorkflowInterface
interface ComplianceWorkflow {

    @WorkflowMethod
    fun run(request: ComplianceRequest): ComplianceResult
}
