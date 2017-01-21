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

class JaCoCoOverallSensor(private val kotlin: Kotlin, private val configuration: JaCoCoConfiguration, private val fileSystem: FileSystem, private val pathResolver: PathResolver) : Sensor {

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.onlyOnLanguage(kotlinLanguageName).name(this.toString())
    }

    override fun execute(context: SensorContext) {
        val reportUTs = pathResolver.relativeFile(fileSystem.baseDir(), configuration.reportPath)
        val reportITs = pathResolver.relativeFile(fileSystem.baseDir(), configuration.itReportPath)
        if (shouldExecuteOnProject()) {
            val reportOverall = File(fileSystem.workDir(), JACOCO_OVERALL)
            reportOverall.parentFile.mkdirs()
            JaCoCoReportMerger.mergeReports(reportOverall, reportUTs, reportITs)
            OverallAnalyzer(reportOverall).analyse(context)
        }
    }

    internal fun shouldExecuteOnProject(): Boolean {
        val baseDir = fileSystem.baseDir()
        val reportUTs = pathResolver.relativeFile(baseDir, configuration.reportPath)
        val reportITs = pathResolver.relativeFile(baseDir, configuration.itReportPath)
        val foundOneReport = reportUTs.exists() || reportITs.exists()
        val shouldExecute = configuration.shouldExecuteOnProject(foundOneReport)
        if (!foundOneReport && shouldExecute) {
            JaCoCoExtensions.logger().info("JaCoCoOverallSensor: JaCoCo reports not found.")
        }
        return shouldExecute
    }

    internal inner class OverallAnalyzer(private val report: File) : AbstractAnalyzer(kotlin, fileSystem, pathResolver) {

        override fun coverageType(): CoverageType {
            return CoverageType.OVERALL
        }

        protected override val reportPath: String?
            get() = report.absolutePath
    }

    override fun toString(): String {
        return "Kotlin " + javaClass.simpleName
    }

    companion object {

        val JACOCO_OVERALL = "jacoco-overall.exec"
    }
}
