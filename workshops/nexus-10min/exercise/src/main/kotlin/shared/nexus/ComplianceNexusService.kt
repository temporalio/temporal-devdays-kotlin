package shared.nexus

import compliance.domain.ComplianceRequest
import compliance.domain.ComplianceResult
import io.nexusrpc.Operation
import io.nexusrpc.Service

/**
 * [GIVEN] Nexus Service interface. The shared contract between the Payments and
 * Compliance teams.
 *
 * Both teams depend on this interface:
 *   - Payments creates a stub from it, inside the caller Workflow
 *   - Compliance implements a handler for it, on its own Worker
 */
@Service
interface ComplianceNexusService {

    @Operation
    fun checkCompliance(request: ComplianceRequest): ComplianceResult
}
