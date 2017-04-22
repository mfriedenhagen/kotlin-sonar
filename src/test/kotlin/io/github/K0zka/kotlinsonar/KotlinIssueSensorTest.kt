package io.github.K0zka.kotlinsonar

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.sonar.api.batch.fs.FilePredicate
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor

class KotlinIssueSensorTest {
    val fileSystem = mock<FileSystem>()
    val sut = KotlinIssueSensor(fileSystem)

    @Test
    fun `Check describe`() {
        val descriptor = mock<SensorDescriptor>()
        whenever(descriptor.onlyOnLanguage(kotlinLanguageName)).thenReturn(descriptor)
        sut.describe(descriptor)
    }

    @Test
    fun `Check execute`() {
        val context = mock<SensorContext>()
        val p = FilePredicate{ it.language() == kotlinLanguageName }
        whenever(fileSystem.inputFiles(p)).thenReturn(emptyList())
        sut.execute(context)
    }

}