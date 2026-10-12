package payments.temporal

import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.client.WorkflowExecutionAlreadyStarted
import io.temporal.client.WorkflowOptions
import io.temporal.client.WorkflowStub
import io.temporal.serviceclient.WorkflowServiceStubs
import payments.Shared
import payments.domain.PaymentRequest
import payments.domain.PaymentResult
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.system.exitProcess

/**
 * [GIVEN] Starts two payment Workflows and collects their results.
 *
 * The starter does not know or care whether compliance runs locally or via Nexus.
 */

// How long to wait before calling a payment "still running". Both finish in roughly
// 7 seconds while Compliance is up (its Workflow sleeps for 5 of those).
private const val COLLECT_DEADLINE_SECONDS = 20L

fun main() {
    println("==========================================================")
    println("  PAYMENT STARTER")
    println("  Running 2 transactions through Temporal")
    println("==========================================================")
    println()

    val client = WorkflowClient.newInstance(
        WorkflowServiceStubs.newLocalServiceStubs(),
        WorkflowClientOptions.newBuilder()
            .setNamespace(Shared.PAYMENTS_NAMESPACE)
            .build(),
    )

    val transactions = listOf(
        PaymentRequest(
            "TXN-A", 250.00, "USD", "US", "US",
            "Routine supplier payment", "ACC-001", "ACC-002",
        ),
        PaymentRequest(
            "TXN-B", 75_000.00, "USD", "US", "US",
            "Large capital transfer", "ACC-003", "ACC-004",
        ),
    )

    // Start both without blocking, so a slow one cannot hold up the other.
    val pending = transactions.map { txn ->
        val workflowId = "payment-${txn.transactionId}"
        val workflow = client.newWorkflowStub(
            PaymentProcessingWorkflow::class.java,
            WorkflowOptions.newBuilder()
                .setTaskQueue(Shared.TASK_QUEUE)
                .setWorkflowId(workflowId)
                .build(),
        )
        try {
            WorkflowClient.start(workflow::processPayment, txn)
            println("  Started: $workflowId")
        } catch (e: WorkflowExecutionAlreadyStarted) {
            // Still running from an earlier run, so wait on that one instead.
            println("  Already running: $workflowId")
        }
        println(
            "    Amount: $${"%.2f".format(txn.amount)}" +
                " | Route: ${txn.senderCountry} -> ${txn.receiverCountry}"
        )
        txn to client.newUntypedWorkflowStub(workflowId)
    }

    println()
    println("  Collecting results...")
    println()

    val futures = pending.map { (txn, stub: WorkflowStub) ->
        txn to stub.getResultAsync(PaymentResult::class.java)
    }
    try {
        CompletableFuture
            .allOf(*futures.map { it.second }.toTypedArray())
            .get(COLLECT_DEADLINE_SECONDS, TimeUnit.SECONDS)
    } catch (e: TimeoutException) {
        // Expected while the Compliance Worker is down. Reported per transaction below.
    } catch (e: ExecutionException) {
        // A Workflow failed. Reported per transaction below.
    }

    var stillRunning = 0

    futures.forEach { (txn, future) ->
        println("----------------------------------------------------")
        println("  ${txn.transactionId}")

        if (!future.isDone) {
            stillRunning++
            println("  Result: STILL RUNNING")
            println("  Reason: the Compliance handler is not answering right now.")
        } else {
            try {
                val result = future.get()
                println("  Result: ${result.status}")
                println("  Risk:   ${result.riskLevel ?: "N/A"}")
                println("  Reason: ${result.explanation ?: "N/A"}")
                result.confirmationNumber?.let { println("  Conf#:  $it") }
                result.error?.let { println("  Error:  $it") }
            } catch (e: ExecutionException) {
                println("  Result: FAILED")
                println("  Error:  ${e.cause?.message ?: e.message}")
            }
        }
        println()
    }

    println("==========================================================")
    if (stillRunning > 0) {
        println("  $stillRunning transaction(s) are still running. Nothing failed.")
        println("  Start the Compliance Worker and they resume on their own.")
    } else {
        println("  Both transactions resolved!")
    }
    println("  Check Temporal UI: http://localhost:8233")
    println("==========================================================")

    exitProcess(0)
}
