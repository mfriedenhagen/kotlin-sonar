package io.github.K0zka.kotlinsonar.foundation


import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.K0zka.kotlinsonar.KotlinPlugin
import org.junit.Test
import org.sonar.api.config.Settings

class KotlinTest {
    val settings = mock<Settings>()
    val sut = Kotlin(settings)

    @Test
    fun `Check getFileSuffixes`() {
        assertThat(".kt", equalTo(sut.fileSuffixes[0]))
    }

    @Test
    fun `Check getBinaryDirectories if SONAR_KOTLIN_BINARIES is not set`() {
        whenever(settings.getStringArray(KotlinPlugin.SONAR_KOTLIN_BINARIES)).thenReturn(emptyArray())
        whenever(settings.getStringArray(KotlinPlugin.SONAR_KOTLIN_BINARIES_FALLBACK)).thenReturn(arrayOf("src"))
        assertThat(1, equalTo(sut.getBinaryDirectories().size))
    }

    @Test
    fun `Check getBinaryDirectories if SONAR_KOTLIN_BINARIES is set`() {
        whenever(settings.getStringArray(KotlinPlugin.SONAR_KOTLIN_BINARIES)).thenReturn(arrayOf("src/main/kotlin", "target/generated-sources"))
        assertThat(2, equalTo(sut.getBinaryDirectories().size))
    }

    @Test
    fun `Check tokenize`() {
        sut.tokenize("")
    }

}