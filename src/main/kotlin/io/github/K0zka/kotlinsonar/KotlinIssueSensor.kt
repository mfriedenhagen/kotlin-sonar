package io.github.K0zka.kotlinsonar

import org.slf4j.LoggerFactory
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor

class KotlinIssueSensor(
        private val fileSystem: FileSystem) : Sensor {
    override fun describe(descriptor: SensorDescriptor?) {
        descriptor!!.onlyOnLanguage(kotlinLanguageName).name("Kotlin Sensor")
    }

    override fun execute(context: SensorContext?) {
        for (input in fileSystem.inputFiles { it.language() == kotlinLanguageName }) {
            logger.debug("Analysing {}", input.relativePath())
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(KotlinIssueSensor::class.java)!!
    }
}