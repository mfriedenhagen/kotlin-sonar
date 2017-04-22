package io.github.K0zka.kotlinsonar.jacoco

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.sensor.coverage.CoverageType
import org.sonar.api.batch.sensor.coverage.NewCoverage

/**
 * Encapsulate the {@link DefaultCoverage} delegate.
 *
 * We need to suppress the {@link IllegalStateException}s thrown
 * when the number of lines is out of bounds.
 * Kotlin produces extra class files for "functions" in a
 * {@literal *Kt.class} which have no direct correspondence
 * in the source.
 */
class KotlinCoverage(
        private val delegate: NewCoverage,
        private val logger: Logger = LoggerFactory.getLogger(KotlinCoverage::class.java)) : NewCoverage {

    private var inputFile: InputFile? = null

    override fun onFile(inputFile: InputFile): NewCoverage {
        this.inputFile = inputFile
        delegate.onFile(inputFile)
        return this
    }

    override fun ofType(type: CoverageType): NewCoverage {
        delegate.ofType(type)
        return this
    }

    override fun lineHits(line: Int, hits: Int): NewCoverage {
        try {
            delegate.lineHits(line, hits)
        } catch (e: IllegalStateException) {
            logger.debug("${inputFile} has additional lines: ${e}")
        }
        return this
    }

    override fun conditions(line: Int, conditions: Int, coveredConditions: Int): NewCoverage {
        try {
            delegate.conditions(line, conditions, coveredConditions)
        } catch (e: IllegalStateException) {
            logger.debug("${inputFile} has additional lines: ${e}")
        }
        return this
    }

    override fun save() {
        delegate.save()
    }
}
