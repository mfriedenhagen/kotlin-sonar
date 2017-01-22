package io.github.K0zka.kotlinsonar.surefire

import io.github.K0zka.kotlinsonar.kotlinLanguageName
import io.github.K0zka.kotlinsonar.surefire.api.SurefireUtils
import org.sonar.api.batch.DependedUpon
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.config.Settings
import org.sonar.api.scan.filesystem.PathResolver
import org.sonar.api.utils.log.Loggers
import java.io.File

@DependedUpon("surefire-java")
class KotlinSurefireSensor(private val kotlinSurefireParser: KotlinSurefireParser, private val settings: Settings, private val fs: FileSystem, private val pathResolver: PathResolver) : Sensor {

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.onlyOnLanguage(kotlinLanguageName).name("KotlinSurefireSensor")
    }

    override fun execute(context: SensorContext) {
        val dir = SurefireUtils.getReportsDirectory(settings, fs, pathResolver)
        collect(context, dir)
    }

    protected fun collect(context: SensorContext, reportsDir: File) {
        LOGGER.info("parsing {}", reportsDir)
        kotlinSurefireParser.collect(context, reportsDir)
    }

    override fun toString(): String {
        return javaClass.simpleName
    }

    companion object {

        private val LOGGER = Loggers.get(KotlinSurefireSensor::class.java)
    }

}
