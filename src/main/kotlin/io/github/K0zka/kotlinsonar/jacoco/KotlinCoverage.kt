package io.github.K0zka.kotlinsonar.jacoco

import org.slf4j.LoggerFactory
import org.sonar.api.batch.sensor.coverage.NewCoverage
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage

class KotlinCoverage(private val delegate: DefaultCoverage) : NewCoverage by delegate {
    private val logger = LoggerFactory.getLogger(KotlinCoverage::class.java)

    override fun lineHits(line: Int, hits: Int): NewCoverage {
        try {
            return delegate.lineHits(line, hits)
        } catch (e: IllegalStateException) {
            logger.warn("${delegate.inputFile()} has additional lines: ${e}")
            return delegate
        }
    }

    override fun conditions(line: Int, conditions: Int, coveredConditions: Int): NewCoverage {
        try {
            return delegate.conditions(line, conditions, coveredConditions)
        } catch (e: IllegalStateException) {
            logger.warn("${delegate.inputFile()} has additional lines: ${e}")
            return delegate
        }

    }
}
