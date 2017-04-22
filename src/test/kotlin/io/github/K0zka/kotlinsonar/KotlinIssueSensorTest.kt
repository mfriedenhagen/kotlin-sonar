package io.github.K0zka.kotlinsonar

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
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
    fun `Check execute without files`() {
        val context = mock<SensorContext>()
        whenever(fileSystem.inputFiles(any())).thenReturn(emptyList())
        sut.execute(context)
    }

    @Test
    fun `Check execute with files`() {
        val context = mock<SensorContext>()
        val inputFile = mock<InputFile>()
        whenever(inputFile.language()).thenReturn(kotlinLanguageName)
        whenever(fileSystem.inputFiles(any())).thenReturn(listOf(inputFile))
        sut.execute(context)
    }

}