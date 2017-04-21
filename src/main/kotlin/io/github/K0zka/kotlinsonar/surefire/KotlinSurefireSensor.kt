package io.github.K0zka.kotlinsonar.surefire

import io.github.K0zka.kotlinsonar.foundation.KotlinSensorContext
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.batch.DependedUpon
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.SensorContext
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.config.Settings
import org.sonar.api.scan.filesystem.PathResolver
import org.sonar.plugins.java.api.JavaResourceLocator
import org.sonar.plugins.surefire.SurefireJavaParser
import org.sonar.plugins.surefire.SurefireSensor

@DependedUpon("kotlin-sonar")
class KotlinSurefireSensor(perspectives: ResourcePerspectives, settings: Settings, fs: FileSystem, pathResolver: PathResolver, javaSourcLocator: JavaResourceLocator) : SurefireSensor(
        KotlinSurefireParser(fs, perspectives, javaSourcLocator),
        settings,
        fs,
        pathResolver
) {
    override fun describe(descriptor: SensorDescriptor) {
        descriptor.onlyOnLanguage(kotlinLanguageName).name(this.toString())
    }

    override fun execute(context: SensorContext) {
        super.execute(KotlinSensorContext(context))
    }

    override fun toString(): String {
        return "Kotlin " + javaClass.simpleName
    }

}
