package com.example

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import java.io.PrintStream

private val NAME = "Michael J."
private val AGE = 51

class AKotlinThingTest {

    @Test
    fun `Check writeIt`() {
        val sut = AKotlinThing.valueOf(NAME, AGE)
        val (name, age) = sut
        assertThat(name, equalTo(NAME))
        assertThat(age, equalTo(AGE))
        val mockedStream = mock<PrintStream>()
        sut.writeIt(mockedStream)
        verify(mockedStream).println(sut.toString())
    }

    @Test
    fun `Check doIt`() {
        val sut = AKotlinThing.valueOf(NAME, AGE)
        val original = System.out
        val mockedStream = mock<PrintStream>()
        System.setOut(mockedStream)
        try {
            doIt()
            verify(mockedStream).println(sut.toString())
        } finally {
            System.setOut(original)
        }

    }
}