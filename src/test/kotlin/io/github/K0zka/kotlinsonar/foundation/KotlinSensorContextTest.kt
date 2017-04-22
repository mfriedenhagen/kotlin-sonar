package io.github.K0zka.kotlinsonar.foundation

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.coverage.NewCoverage

class KotlinSensorContextTest {
    @Test
    fun newCoverage() {
        val sensorContext = mock<SensorContext>()
        whenever(sensorContext.newCoverage()).thenReturn(mock<NewCoverage>())
        val sut = KotlinSensorContext(sensorContext)
        assertNotNull(sut.newCoverage())
    }
}