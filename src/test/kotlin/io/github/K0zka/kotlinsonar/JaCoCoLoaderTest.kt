package io.github.K0zka.kotlinsonar

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.jacoco.core.analysis.Analyzer
import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataReader
import org.junit.Test
import org.sonar.plugins.jacoco.ExecutionDataVisitor

open class JaCoCoLoaderTest {

    open val c: ResourceUtils = ResourceUtils("target/test-classes/test_data/")

    open val s: ResourceUtils = ResourceUtils("target/test-classes/test_data/")

    open val classSuffix: String = "klass"

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
        val visitor = c.executionDataVisitor(jacocoExecName(simpleName))
        val coverageBuilder = c.coverageBuilder(klassName, visitor, classSuffix)
        val iClassCoverage = coverageBuilder.classes.first { it.name == klassName }
        val iSourceFileCoverage = coverageBuilder.sourceFiles.first { it.packageName + "/" + it.name == "${klassName}.kt" }
        assertThat(iClassCoverage.firstLine, equalTo(iSourceFileCoverage.firstLine))
//        assertThat(iClassCoverage.lastLine, equalTo(iSourceFileCoverage.lastLine))
        println("iClassCoverage.lastLine=${iClassCoverage.lastLine}, iSourceFileCoverage.lastLine=${iSourceFileCoverage.lastLine}")
        println("Lines of ${klassName}: ${s.lines(klassName).size} should be ${iSourceFileCoverage.lastLine}")
    }

    open fun jacocoExecName(klassName: String) = "jacoco-${klassName}.exec"

}