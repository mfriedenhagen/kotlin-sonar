package io.github.K0zka.kotlinsonar.jacoco

import org.sonar.api.utils.log.Logger
import org.sonar.api.utils.log.Loggers

object JaCoCoExtensions {

    private val LOG = Loggers.get(JaCoCoExtensions::class.java)

    val extensions: List<Any>

    init {
        val e: MutableList<Any> = mutableListOf(JaCoCoConfiguration.propertyDefinitions)
        e.addAll(arrayOf(
                JaCoCoConfiguration::class.java,
                JaCoCoSensor::class.java,
                JaCoCoItSensor::class.java,
                JaCoCoOverallSensor::class.java))
        extensions = e.toList()
    }

    fun logger(): Logger {
        return LOG
    }

}
