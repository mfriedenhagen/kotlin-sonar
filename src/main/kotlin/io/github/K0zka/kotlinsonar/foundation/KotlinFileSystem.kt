package io.github.K0zka.kotlinsonar.foundation

import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.fs.InputFile.Type

class KotlinFileSystem(private val fs: FileSystem) {

    private val predicates = fs.predicates()

    private val isKotlinFile = predicates.hasLanguage(kotlinLanguageName)

    private val isMainTypeFile = predicates.hasType(Type.MAIN)

    fun sourceInputFileFromRelativePath(relativePath: String): InputFile? = fs.inputFile(
            predicates.and(predicates.matchesPathPattern("**/" + relativePath), isKotlinFile, isMainTypeFile))

}
