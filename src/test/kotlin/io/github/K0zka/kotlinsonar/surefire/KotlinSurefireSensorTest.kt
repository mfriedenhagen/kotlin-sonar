package io.github.K0zka.kotlinsonar.surefire

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.junit.Test
import org.sonar.api.batch.fs.FilePredicates
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.config.Settings
import org.sonar.api.scan.filesystem.PathResolver
import org.sonar.plugins.java.api.JavaResourceLocator

class KotlinSurefireSensorTest {
    val fs = mock<FileSystem>()
    val resourceLocator = mock<JavaResourceLocator>()
    val sut = KotlinSurefireSensor(
            mock<ResourcePerspectives>(),
            mock<Settings>(),
            fs,
            mock<PathResolver>(),
            resourceLocator)

    @Test
    fun `Check describe`() {
        val descriptor = mock<SensorDescriptor>()
        whenever(descriptor.onlyOnLanguage(kotlinLanguageName)).thenReturn(descriptor)
        whenever(descriptor.name(KotlinSurefireSensor::javaClass.name)).thenReturn(descriptor)
        sut.describe(descriptor)
    }

    @Test
    fun `Check execute`() {
        val context = mock<SensorContext>()
        val predicates = mock<FilePredicates>()
        whenever(fs.predicates()).thenReturn(predicates)
        sut.execute(context)
    }

    @Test
    fun `Check toString`() {
        assertThat("Kotlin KotlinSurefireSensor", equalTo(sut.toString()))
    }

}