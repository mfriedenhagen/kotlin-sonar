package io.github.K0zka.kotlinsonar.surefire.data

import org.codehaus.staxmate.`in`.ElementFilter
import org.codehaus.staxmate.`in`.SMEvent
import org.codehaus.staxmate.`in`.SMHierarchicCursor
import org.codehaus.staxmate.`in`.SMInputCursor
import org.sonar.api.utils.ParsingUtils
import org.sonar.api.utils.StaxParser
import java.text.ParseException
import java.util.*
import javax.xml.stream.XMLStreamException

class SurefireStaxHandler(private val index: UnitTestIndex) : StaxParser.XmlStreamHandler {

    @Throws(XMLStreamException::class)
    override fun stream(rootCursor: SMHierarchicCursor) {
        val testSuite = rootCursor.constructDescendantCursor(ElementFilter("testsuite"))
        var testSuiteEvent: SMEvent?
        testSuiteEvent = testSuite.next
        while (testSuiteEvent != null) {
            if (testSuiteEvent.compareTo(SMEvent.START_ELEMENT) == 0) {
                val testSuiteClassName = testSuite.getAttrValue("name")
                if (testSuiteClassName.contains('$')) {
                    // test suites for inner classes are ignored
                    return
                }
                handleTestCases(testSuiteClassName, testSuite.childCursor(ElementFilter("testcase")))
            }
            testSuiteEvent = testSuite.next
        }
    }

    @Throws(XMLStreamException::class)
    private fun handleTestCases(testSuiteClassName: String, testCase: SMInputCursor) {
        var event: SMEvent?
        event = testCase.next
        while (event != null) {
            if (event.compareTo(SMEvent.START_ELEMENT) == 0) {
                val testClassName = getClassname(testCase, testSuiteClassName)
                val classReport = index.index(testClassName)
                parseTestCase(testCase, classReport)
            }
            event = testCase.next
        }
    }

    @Throws(XMLStreamException::class)
    private fun getClassname(testCaseCursor: SMInputCursor, defaultClassname: String): String {
        var testClassName = testCaseCursor.getAttrValue("classname")
        if (testClassName?.isNotBlank()?:false && testClassName.endsWith(")")) {
            testClassName = testClassName.substring(0, testClassName.indexOf('('))
        }
        return testClassName ?: defaultClassname
    }

    @Throws(XMLStreamException::class)
    private fun parseTestCase(testCaseCursor: SMInputCursor, report: UnitTestClassReport) {
        report.add(parseTestResult(testCaseCursor))
    }

    @Throws(XMLStreamException::class)
    private fun setStackAndMessage(result: UnitTestResult, stackAndMessageCursor: SMInputCursor) {
        result.message = stackAndMessageCursor.getAttrValue("message")
        val stack = stackAndMessageCursor.collectDescendantText()
        result.stackTrace = stack
    }

    @Throws(XMLStreamException::class)
    private fun parseTestResult(testCaseCursor: SMInputCursor): UnitTestResult {
        val detail = UnitTestResult()
        val name = getTestCaseName(testCaseCursor)
        detail.name = name

        var status = UnitTestResult.STATUS_OK
        var duration = getTimeAttributeInMS(testCaseCursor)

        val childNode = testCaseCursor.descendantElementCursor()
        if (childNode.next != null) {
            val elementName = childNode.localName
            if ("skipped" == elementName) {
                status = UnitTestResult.STATUS_SKIPPED
                // bug with surefire reporting wrong time for skipped tests
                duration = 0L

            } else if ("failure" == elementName) {
                status = UnitTestResult.STATUS_FAILURE
                setStackAndMessage(detail, childNode)

            } else if ("error" == elementName) {
                status = UnitTestResult.STATUS_ERROR
                setStackAndMessage(detail, childNode)
            }
        }
        while (childNode.next != null) {
            // make sure we loop till the end of the elements cursor
        }
        detail.durationMilliseconds = duration
        detail.status = status
        return detail
    }

    @Throws(XMLStreamException::class)
    private fun getTimeAttributeInMS(testCaseCursor: SMInputCursor): Long {
        // hardcoded to Locale.ENGLISH see http://jira.codehaus.org/browse/SONAR-602
        try {
            val time = ParsingUtils.parseNumber(testCaseCursor.getAttrValue("time"), Locale.ENGLISH)
            return if (!java.lang.Double.isNaN(time)) ParsingUtils.scaleValue(time * 1000, 3).toLong() else 0L
        } catch (e: ParseException) {
            throw XMLStreamException(e)
        }

    }

    @Throws(XMLStreamException::class)
    private fun getTestCaseName(testCaseCursor: SMInputCursor): String {
        val classname = testCaseCursor.getAttrValue("classname")
        val name = testCaseCursor.getAttrValue("name")
        if (classname.contains('$')) {
            return classname.substringAfter('$') + "/" + name
        } else {
            return name
        }
    }

}
