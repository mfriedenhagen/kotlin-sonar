/*
 * Sonar Groovy Plugin
 * Copyright (C) 2010-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.github.K0zka.kotlinsonar.jacoco

import org.jacoco.core.data.*

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility class to merge JaCoCo reports.

 * This class handles two versions of JaCoCo binary format to merge.
 */
object JaCoCoReportMerger {

    /**
     * Merge all reports in reportOverall.
     * @param reportOverall destination file of merge.
     * *
     * @param reports files to be merged.
     */
    fun mergeReports(reportOverall: File, vararg reports: File) {
        val infoStore = SessionInfoStore()
        val dataStore = ExecutionDataStore()
        loadSourceFiles(infoStore, dataStore, *reports)
        try {
            BufferedOutputStream(FileOutputStream(reportOverall)).use { outputStream ->
                val visitor = ExecutionDataWriter(outputStream)
                infoStore.accept(visitor as ISessionInfoVisitor)
                dataStore.accept(visitor as IExecutionDataVisitor)
            }
        } catch (e: IOException) {
            throw IllegalStateException(String.format("Unable to write overall coverage report %s", reportOverall.absolutePath), e)
        }

    }

    private fun loadSourceFiles(infoStore: ISessionInfoVisitor, dataStore: IExecutionDataVisitor, vararg reports: File) {
        for (report in reports) {
            if (report.isFile) {
                JaCoCoReportReader(report).readJacocoReport(dataStore, infoStore)
            }
        }
    }

}
