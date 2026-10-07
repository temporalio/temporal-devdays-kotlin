package compliance.temporal.workflow

import compliance.domain.ComplianceRequest
import compliance.domain.ComplianceResult
import compliance.temporal.activity.ComplianceActivity
import io.temporal.activity.ActivityOptions
import io.temporal.workflow.Workflow
import java.time.Duration

/**
 * [GIVEN] Compliance Workflow implementation.
 *
 * The 5-second Workflow.sleep is intentional. It keeps each ComplianceWorkflow Running
 * long enough to spot in compliance-namespace in the Web UI. The timer runs on the
 * server, so nothing is lost if the Compliance Worker goes away mid-sleep.
 */
class ComplianceWorkflowImpl : ComplianceWorkflow {

    private val complianceActivity: ComplianceActivity = Workflow.newActivityStub(
        ComplianceActivity::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .build(),
    )

    override fun run(request: ComplianceRequest): ComplianceResult {
        val result = complianceActivity.checkCompliance(request)
        Workflow.sleep(Duration.ofSeconds(5))
        return result
    }
}
