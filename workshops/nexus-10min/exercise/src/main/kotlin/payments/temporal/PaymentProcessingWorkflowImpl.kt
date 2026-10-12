package payments.temporal

import compliance.domain.ComplianceRequest
import compliance.temporal.activity.ComplianceActivity
import io.temporal.activity.ActivityOptions
import io.temporal.common.RetryOptions
import io.temporal.workflow.NexusOperationOptions
import io.temporal.workflow.NexusServiceOptions
import io.temporal.workflow.Workflow
import payments.domain.PaymentRequest
import payments.domain.PaymentResult
import payments.temporal.activity.PaymentActivity
import shared.nexus.ComplianceNexusService
import java.time.Duration

/**
 * MONOLITH VERSION. It works, which is what makes the coupling easy to miss.
 *
 * Three steps, all on the Payments Worker:
 *   Step 1: validatePayment  (PaymentActivity, Payments team)
 *   Step 2: checkCompliance  (ComplianceActivity, Compliance team)  <- crosses a team boundary
 *   Step 3: executePayment   (PaymentActivity, Payments team)
 *
 * Your TODOs here are 1a and 1b. TODO 1c is in PaymentsWorkerApp.kt.
 *
 * On failures: this Workflow returns a result for business outcomes (validation
 * rejected, compliance declined) and lets infrastructure failures propagate. An
 * ActivityFailure or NexusOperationFailure fails the Workflow, so a broken Endpoint
 * mapping shows up red in the Web UI instead of hiding inside the result of a
 * Workflow that claims to have completed.
 */
class PaymentProcessingWorkflowImpl : PaymentProcessingWorkflow {

    private val paymentActivity: PaymentActivity = Workflow.newActivityStub(
        PaymentActivity::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .setRetryOptions(
                RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setBackoffCoefficient(2.0)
                    .build()
            )
            .build(),
    )

    // ── TODO 1a ──────────────────────────────────────────────────────────────────
    // Replace the whole `complianceActivity` declaration below with a Nexus Service
    // stub. Shape of what you write:
    //
    //     private val complianceService: ComplianceNexusService =
    //         Workflow.newNexusServiceStub(
    //             ComplianceNexusService::class.java,
    //             NexusServiceOptions.newBuilder()
    //                 .setOperationOptions(
    //                     NexusOperationOptions.newBuilder()
    //                         .setScheduleToCloseTimeout(Duration.ofMinutes(10))
    //                         .build()
    //                 )
    //                 .build(),
    //         )
    //
    // The imports you need are already at the top of this file.
    //
    // An Activity stub schedules work on THIS Worker. A Nexus stub sends it to whichever
    // Worker owns the Nexus Service, in another Namespace. Notice what you do NOT write:
    // the Endpoint name. The Worker already maps the contract to it.
    //
    // The 10 minutes is the budget for the whole call, retries included. It is what
    // lets the Operation survive the Compliance Worker going away. You prove that in
    // Challenge 2.
    private val complianceActivity: ComplianceActivity = Workflow.newActivityStub(
        ComplianceActivity::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofSeconds(30))
            .build(),
    )

    override fun processPayment(request: PaymentRequest): PaymentResult {
        val logger = Workflow.getLogger(PaymentProcessingWorkflowImpl::class.java)

        // Step 1: Validate payment (Payments team)
        if (!paymentActivity.validatePayment(request)) {
            return PaymentResult(
                success = false,
                transactionId = request.transactionId,
                status = "REJECTED",
                error = "Payment validation failed",
            )
        }
        logger.info("Step 1 passed: validation OK for ${request.transactionId}")

        // Step 2: Compliance check (Compliance team)
        val compReq = ComplianceRequest(
            request.transactionId,
            request.amount,
            request.senderCountry,
            request.receiverCountry,
            request.description,
        )

        logger.info("Step 2: calling compliance check for ${request.transactionId}")

        // ── TODO 1b ──────────────────────────────────────────────────────────────
        // Change `complianceActivity` to `complianceService`, the stub from TODO 1a.
        // One word. Same method name, same input, same result type.
        val compliance = complianceActivity.checkCompliance(compReq)

        logger.info("Compliance result: ${compliance.riskLevel} | approved=${compliance.approved}")

        // A declined payment is a business outcome, not a failure. The Workflow
        // completes successfully and reports the decision.
        if (!compliance.approved) {
            return PaymentResult(
                success = false,
                transactionId = request.transactionId,
                status = "DECLINED_COMPLIANCE",
                riskLevel = compliance.riskLevel,
                explanation = compliance.explanation,
            )
        }

        // Step 3: Execute payment, only if compliance approved
        logger.info("Step 3: executing payment for ${request.transactionId}")
        val confirmation = paymentActivity.executePayment(request)

        return PaymentResult(
            success = true,
            transactionId = request.transactionId,
            status = "COMPLETED",
            riskLevel = compliance.riskLevel,
            explanation = compliance.explanation,
            confirmationNumber = confirmation,
        )
    }
}
