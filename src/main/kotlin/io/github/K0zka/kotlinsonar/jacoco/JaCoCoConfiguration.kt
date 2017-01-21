package io.github.K0zka.kotlinsonar.jacoco

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.PropertyType
import org.sonar.api.batch.BatchSide
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.config.PropertyDefinition
import org.sonar.api.config.Settings
import org.sonar.api.resources.Qualifiers

@BatchSide
class JaCoCoConfiguration(private val settings: Settings, private val fileSystem: FileSystem) {

    fun shouldExecuteOnProject(reportFound: Boolean): Boolean {
        return hasGroovyFiles() && (reportFound || isCoverageToZeroWhenNoReport)
    }

    private fun hasGroovyFiles(): Boolean {
        return fileSystem.hasFiles(fileSystem.predicates().hasLanguage(kotlinLanguageName))
    }

    val reportPath: String
        get() = settings.getString(REPORT_PATH_PROPERTY) ?: REPORT_PATH_DEFAULT_VALUE

    val itReportPath: String
        get() = settings.getString(IT_REPORT_PATH_PROPERTY) ?: IT_REPORT_PATH_DEFAULT_VALUE

    private val isCoverageToZeroWhenNoReport: Boolean
        get() = settings.getBoolean(REPORT_MISSING_FORCE_ZERO) ?: REPORT_MISSING_FORCE_ZERO_DEFAULT_VALUE

    companion object {

        val REPORT_PATH_PROPERTY = "sonar.kotlin.jacoco.reportPath"
        val REPORT_PATH_DEFAULT_VALUE = "target/jacoco.exec"
        val IT_REPORT_PATH_PROPERTY = "sonar.kotlin.jacoco.itReportPath"
        val IT_REPORT_PATH_DEFAULT_VALUE = "target/jacoco-it.exec"
        val REPORT_MISSING_FORCE_ZERO = "sonar.kotlin.jacoco.reportMissing.force.zero"
        val REPORT_MISSING_FORCE_ZERO_DEFAULT_VALUE = false

        val propertyDefinitions: List<PropertyDefinition>
            get() = listOf(
                            PropertyDefinition.builder(JaCoCoConfiguration.REPORT_PATH_PROPERTY)
                                    .defaultValue(JaCoCoConfiguration.REPORT_PATH_DEFAULT_VALUE)
                                    .name("UT JaCoCo Report")
                                    .description("Path to the JaCoCo report file containing coverage data by unit tests. The path may be absolute or relative to the project base directory.")
                                    .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
                                    .build(),
                            PropertyDefinition.builder(JaCoCoConfiguration.IT_REPORT_PATH_PROPERTY)
                                    .defaultValue(JaCoCoConfiguration.IT_REPORT_PATH_DEFAULT_VALUE)
                                    .name("IT JaCoCo Report")
                                    .description("Path to the JaCoCo report file containing coverage data by integration tests. The path may be absolute or relative to the project base directory.")
                                    .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
                                    .build(),
                            PropertyDefinition.builder(JaCoCoConfiguration.REPORT_MISSING_FORCE_ZERO)
                                    .defaultValue(java.lang.Boolean.toString(JaCoCoConfiguration.REPORT_MISSING_FORCE_ZERO_DEFAULT_VALUE))
                                    .name("Force zero coverage")
                                    .description("Force coverage to 0% if no JaCoCo reports are found during analysis.")
                                    .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
                                    .type(PropertyType.BOOLEAN)
                                    .build()
                    )
    }
}
