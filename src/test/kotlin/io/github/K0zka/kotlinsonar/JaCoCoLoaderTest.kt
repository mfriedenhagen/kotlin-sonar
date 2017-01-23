package io.github.K0zka.kotlinsonar

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataReader
import org.junit.Test
import org.sonar.plugins.jacoco.ExecutionDataVisitor

class JaCoCoLoaderTest {
    @Test
    fun shouldLoad_KotlinLintProfile() {
        val klassName = "io/github/K0zka/kotlinsonar/KotlinLintProfile"
        val simpleName = "KotlinLintProfile"
        shouldLoad(klassName, simpleName)
    }

    @Test
    fun shouldLoad_AKotlinThing() {
        val klassName = "com/example/AKotlinThing"
        val simpleName = "AKotlinThing"
        shouldLoad(klassName, simpleName)
    }

    private fun shouldLoad(klassName: String, simpleName: String) {
        println("Processing ${klassName}")
        val visitor = executionDataVisitor(simpleName)
        val coverageBuilder = coverageBuilder(klassName, visitor)
        val iClassCoverage = coverageBuilder.classes.first { it.name == klassName }
        val iSourceFileCoverage = coverageBuilder.sourceFiles.first { it.packageName + "/" + it.name == "${klassName}.kt" }
        assertThat(iClassCoverage.firstLine, equalTo(iSourceFileCoverage.firstLine))
        assertThat(iClassCoverage.lastLine, equalTo(iSourceFileCoverage.lastLine))
        println("Lines of ${klassName}: ${lines(klassName)} should be ${iSourceFileCoverage.lastLine}")
    }

    private fun lines(klassName: String) = resource("/test_data/${klassName}.kt").reader().readLines().size

    private fun executionDataVisitor(klassName: String): ExecutionDataVisitor {
        val visitor = ExecutionDataVisitor()
        resource("/test_data/jacoco-${klassName}.exec").use {
            val reader = ExecutionDataReader(it)
            reader.setExecutionDataVisitor(visitor)
            reader.setSessionInfoVisitor(visitor)
            reader.read()
        }
        return visitor
    }

    private fun coverageBuilder(klassName: String, visitor: ExecutionDataVisitor): CoverageBuilder {
        val coverageBuilder = CoverageBuilder()
        val analyzer = Analyzer(visitor.merged, coverageBuilder)
        resource("/test_data/${klassName}.klass").use {
            analyzer.analyzeClass(it, "test_data/${klassName}.klass")
        }
        return coverageBuilder
    }

    private fun resource(s: String) = JaCoCoLoaderTest::class.java.getResourceAsStream(s).buffered()
}