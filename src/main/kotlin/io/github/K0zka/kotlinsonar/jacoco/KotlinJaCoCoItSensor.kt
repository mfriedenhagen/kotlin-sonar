package io.github.K0zka.kotlinsonar.jacoco

import io.github.K0zka.kotlinsonar.foundation.KotlinJavaResourceLocator
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.batch.fs.FileSystem
import org.sonar.api.batch.sensor.SensorDescriptor
import org.sonar.api.component.ResourcePerspectives
import org.sonar.api.scan.filesystem.PathResolver
import org.sonar.java.JavaClasspath
import org.sonar.plugins.jacoco.JaCoCoItSensor
import org.sonar.plugins.jacoco.JacocoConfiguration

class KotlinJaCoCoItSensor(
        configuration : JacocoConfiguration,
        perspectives: ResourcePerspectives,
        fileSystem :FileSystem,
        pathResolver:PathResolver,
        javaResourceLocator: KotlinJavaResourceLocator,
        javaClasspath: JavaClasspath
) : JaCoCoItSensor(
        configuration,
        perspectives,
        fileSystem,
        pathResolver,
        javaResourceLocator,
        javaClasspath

) {

    override fun describe(descriptor: SensorDescriptor) {
        descriptor.onlyOnLanguage(kotlinLanguageName).name(this.toString())
    }
    override fun toString(): String {
        return "Kotlin " + javaClass.simpleName
    }
}
