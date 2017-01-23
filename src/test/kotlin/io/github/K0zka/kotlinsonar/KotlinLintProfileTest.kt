package io.github.K0zka.kotlinsonar

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test
import org.sonar.api.utils.ValidationMessages

class KotlinLintProfileTest {
    @Test
    fun should_create_profile() {
        val validationMessages = ValidationMessages.create()
        val sut = KotlinLintProfile().createProfile(validationMessages)
        assertThat(sut.language, equalTo(kotlinLanguageName))
    }
}