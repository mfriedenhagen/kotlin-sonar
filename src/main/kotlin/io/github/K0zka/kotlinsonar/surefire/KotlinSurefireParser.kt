package io.github.K0zka.kotlinsonar.surefire

import io.github.K0zka.kotlinsonar.foundation.Kotlin
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import io.github.K0zka.kotlinsonar.surefire.data.SurefireStaxHandler
import io.github.K0zka.kotlinsonar.surefire.data.UnitTestClassReport
import io.github.K0zka.kotlinsonar.surefire.data.UnitTestIndex
import org.sonar.api.batch.BatchSide
import org.sonar.api.batch.fs.FilePredicate
import org.sonar.api.batch.fs.FilePredicates
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.measures.CoreMetrics
import org.sonar.api.measures.Metric
import org.sonar.api.test.MutableTestPlan
import org.sonar.api.test.TestCase
import org.sonar.api.utils.MessageException
import org.sonar.api.utils.ParsingUtils
import org.sonar.api.utils.StaxParser
import org.sonar.api.utils.log.Loggers
import java.io.File
import java.io.Serializable
import javax.xml.stream.XMLStreamException

@BatchSide
class KotlinSurefireParser(private val kotlin: Kotlin, private val perspectives: ResourcePerspectives, private val fs: FileSystem) {


    fun collect(context: SensorContext, reportsDir: File) {
        val xmlFiles = getReports(reportsDir)
        if (xmlFiles.size > 0) {
            parseFiles(context, xmlFiles)
        }
    }

    private fun parseFiles(context: SensorContext, reports: Array<File>) {
        val index = UnitTestIndex()
        parseFiles(reports, index)
        sanitize(index)
        save(index, context)
    }

    private fun save(index: UnitTestIndex, context: SensorContext) {
        var negativeTimeTestNumber: Long = 0
        for (entry in index.getIndexByClassname().entries) {
            val report = entry.value
            if (report.tests > 0) {
                negativeTimeTestNumber += report.negativeTimeTestNumber
                val inputFile = getUnitTestInputFile(entry.key)
                if (inputFile != null) {
                    save(report, inputFile, context)
                } else {
                    LOGGER.warn("Resource not found: {}", entry.key)
                }
            }
        }
        if (negativeTimeTestNumber > 0) {
            LOGGER.warn("There is {} test(s) reported with negative time by surefire, total duration may not be accurate.", negativeTimeTestNumber)
        }
    }

    private fun save(report: UnitTestClassReport, inputFile: InputFile, context: SensorContext) {
        val testsCount = report.tests - report.skipped
        saveMeasure(context, inputFile, CoreMetrics.SKIPPED_TESTS, report.skipped)
        saveMeasure(context, inputFile, CoreMetrics.TESTS, testsCount)
        saveMeasure(context, inputFile, CoreMetrics.TEST_ERRORS, report.skipped)
        saveMeasure(context, inputFile, CoreMetrics.TEST_FAILURES, report.failures)
        saveMeasure(context, inputFile, CoreMetrics.TEST_EXECUTION_TIME, report.durationMilliseconds)
        val passedTests = testsCount - report.errors - report.failures
        if (testsCount > 0) {
            val percentage = passedTests * 100.0 / testsCount
            saveMeasure(context, inputFile, CoreMetrics.TEST_SUCCESS_DENSITY, ParsingUtils.scaleValue(percentage))
        }
        saveResults(inputFile, report)
    }

    protected fun saveResults(testFile: InputFile, report: UnitTestClassReport) {
        for (unitTestResult in report.getResults()) {
            val testPlan = perspectives.`as`(MutableTestPlan::class.java, testFile)
            if (testPlan != null) {
                testPlan.addTestCase(unitTestResult.name)
                        .setDurationInMs(Math.max(unitTestResult.durationMilliseconds, 0))
                        .setStatus(TestCase.Status.of(unitTestResult.status))
                        .setMessage(unitTestResult.message)
                        .setStackTrace(unitTestResult.stackTrace)
            }
        }
    }

    protected fun getUnitTestInputFile(classKey: String): InputFile? {
        val fileName = classKey.replace('.', '/')
        val p = fs.predicates()
        val fileNamePredicates = getFileNamePredicateFromSuffixes(p, fileName, kotlin.getFileSuffixes())
        val searchPredicate = p.and(p.and(p.hasLanguage(kotlinLanguageName), p.hasType(InputFile.Type.TEST)), fileNamePredicates)
        if (fs.hasFiles(searchPredicate)) {
            return fs.inputFiles(searchPredicate).iterator().next()
        } else {
            return null
        }
    }

    companion object {

        private val LOGGER = Loggers.get(KotlinSurefireParser::class.java)

        private fun getReports(dir: File?): Array<File> {
            if (dir == null) {
                return arrayOf()
            } else if (!dir.isDirectory) {
                LOGGER.warn("Reports path not found: " + dir.absolutePath)
                return arrayOf()
            }
            var unitTestResultFiles = findXMLFilesStartingWith(dir, "TEST-")
            if (unitTestResultFiles.size == 0) {
                // maybe there's only a test suite result file
                unitTestResultFiles = findXMLFilesStartingWith(dir, "TESTS-")
            }
            return unitTestResultFiles
        }

        private fun findXMLFilesStartingWith(dir: File, fileNameStart: String): Array<File> {
            return dir.listFiles { folder, name -> name.startsWith(fileNameStart) && name.endsWith(".xml") }
        }

        private fun parseFiles(reports: Array<File>, index: UnitTestIndex) {
            val parser = StaxParser(SurefireStaxHandler(index))
            for (report in reports) {
                try {
                    parser.parse(report)
                } catch (e: XMLStreamException) {
                    throw MessageException.of("Fail to parse the Surefire report: " + report, e)
                }

            }
        }

        private fun sanitize(index: UnitTestIndex) {
            for (classname in index.classnames) {
                if (classname.contains('$')) {
                    // Surefire reports classes whereas sonar supports files
                    val parentClassName = classname.substringBefore('$')
                    index.merge(classname, parentClassName)
                }
            }
        }

        private fun getFileNamePredicateFromSuffixes(p: FilePredicates, fileName: String, suffixes: Array<String>): FilePredicate {
            val fileNamePredicates = mutableListOf<FilePredicate>()
            for (suffix in suffixes) {
                fileNamePredicates.add(p.matchesPathPattern("**/" + fileName + suffix))
            }
            return p.or(fileNamePredicates)
        }

        private fun <T : Serializable> saveMeasure(context: SensorContext, inputFile: InputFile, metric: Metric<T>, value: T) {
            context.newMeasure<T>().forMetric(metric).on(inputFile).withValue(value).save()
        }
    }

}
