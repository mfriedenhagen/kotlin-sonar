package io.github.K0zka.kotlinsonar

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.jacoco.JaCoCoExtensions
import org.sonar.api.Plugin

class KotlinPlugin : Plugin {

	override fun define(context: Plugin.Context) {
		context.addExtensions(listOf(
                Kotlin::class.java,
				KotlinLintProfile::class.java,
				KotlinIssueSensor::class.java,
				KotlinLintRulesDefinition::class.java))
        context.addExtensions(JaCoCoExtensions.extensions)
	}
    companion object {
        val SONAR_KOTLIN_BINARIES = "sonar.kotlin.binaries"
        val SONAR_KOTLIN_BINARIES_FALLBACK = "sonar.binaries"
    }
}