plugins {
    kotlin("jvm") version "2.4.20"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.temporal:temporal-sdk:1.40.0")

    // Without an SLF4J binding the Worker prints a warning banner on every start.
    implementation("org.slf4j:slf4j-simple:2.0.17")

    testImplementation("io.temporal:temporal-testing:1.40.0")
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        // Emits parameter names into the class files so Jackson can bind constructor
        // arguments by name when deserializing Nexus operation payloads.
        javaParameters = true
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging { events("passed", "failed") }
}

fun entryPoint(name: String, mainClassName: String, desc: String) =
    tasks.register<JavaExec>(name) {
        group = "application"
        description = desc
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set(mainClassName)
        standardInput = System.`in`
    }

entryPoint("paymentsWorker", "payments.temporal.PaymentsWorkerAppKt", "Runs the Payments Worker.")
entryPoint("complianceWorker", "compliance.temporal.ComplianceWorkerAppKt", "Runs the Compliance Worker.")
entryPoint("starter", "payments.temporal.PaymentStarterKt", "Starts the two sample payments.")
