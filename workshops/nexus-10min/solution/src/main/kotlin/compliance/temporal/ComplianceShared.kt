package compliance.temporal

object ComplianceShared {
    const val NAMESPACE = "compliance-namespace"

    // MUST match --target-task-queue on the Nexus Endpoint.
    const val TASK_QUEUE = "compliance-risk"
}
