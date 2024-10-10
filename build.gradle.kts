import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kover) apply false
}

subprojects {
    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.addAll(
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            )
        }
    }

    tasks.withType<Test>().configureEach {
        testLogging {
            events = setOf(
                TestLogEvent.STANDARD_OUT,
                TestLogEvent.SKIPPED,
                TestLogEvent.FAILED
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }
    }
}