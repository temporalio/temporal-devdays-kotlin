package workshop

import io.temporal.activity.ActivityInterface
import io.temporal.activity.ActivityMethod

@ActivityInterface
interface GreetingActivities {
    @ActivityMethod
    fun composeGreeting(input: GreetingInput): String
}

class GreetingActivitiesImpl : GreetingActivities {
    override fun composeGreeting(input: GreetingInput): String = "Hello, ${input.name}!"
}
