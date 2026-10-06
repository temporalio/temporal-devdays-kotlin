package workshop

import io.temporal.client.WorkflowOptions
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.worker.WorkflowImplementationOptions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class GreetingWorkflowTest {

    private lateinit var env: TestWorkflowEnvironment

    @BeforeEach
    fun setUp() {
        env = TestWorkflowEnvironment.newInstance()
    }

    @AfterEach
    fun tearDown() {
        env.close()
    }

    @Test
    fun `greeting workflow returns greeting`() {
        val taskQueue = UUID.randomUUID().toString()
        val worker = env.newWorker(taskQueue)
        worker.registerWorkflowImplementationTypes(
            // Fail the Workflow on any Throwable instead of retrying the Task, so an
            // unfinished TODO fails the test rather than hanging it.
            WorkflowImplementationOptions.newBuilder()
                .setFailWorkflowExceptionTypes(Throwable::class.java)
                .build(),
            GreetingWorkflowImpl::class.java,
        )
        worker.registerActivitiesImplementations(GreetingActivitiesImpl())
        env.start()

        val workflow = env.workflowClient.newWorkflowStub(
            GreetingWorkflow::class.java,
            WorkflowOptions.newBuilder()
                .setTaskQueue(taskQueue)
                .setWorkflowId(UUID.randomUUID().toString())
                .build(),
        )

        assertEquals("Hello, Temporal!", workflow.run(GreetingInput("Temporal")))
    }
}
