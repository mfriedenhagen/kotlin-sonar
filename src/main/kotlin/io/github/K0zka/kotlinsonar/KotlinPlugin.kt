package io.github.K0zka.kotlinsonar

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.foundation.KotlinJavaResourceLocator
import io.github.K0zka.kotlinsonar.jacoco.JaCoCoExtensions
import io.github.K0zka.kotlinsonar.surefire.KotlinSurefireParser
import io.github.K0zka.kotlinsonar.surefire.KotlinSurefireSensor
import org.sonar.api.Plugin
import org.sonar.java.DefaultJavaResourceLocator
import org.sonar.java.JavaClasspath
import org.sonar.java.JavaTestClasspath
import org.sonar.plugins.java.api.JavaResourceLocator

class KotlinPlugin : Plugin {

	override fun define(context: Plugin.Context) {
		context.addExtensions(listOf(
                Kotlin::class.java,
                JavaClasspath::class.java,
                JavaTestClasspath::class.java,
                KotlinJavaResourceLocator::class.java,
                KotlinSurefireParser::class.java,
                KotlinSurefireSensor::class.java,
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