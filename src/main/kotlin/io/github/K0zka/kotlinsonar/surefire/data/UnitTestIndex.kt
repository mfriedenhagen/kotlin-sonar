package io.github.K0zka.kotlinsonar.surefire.data

class UnitTestIndex {

    private val indexByClassname: MutableMap<String, UnitTestClassReport> = mutableMapOf()

    fun index(classname: String): UnitTestClassReport {
        var classReport: UnitTestClassReport? = indexByClassname[classname]
        if (classReport == null) {
            classReport = UnitTestClassReport()
            indexByClassname.put(classname, classReport)
        }
        return classReport
    }

    operator fun get(classname: String): UnitTestClassReport? {
        return indexByClassname[classname]
    }

    val classnames: Set<String>
        get() = indexByClassname.keys

    fun getIndexByClassname(): Map<String, UnitTestClassReport> {
        return indexByClassname
    }

    fun size(): Int {
        return indexByClassname.size
    }

    fun merge(classname: String, intoClassname: String): UnitTestClassReport? {
        val from = indexByClassname[classname]
        if (from != null) {
            val to = index(intoClassname)
            to.add(from)
            indexByClassname.remove(classname)
            return to
        }
        return null
    }

    fun remove(classname: String) {
        indexByClassname.remove(classname)
    }


}
