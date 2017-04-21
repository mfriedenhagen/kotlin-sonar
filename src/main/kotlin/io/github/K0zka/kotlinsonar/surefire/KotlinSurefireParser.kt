package io.github.K0zka.kotlinsonar.surefire

import io.github.K0zka.kotlinsonar.foundation.KotlinJavaResourceLocator
import org.sonar.api.batch.BatchSide
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.component.ResourcePerspectives
import org.sonar.plugins.java.api.JavaResourceLocator
import org.sonar.plugins.surefire.SurefireJavaParser

@BatchSide
class KotlinSurefireParser(fileSystem: FileSystem, perspectives: ResourcePerspectives, javaSourcLocator: JavaResourceLocator) : SurefireJavaParser(
        perspectives,
        KotlinJavaResourceLocator(javaSourcLocator, fileSystem)
)
