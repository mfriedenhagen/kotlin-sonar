package io.github.K0zka.kotlinsonar.jacoco

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.junit.Test

import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.config.Settings
import org.sonar.api.scan.filesystem.PathResolver
import org.sonar.api.utils.Version
import org.sonar.java.JavaClasspath
import org.sonar.plugins.jacoco.JacocoConfiguration
import org.sonar.plugins.jacoco.JacocoConfiguration.REPORT_PATHS_PROPERTY
import org.sonar.plugins.java.api.JavaResourceLocator
import java.io.File

/**
 * Created by mirko on 21.04.17.
 */
class KotlinJaCoCoSensorTest {
    val fs = mock<FileSystem>()
    val pathResolver = mock<PathResolver>()
    val sut = KotlinJaCoCoSensor(
            mock<JacocoConfiguration>(),
            mock<ResourcePerspectives>(),
            fs,
            pathResolver,
            mock<JavaResourceLocator>(),
            mock<JavaClasspath>())

    @Test
    fun `Check description`() {
        val sensorDescriptor = mock<SensorDescriptor>()
        whenever(sensorDescriptor.onlyOnLanguage(kotlinLanguageName)).thenReturn(sensorDescriptor)
        whenever(sensorDescriptor.name("KotlinJaCoCoSensor")).thenReturn(sensorDescriptor)
        sut.describe(sensorDescriptor)
    }

    @Test
    fun `Check toString`() {
        assertThat("Kotlin KotlinJaCoCoSensor", equalTo(sut.toString()))
    }

    @Test
    fun `Check execute`() {
        val sensorContext = mock<SensorContext>()
        val settings = mock<Settings>()
        whenever(sensorContext.fileSystem()).thenReturn(fs)
        whenever(settings.getStringArray(REPORT_PATHS_PROPERTY)).thenReturn(arrayOf("target/jacoco.exec"))
        whenever(sensorContext.sonarQubeVersion).thenReturn(Version.create(5,6))
        whenever(sensorContext.settings()).thenReturn(settings)
        whenever(pathResolver.relativeFile(fs.baseDir(), null)).thenReturn(File("."))
        sut.execute(sensorContext)
    }

}