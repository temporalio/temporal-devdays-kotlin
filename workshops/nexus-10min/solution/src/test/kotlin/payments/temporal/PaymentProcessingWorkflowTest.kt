package payments.temporal

import compliance.ComplianceChecker
import compliance.temporal.ComplianceNexusServiceImpl
import compliance.temporal.ComplianceShared
import compliance.temporal.activity.ComplianceActivityImpl
import compliance.temporal.workflow.ComplianceWorkflowImpl
import io.temporal.api.enums.v1.EventType
import io.temporal.client.WorkflowOptions
import io.temporal.testing.TestWorkflowEnvironment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import payments.domain.PaymentRequest
import payments.domain.PaymentResult
import java.util.UUID

/**
 * Runs both teams' Workers against one in-process test server, joined by a real Nexus
 * Endpoint. The Payments Worker is registered by the same registerPayments() the
 * Payments Worker app uses.
 */
class PaymentProcessingWorkflowTest {

    private lateinit var env: TestWorkflowEnvironment

    @BeforeEach
    fun setUp() {
        env = TestWorkflowEnvironment.newInstance()

        val compliance = env.newWorker(ComplianceShared.TASK_QUEUE)
        compliance.registerWorkflowImplementationTypes(ComplianceWorkflowImpl::class.java)
        compliance.registerActivitiesImplementations(ComplianceActivityImpl(ComplianceChecker()))
        compliance.registerNexusServiceImplementation(ComplianceNexusServiceImpl())

        val endpoint = env.createNexusEndpoint("compliance-endpoint-test", ComplianceShared.TASK_QUEUE)
        registerPayments(env.newWorker(PAYMENTS_TASK_QUEUE), endpoint.spec.name)

        env.start()
    }

    @AfterEach
    fun tearDown() {
        env.close()
    }

    @Test
    fun `low risk payment completes through Nexus`() {
        val (workflowId, result) = process(
            PaymentRequest("TXN-A", 250.00, "USD", "US", "US", "Routine", "ACC-001", "ACC-002"),
        )

        assertEquals("COMPLETED", result.status)
        assertEquals("LOW", result.riskLevel)
        assertEquals("CONF-TXN-A", result.confirmationNumber)
        assertCalledThroughNexus(workflowId)
    }

    @Test
    fun `high risk payment is declined through Nexus`() {
        val (workflowId, result) = process(
            PaymentRequest("TXN-B", 75_000.00, "USD", "US", "US", "Large", "ACC-003", "ACC-004"),
        )

        assertEquals("DECLINED_COMPLIANCE", result.status)
        assertEquals("HIGH", result.riskLevel)
        assertCalledThroughNexus(workflowId)
    }

    private fun process(request: PaymentRequest): Pair<String, PaymentResult> {
        val workflowId = "payment-${request.transactionId}-${UUID.randomUUID()}"
        val workflow = env.workflowClient.newWorkflowStub(
            PaymentProcessingWorkflow::class.java,
            WorkflowOptions.newBuilder()
                .setTaskQueue(PAYMENTS_TASK_QUEUE)
                .setWorkflowId(workflowId)
                .build(),
        )
        return workflowId to workflow.processPayment(request)
    }

    private fun assertCalledThroughNexus(workflowId: String) {
        val events = env.workflowClient.fetchHistory(workflowId).events
        assertTrue(
            events.any { it.eventType == EventType.EVENT_TYPE_NEXUS_OPERATION_COMPLETED },
            "The compliance check did not go through Nexus. Finish TODO 1a, 1b and 1c.",
        )
    }

    private companion object {
        val PAYMENTS_TASK_QUEUE = "payments-${UUID.randomUUID()}"
    }
}
