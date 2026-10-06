package workshop

const val TASK_QUEUE = "workshop-slug"

// Every property has a default, so Kotlin also emits the no-arg constructor
// Jackson needs to deserialize this across the Temporal boundary.
data class GreetingInput(val name: String = "")
