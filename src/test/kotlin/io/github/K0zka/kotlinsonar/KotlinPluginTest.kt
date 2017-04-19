package io.github.K0zka.kotlinsonar

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.sonar.api.Plugin.Context
import org.sonar.api.SonarQubeSide
import org.sonar.api.internal.SonarRuntimeImpl
import org.sonar.api.utils.Version

class KotlinPluginTest {
    @Test
    fun define() {
        val runtime = SonarRuntimeImpl.forSonarQube(Version.create(6, 2), SonarQubeSide.SCANNER)
        val context = Context(runtime)
        KotlinPlugin().define(context)
        assertThat(context.extensions.size, equalTo(14))
    }

}