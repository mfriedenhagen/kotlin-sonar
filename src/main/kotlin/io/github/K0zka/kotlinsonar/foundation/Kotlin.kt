package io.github.K0zka.kotlinsonar.foundation

import io.github.K0zka.kotlinsonar.KotlinPlugin
import io.github.K0zka.kotlinsonar.kotlinFileExtension
import io.github.K0zka.kotlinsonar.kotlinLanguageName
import org.sonar.api.config.Settings
import org.sonar.api.resources.AbstractLanguage

class Kotlin(private val settings: Settings) : AbstractLanguage(kotlinLanguageName, kotlinLanguageName) {

    override fun getFileSuffixes(): Array<out String>
            = arrayOf(kotlinFileExtension)

    fun getBinaryDirectories() : List<String> {
        var binaries = settings.getStringArray(KotlinPlugin.SONAR_KOTLIN_BINARIES)
        if (binaries.isEmpty()) {
            binaries = settings.getStringArray(KotlinPlugin.SONAR_KOTLIN_BINARIES_FALLBACK)
        }
        return binaries.toList()
    }
}
