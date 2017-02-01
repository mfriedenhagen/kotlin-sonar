package io.github.K0zka.kotlinsonar.foundation

import io.github.K0zka.kotlinsonar.jacoco.KotlinCoverage
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.coverage.NewCoverage

class KotlinSensorContext(private val delegate: SensorContext) : SensorContext by delegate {
    override fun newCoverage(): NewCoverage {
        return KotlinCoverage(delegate.newCoverage())
    }
}