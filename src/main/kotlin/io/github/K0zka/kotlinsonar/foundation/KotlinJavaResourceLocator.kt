package io.github.K0zka.kotlinsonar.foundation

import io.github.K0zka.kotlinsonar.kotlinFileExtension
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.slf4j.LoggerFactory
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.fs.InputFile
import org.sonar.plugins.java.api.JavaResourceLocator

class KotlinJavaResourceLocator(
        delegate: JavaResourceLocator,
        private val fileSystem: FileSystem
) : JavaResourceLocator by delegate {

    private val logger = LoggerFactory.getLogger(KotlinJavaResourceLocator::class.java)

    override fun findResourceByClassName(className: String): InputFile? {
        val p = fileSystem.predicates()
        val inputFile = fileSystem.inputFile(p.and(
                p.matchesPathPattern("**/" + className.replace('.', '/') + '.' + kotlinFileExtension),
                p.hasLanguage(kotlinLanguageName)
        ))
        logger.debug("Processing {}", inputFile)
        return inputFile
    }
}