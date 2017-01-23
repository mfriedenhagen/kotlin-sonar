package com.example

import java.io.PrintStream

data class AKotlinThing private constructor(val name: String, val age: Int) {

    fun writeIt(p : PrintStream) {
        p.println(toString())
    }

    companion object {
        fun valueOf(name: String, age: Int) : AKotlinThing = AKotlinThing(name, age)
    }
}

fun doIt() {
    AKotlinThing.valueOf("Michael J.", 51).writeIt(System.out)
}