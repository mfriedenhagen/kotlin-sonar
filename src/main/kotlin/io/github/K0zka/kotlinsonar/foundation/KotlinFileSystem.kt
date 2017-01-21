package io.github.K0zka.kotlinsonar.foundation

import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
import org.sonar.api.batch.fs.InputFile.Type
import java.io.File

class KotlinFileSystem(private val fileSystem: FileSystem) {

    private val predicates = fileSystem.predicates()

    private val isKotlinFile = predicates.hasLanguage(kotlinLanguageName)

    private val isMainTypeFile = predicates.hasType(Type.MAIN)

    fun sourceFiles(): List<File> = fileSystem.files(predicates.and(isKotlinFile, isMainTypeFile)).toList()

    fun groovyInputFiles(): List<InputFile> = fileSystem.inputFiles(isKotlinFile).toList()

    fun sourceInputFiles(): List<InputFile> = fileSystem.inputFiles(
            predicates.and(isKotlinFile, isMainTypeFile)).toList()

    fun sourceInputFileFromRelativePath(relativePath: String): InputFile? = fileSystem.inputFile(
            predicates.and(predicates.matchesPathPattern("**/" + relativePath), isKotlinFile, isMainTypeFile))

}
