package io.github.K0zka.kotlinsonar.surefire.data


class UnitTestClassReport {
    var errors = 0
        private set
    var failures = 0
        private set
    var skipped = 0
        private set
    var tests = 0
        private set
    var durationMilliseconds = 0L
        private set


    var negativeTimeTestNumber = 0L
        private set
    private var results = mutableListOf<UnitTestResult>()

    fun add(other: UnitTestClassReport): UnitTestClassReport {
        for (otherResult in other.getResults()) {
            add(otherResult)
        }
        return this
    }

    fun add(result: UnitTestResult): UnitTestClassReport {
        results.add(result)
        if (result.status == UnitTestResult.STATUS_SKIPPED) {
            skipped += 1

        } else if (result.status == UnitTestResult.STATUS_FAILURE) {
            failures += 1

        } else if (result.status == UnitTestResult.STATUS_ERROR) {
            errors += 1
        }
        tests += 1
        if (result.durationMilliseconds < 0) {
            negativeTimeTestNumber += 1
        } else {
            durationMilliseconds += result.durationMilliseconds
        }
        return this
    }

    fun getResults(): List<UnitTestResult> {
        return results
    }
}
