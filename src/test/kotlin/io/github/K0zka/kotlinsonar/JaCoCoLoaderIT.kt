package io.github.K0zka.kotlinsonar

class JaCoCoLoaderIT : JaCoCoLoaderTest() {

    override val c: ResourceUtils = ResourceUtils("target/classes/")

    override val s: ResourceUtils = ResourceUtils("src/main/kotlin/")

    override val classSuffix: String = "class"

    override fun jacocoExecName(klassName: String) = "../jacoco.exec"

}