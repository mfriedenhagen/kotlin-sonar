package io.github.K0zka.kotlinsonar.jacoco

import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.coverage.NewCoverage
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage

class KotlinSensorContext(private val delegate: SensorContext) : SensorContext by delegate {
    override fun newCoverage(): NewCoverage {
        return KotlinCoverage(delegate.newCoverage() as DefaultCoverage)
    }
}