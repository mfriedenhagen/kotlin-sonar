package io.github.K0zka.kotlinsonar

import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataReader
import org.sonar.plugins.jacoco.ExecutionDataVisitor
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

class ResourceUtils(private val stemPath: String) {

    private fun hasResource(resourceName: String): Boolean {
        return File("${stemPath}${resourceName}").exists()
    }

    private fun loadResource(resourceName: String): InputStream {
        return FileInputStream("${stemPath}${resourceName}").buffered()
    }

    fun executionDataVisitor(jacocoExecName: String): ExecutionDataVisitor {
        val visitor = ExecutionDataVisitor()
        loadResource(jacocoExecName).use {
            val reader = ExecutionDataReader(it)
            reader.setExecutionDataVisitor(visitor)
            reader.setSessionInfoVisitor(visitor)
            reader.read()
        }
        return visitor
    }

    fun coverageBuilder(klassName: String, visitor: ExecutionDataVisitor, classSuffix : String): CoverageBuilder {
        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(visitor.merged, coverageBuilder)
        val classFilesOfInterest = mutableListOf<String>(klassName + "Kt")
        classFilesOfInterest.addAll(
                visitor.merged.contents
                        .filter { it.name.startsWith(klassName + "$") || it.name == klassName || it.name == klassName + "Kt" }
                        .map { it.name })
        println(classFilesOfInterest)
        classFilesOfInterest.forEach { classFile ->
            val path = "${classFile}.${classSuffix}"
            if (hasResource("${path}"))
                loadResource("${path}").use {
                    analyzer.analyzeClass(it, path)
                }
        }
        return coverageBuilder
    }

    fun lines(klassName: String) = loadResource("${klassName}.kt").reader().readLines()

}