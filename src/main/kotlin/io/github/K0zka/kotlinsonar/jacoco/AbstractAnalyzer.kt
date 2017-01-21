package io.github.K0zka.kotlinsonar.jacoco

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.foundation.KotlinFileSystem
import org.jacoco.core.analysis.ICounter
import org.jacoco.core.analysis.ISourceFileCoverage
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.coverage.CoverageType
import org.sonar.api.batch.sensor.coverage.NewCoverage
import org.sonar.api.scan.filesystem.PathResolver
import java.io.File

abstract class AbstractAnalyzer(kotlin: Kotlin, fileSystem: FileSystem, private val pathResolver: PathResolver) {
    private val baseDir = fileSystem.baseDir()
    private val kotlinFileSystem: KotlinFileSystem = KotlinFileSystem(fileSystem)
    private val classFilesCache: MutableMap<String, File> = mutableMapOf()
    private val binaryDirs = kotlin.getBinaryDirectories().map {
        val f = File(it)
        if (!f.isAbsolute) File(baseDir, it) else f
    }

    private fun getInputFile(coverage: ISourceFileCoverage): InputFile? {
        val path = getFileRelativePath(coverage)
        val sourceInputFileFromRelativePath = kotlinFileSystem.sourceInputFileFromRelativePath(path)
        if (sourceInputFileFromRelativePath == null) {
            JaCoCoExtensions.logger().warn("File not found: " + path)
        }
        return sourceInputFileFromRelativePath
    }

    private fun getFileRelativePath(coverage: ISourceFileCoverage): String {
        return coverage.packageName + "/" + coverage.name
    }

    fun analyse(context: SensorContext) {
        if (!atLeastOneBinaryDirectoryExists()) {
            JaCoCoExtensions.logger().warn("Project coverage is set to 0% since there is no directories with classes.")
            return
        }
        for (classesDir in binaryDirs) {
            populateClassFilesCache(classFilesCache, classesDir, "")
        }

        val path = reportPath
        if (path == null) {
            JaCoCoExtensions.logger().warn("No jacoco coverage execution file found.")
            return
        }
        val jacocoExecutionData = pathResolver.relativeFile(baseDir, path)

        readExecutionData(jacocoExecutionData, context)

        classFilesCache.clear()
    }

    private fun populateClassFilesCache(classFilesCache: MutableMap<String, File>, dir: File, path: String) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory) {
                populateClassFilesCache(classFilesCache, file, path + file.name + "/")
            } else if (file.name.endsWith(".class")) {
                val className = path + file.name.removeSuffix(".class")
                classFilesCache.put(className, file)
            }
        }
    }

    private fun atLeastOneBinaryDirectoryExists(): Boolean {
        if (binaryDirs.isEmpty()) {
            JaCoCoExtensions.logger().warn("No binary directories defined.")
        }
        for (binaryDir in binaryDirs) {
            JaCoCoExtensions.logger().info("\tChecking binary directory: {}", binaryDir.toString())
            if (binaryDir.exists()) {
                return true
            }
        }
        return false
    }

    fun readExecutionData(jacocoExecutionData: File, context: SensorContext) {
        val executionDataVisitor = ExecutionDataVisitor()

        var fileToAnalyze: File? = jacocoExecutionData
        if (fileToAnalyze == null || !fileToAnalyze.isFile) {
            JaCoCoExtensions.logger().warn("Project coverage is set to 0% as no JaCoCo execution data has been dumped: {}", jacocoExecutionData)
            fileToAnalyze = null
        } else {
            JaCoCoExtensions.logger().info("Analysing {}", fileToAnalyze)
        }
        val jacocoReportReader = JaCoCoReportReader(fileToAnalyze).readJacocoReport(executionDataVisitor, executionDataVisitor)

        val coverageBuilder = jacocoReportReader.analyzeFiles(executionDataVisitor.merged, classFilesCache.values)
        var analyzedResources = 0
        for (coverage in coverageBuilder.sourceFiles) {
            val kotlinFile = getInputFile(coverage)
            if (kotlinFile != null) {
                val newCoverage = context.newCoverage().onFile(kotlinFile).ofType(coverageType())
                analyzeFile(newCoverage, kotlinFile, coverage)
                newCoverage.save()
                analyzedResources++
            }
        }
        if (analyzedResources == 0) {
            JaCoCoExtensions.logger().warn("Coverage information was not collected. Perhaps you forget to include debug information into compiled classes?")
        }
    }

    private fun analyzeFile(newCoverage: NewCoverage, kotlinFile: InputFile, coverage: ISourceFileCoverage) {
        for (lineId in coverage.firstLine..coverage.lastLine) {
            var hits = -1
            val line = coverage.getLine(lineId)
            var ignore = false
            when (line.instructionCounter.status) {
                ICounter.FULLY_COVERED, ICounter.PARTLY_COVERED -> hits = 1
                ICounter.NOT_COVERED -> hits = 0
                ICounter.EMPTY -> ignore = true
                else -> {
                    ignore = true
                    JaCoCoExtensions.logger().warn("Unknown status for line {} in {}", lineId, kotlinFile)
                }
            }
            if (ignore) {
                continue
            }
            try {
                newCoverage.lineHits(lineId, hits)
                val branchCounter = line.branchCounter
                val conditions = branchCounter.totalCount
                if (conditions > 0) {
                    val coveredConditions = branchCounter.coveredCount
                    newCoverage.conditions(lineId, conditions, coveredConditions)
                }
            } catch (e: IllegalStateException) {
                JaCoCoExtensions.logger().warn("Limping along" + e)
            }
        }
    }

    protected abstract fun coverageType(): CoverageType

    protected abstract val reportPath: String?
}
