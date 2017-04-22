package io.github.K0zka.kotlinsonar.surefire

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.component.ResourcePerspectives
import org.sonar.plugins.java.api.JavaResourceLocator


class KotlinSurefireParserTest {
    @Test
    fun `Check exists`() {
        KotlinSurefireParser(mock<FileSystem>(), mock<ResourcePerspectives>(), mock<JavaResourceLocator>())
    }
}