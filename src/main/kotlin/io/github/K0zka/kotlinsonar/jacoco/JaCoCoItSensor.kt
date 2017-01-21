package io.github.K0zka.kotlinsonar.jacoco

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.Sensor
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.batch.sensor.coverage.CoverageType
import org.sonar.api.scan.filesystem.PathResolver

import java.io.File

class JaCoCoItSensor(private val groovy: Kotlin, private val configuration: JaCoCoConfiguration, private val fileSystem: FileSystem, private val pathResolver: PathResolver) : Sensor {

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.onlyOnLanguage(kotlinLanguageName).name(this.toString())
    }

    override fun execute(context: SensorContext) {
        if (shouldExecuteOnProject()) {
            ITAnalyzer().analyse(context)
        }
    }

    internal fun shouldExecuteOnProject(): Boolean {
        val report = pathResolver.relativeFile(fileSystem.baseDir(), configuration.itReportPath)
        val foundReport = report.exists() && report.isFile
        val shouldExecute = configuration.shouldExecuteOnProject(foundReport)
        if (!foundReport && shouldExecute) {
            JaCoCoExtensions.logger().info(this.toString() + ": JaCoCo IT report not found.")
        }
        return shouldExecute
    }

    internal inner class ITAnalyzer : AbstractAnalyzer(groovy, fileSystem, pathResolver) {

        override val reportPath: String?
            get() = configuration.itReportPath

        override fun coverageType(): CoverageType {
            return CoverageType.IT
        }
    }

    override fun toString(): String {
        return "Kotlin " + javaClass.simpleName
    }
}
