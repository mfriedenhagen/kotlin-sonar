package io.github.K0zka.kotlinsonar

import org.junit.Test
import org.sonar.api.server.rule.RulesDefinition

class KotlinLintRulesDefinitionTest {
    val sut = KotlinLintRulesDefinition()

    @Test
    fun `Check define`() {
        val context = RulesDefinition.Context()
        sut.define(context)
    }
}