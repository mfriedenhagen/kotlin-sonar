package io.github.K0zka.kotlinsonar.jacoco

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataReader
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.data.IExecutionDataVisitor
import org.jacoco.core.data.ISessionInfoVisitor
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class JaCoCoReportReader(private val jacocoExecutionData: File?) {
    /**
     * Read JaCoCo report determining the format to be used.
     * @param executionDataVisitor visitor to store execution data.
     * *
     * @param sessionInfoStore visitor to store info session.
     * *
     * @return true if binary format is the latest one.
     * *
     * @throws IOException in case of error or binary format not supported.
     */
    fun readJacocoReport(executionDataVisitor: IExecutionDataVisitor, sessionInfoStore: ISessionInfoVisitor): JaCoCoReportReader {
        if (jacocoExecutionData == null) {
            return this
        }

        JaCoCoExtensions.logger().info("Reading {}", jacocoExecutionData)
        try {
            BufferedInputStream(FileInputStream(jacocoExecutionData)).use { inputStream ->
                val reader = ExecutionDataReader(inputStream)
                reader.setSessionInfoVisitor(sessionInfoStore)
                reader.setExecutionDataVisitor(executionDataVisitor)
                reader.read()
            }
        } catch (e: IOException) {
            throw IllegalArgumentException(String.format("Unable to read %s", jacocoExecutionData.absolutePath), e)
        }

        return this
    }

    /**
     * Caller must guarantee that `classFiles` are actually class file.
     */
    fun analyzeFiles(executionDataStore: ExecutionDataStore, classFiles: Collection<File>): CoverageBuilder {
        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(executionDataStore, coverageBuilder)
        for (classFile in classFiles) {
            analyzeClassFile(analyzer, classFile)
        }
        return coverageBuilder
    }

    private fun analyzeClassFile(analyzer: Analyzer, classFile: File) {
        try {
            FileInputStream(classFile).use { inputStream -> analyzer.analyzeClass(inputStream, classFile.path) }
        } catch (e: IOException) {
            // (Godin): in fact JaCoCo includes name into exception
            JaCoCoExtensions.logger().warn("Exception during analysis of file " + classFile.absolutePath, e)
        }

    }

}
