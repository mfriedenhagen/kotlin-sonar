package io.github.K0zka.kotlinsonar.surefire

import org.sonar.api.batch.DependedUpon
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.config.Settings
import org.sonar.api.scan.filesystem.PathResolver
import org.sonar.plugins.surefire.SurefireJavaParser
import org.sonar.plugins.surefire.SurefireSensor

@DependedUpon("surefire-java")
class KotlinSurefireSensor(private val kotlinSurefireParser: SurefireJavaParser, private val settings: Settings, private val fs: FileSystem, private val pathResolver: PathResolver) : SurefireSensor(
        kotlinSurefireParser,
        settings,
        fs,
        pathResolver

) {
}
