package io.github.K0zka.kotlinsonar.jacoco

import org.sonar.api.utils.Version
import org.sonar.plugins.jacoco.JacocoConfiguration

object KotlinJaCoCoExtensions {

    val extensions: List<Any>

    init {
        val e: MutableList<Any> = mutableListOf(JacocoConfiguration.getPropertyDefinitions(Version.create(6, 2)))
        e.addAll(arrayOf(
                JacocoConfiguration::class.java,
                KotlinJaCoCoSensor::class.java))
        extensions = e.toList()
    }
}
