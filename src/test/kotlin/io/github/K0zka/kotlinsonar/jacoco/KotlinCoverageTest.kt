package io.github.K0zka.kotlinsonar.jacoco

import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Assert.assertSame
import org.junit.Test
import org.slf4j.Logger
import org.sonar.api.batch.fs.internal.DefaultInputFile
import org.sonar.api.batch.sensor.coverage.CoverageType
import org.sonar.api.batch.sensor.coverage.NewCoverage
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage
import org.sonar.api.batch.sensor.internal.SensorStorage

class KotlinCoverageTest {
    val delegateMock = mock<NewCoverage>()
    val loggerMock = mock<Logger>()
    val inputFileMock = mock<DefaultInputFile>() {
        on { lines() }.thenReturn(10)
    }

    @After
    fun verifyNoMoreInteractions() {
        verifyNoMoreInteractions(delegateMock)
        verifyNoMoreInteractions(loggerMock)
    }

    @Test
    fun `Check onFile`() {
        // Given
        val sut = KotlinCoverage(delegateMock, loggerMock)
        // When
        val actual = sut.onFile(inputFileMock)
        // Then
        assertSame(actual, sut)
        verify(delegateMock).onFile(inputFileMock)
    }

    @Test
    fun `Check ofType`() {
        // Given
        val sut = KotlinCoverage(delegateMock)
        // When
        sut.ofType(CoverageType.IT)
        // Then
        verify(delegateMock).ofType(CoverageType.IT)
    }

    @Test
    fun `Check lineHits`() {
        // Given
        val sut = KotlinCoverage(DefaultCoverage(), loggerMock)
        sut.onFile(inputFileMock)
        // When
        val actual = sut.lineHits(1, 0)
        // Then
        assertSame(sut, actual)
        verify(loggerMock, never()).debug(any())
    }

    @Test
    fun `Check lineHits returns even if delegated lineHits chokes`() {
        // Given
        val sut = KotlinCoverage(DefaultCoverage(), loggerMock)
        sut.onFile(inputFileMock)
        // When
        val actual = sut.lineHits(0, 0)
        // Then
        assertSame(sut, actual)
        verify(loggerMock, times(1)).debug(argThat { contains("has additional lines") })
    }

    @Test
    fun `Check conditions`() {
        // Given
        val sut = KotlinCoverage(DefaultCoverage(), loggerMock)
        sut.onFile(inputFileMock)
        // When
        val actual = sut.conditions(1, 0, 0)
        // Then
        assertSame(sut, actual)
        verify(loggerMock, never()).debug(any())
    }

    @Test
    fun `Check conditions returns even if delegated conditions chokes`() {
        // Given
        val sut = KotlinCoverage(DefaultCoverage(), loggerMock)
        sut.onFile(inputFileMock)
        // When
        val actual = sut.conditions(11, 1, 0)
        // Then
        assertSame(sut, actual)
        verify(loggerMock, times(1)).debug(argThat { contains("has additional lines") })
    }

    @Test
    fun `Check save`() {
        // Given
        val storageMock = mock<SensorStorage>()
        val sut = KotlinCoverage(DefaultCoverage(storageMock), loggerMock)
        sut.onFile(inputFileMock)
        // When
        sut.save()
        // Then
        verify(storageMock, times(1)).store(any<DefaultCoverage>())
    }

}