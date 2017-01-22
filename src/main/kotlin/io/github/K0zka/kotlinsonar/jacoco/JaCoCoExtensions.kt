package io.github.K0zka.kotlinsonar.jacoco

import org.sonar.api.utils.Version
import org.sonar.api.utils.log.Logger
import org.sonar.api.utils.log.Loggers
import org.sonar.plugins.jacoco.JacocoConfiguration
object JaCoCoExtensions {

    private val LOG = Loggers.get(JaCoCoExtensions::class.java)

    val extensions: List<Any>

    init {
        val e: MutableList<Any> = mutableListOf(JacocoConfiguration.getPropertyDefinitions(Version.create(6, 2)))
        e.addAll(arrayOf(
                JacocoConfiguration::class.java,
                KotlinJaCoCoSensor::class.java,
                KotlinJaCoCoItSensor::class.java,
                KotlinJaCoCoOverallSensor::class.java))
        extensions = e.toList()
    }

    fun logger(): Logger {
        return LOG
    }

}
