package io.github.K0zka.kotlinsonar.foundation

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.junit.Test
import org.sonar.api.batch.fs.FilePredicate
import org.sonar.api.batch.fs.FilePredicates
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile

class KotlinFileSystemTest {
    val predicates = mock<FilePredicates>()
    val fileSystem = mock<FileSystem>()
    val sut : KotlinFileSystem
    val kotlinFile = mock<FilePredicate>()
    val mainType = mock<FilePredicate>()

    init {
        whenever(predicates.hasLanguage(kotlinLanguageName)).thenReturn(kotlinFile)
        whenever(predicates.hasType(InputFile.Type.MAIN)).thenReturn(mainType)
        whenever(fileSystem.predicates()).thenReturn(predicates)
        sut = KotlinFileSystem(fileSystem)
    }

    @Test
    fun sourceInputFileFromRelativePath() {
        sut.sourceInputFileFromRelativePath("")
    }

}