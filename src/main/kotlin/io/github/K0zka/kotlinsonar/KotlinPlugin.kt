package io.github.K0zka.kotlinsonar

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.jacoco.JaCoCoExtensions
import org.slf4j.LoggerFactory
import org.sonar.api.Plugin

class KotlinPlugin : Plugin {

	override fun define(context: Plugin.Context) {
		context.addExtensions(listOf(
                Kotlin::class.java,
				KotlinLintProfile::class.java,
				KotlinIssueSensor::class.java,
				KotlinLintRulesDefinition::class.java))
        context.addExtensions(JaCoCoExtensions.extensions)
        log.warn("{}: extensions={}", this, context.extensions)
	}
    companion object {
        val log = LoggerFactory.getLogger(KotlinPlugin::class.java)
        val SONAR_KOTLIN_BINARIES = "sonar.kotlin.binaries"
        val SONAR_KOTLIN_BINARIES_FALLBACK = "sonar.binaries"
    }
}