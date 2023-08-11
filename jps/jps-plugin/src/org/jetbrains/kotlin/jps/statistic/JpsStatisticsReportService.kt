/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.statistic

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.jps.ModuleChunk
import org.jetbrains.jps.incremental.CompileContext
import org.jetbrains.jps.incremental.ModuleLevelBuilder
import org.jetbrains.kotlin.build.report.FileReportSettings
import org.jetbrains.kotlin.build.report.HttpReportSettings
import org.jetbrains.kotlin.build.report.metrics.*
import org.jetbrains.kotlin.build.report.statistics.BuildDataType
import org.jetbrains.kotlin.build.report.statistics.BuildStartParameters
import org.jetbrains.kotlin.build.report.statistics.HttpReportService
import org.jetbrains.kotlin.build.report.statistics.StatTag
import org.jetbrains.kotlin.build.report.statistics.file.FileReportService
import org.jetbrains.kotlin.compilerRunner.JpsKotlinLogger
import org.jetbrains.kotlin.jps.build.KotlinDirtySourceFilesHolder
import java.io.File
import java.net.InetAddress
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

interface JpsBuilderMetricReporter : BuildMetricsReporter<JpsBuildTime, JpsBuildPerformanceMetric> {
    fun flush(context: CompileContext): JpsCompileStatisticsData
    fun buildFinish(moduleChunk: ModuleChunk, context: CompileContext)
    fun setResult(exitCode: ModuleLevelBuilder.ExitCode)
    fun addCompiledSources(files: Collection<String>)
    fun addChangedFiles(files: Collection<String>)
    fun addSourcesInformation(fileHolder: KotlinDirtySourceFilesHolder)

    fun join(reporter: JpsBuilderMetricReporter)

    fun getModuleName(): String
}

private const val jpsBuildTaskName = "JPS build"

class JpsBuilderMetricReporterImpl(
    chunk: ModuleChunk,
    private val reporter: BuildMetricsReporterImpl<JpsBuildTime, JpsBuildPerformanceMetric>,
    private val label: String? = null,
    private val kotlinVersion: String = "kotlin_version",
) :
    JpsBuilderMetricReporter, BuildMetricsReporter<JpsBuildTime, JpsBuildPerformanceMetric> by reporter {

    companion object {
        private val hostName: String? = try {
            InetAddress.getLocalHost().hostName
        } catch (_: Exception) {
            //do nothing
            null
        }
        private val uuid = UUID.randomUUID()
    }


    private val startTime = System.currentTimeMillis()
    private var finishTime: Long = 0L
    private val tags = HashSet<StatTag>()
    private val module = chunk.name
    private var exitCode: ModuleLevelBuilder.ExitCode? = null
    private val compiledSources = HashSet<String>()
    private val changedFiles = HashSet<String>()

    override fun buildFinish(moduleChunk: ModuleChunk, context: CompileContext) {
        finishTime = System.currentTimeMillis()
    }

    override fun setResult(exitCode: ModuleLevelBuilder.ExitCode) {
        this.exitCode = exitCode
    }

    override fun addCompiledSources(files: Collection<String>) {
        compiledSources.addAll(files)
    }

    override fun addChangedFiles(files: Collection<String>) {
        changedFiles.addAll(files)
    }

    override fun addSourcesInformation(fileHolder: KotlinDirtySourceFilesHolder) {
        addCompiledSources(fileHolder.allDirtyFiles.map { it.path })
        addChangedFiles(fileHolder.allDirtyFiles.map { it.path })
        addChangedFiles(fileHolder.allRemovedFilesFiles.map { it.path })
    }

    override fun join(reporter: JpsBuilderMetricReporter) {
        addMetrics(reporter.getMetrics())
        addCompiledSources(compiledSources)
    }

    override fun getModuleName(): String = module

    override fun flush(context: CompileContext): JpsCompileStatisticsData {
        val buildMetrics = reporter.getMetrics()
        return JpsCompileStatisticsData(
            projectName = context.projectDescriptor.project.name,
            label = label,
            taskName = module,
            taskResult = exitCode?.name,
            startTimeMs = startTime,
            durationMs = finishTime - startTime,
            tags = tags,
            buildUuid = uuid.toString(),
            changes = changedFiles.toList(),
            kotlinVersion = kotlinVersion,
            hostName = hostName,
            finishTime = finishTime,
            buildTimesMetrics = buildMetrics.buildTimes.asMapMs(),
            performanceMetrics = buildMetrics.buildPerformanceMetrics.asMap(),
            compilerArguments = emptyList(), //TODO will be updated in KT-58026
            nonIncrementalAttributes = emptySet(),
            type = BuildDataType.JPS_DATA.name,
            fromKotlinPlugin = true,
            compiledSources = compiledSources.toList(),
            skipMessage = null,
            icLogLines = emptyList(),
            gcTimeMetrics = buildMetrics.gcMetrics.asGcTimeMap(),
            gcCountMetrics = buildMetrics.gcMetrics.asGcCountMap(),
            kotlinLanguageVersion = null
        )
    }

}

// TODO test UserDataHolder in CompileContext to store CompileStatisticsData.Build or KotlinBuilderMetric
class JpsStatisticsReportService {

    private val fileReportSettings: FileReportSettings? = initFileReportSettings()
    private val httpReportSettings: HttpReportSettings? = initHttpReportSettings()

    companion object {
        private fun initFileReportSettings(): FileReportSettings? {
            return System.getProperty("kotlin.build.report.file.output_dir")?.let { FileReportSettings(File(it)) }
        }

        private fun initHttpReportSettings(): HttpReportSettings? {
            val httpReportUrl = System.getProperty("kotlin.build.report.http.url") ?: return null
            val httpReportUser = System.getProperty("kotlin.build.report.http.user")
            val httpReportPassword = System.getProperty("kotlin.build.report.http.password")
            val includeGitBranch = System.getProperty("kotlin.build.report.http.git_branch", "false").toBoolean()
            val verboseEnvironment = System.getProperty("kotlin.build.report.http.environment.verbose", "false").toBoolean()
            return HttpReportSettings(httpReportUrl, httpReportUser, httpReportPassword, verboseEnvironment, includeGitBranch)
        }
    }

    private val buildMetrics = HashMap<String, JpsBuilderMetricReporter>()
    private val finishedModuleBuildMetrics = ArrayList<JpsBuilderMetricReporter>()
    private val log = Logger.getInstance("#org.jetbrains.kotlin.jps.statistic.KotlinBuilderReportService")
    private val loggerAdapter = JpsKotlinLogger(log)
    private val httpService = httpReportSettings?.let { HttpReportService(it.url, it.user, it.password) }

    fun moduleBuildStarted(chunk: ModuleChunk): JpsBuilderMetricReporter {
        val moduleName = chunk.name
        buildMetrics[moduleName]?.also {
            log.warn("Service already initialized for context")
            return it
        }
        log.info("JpsStatisticsReportService: Service started")
        val reporter = JpsBuilderMetricReporterImpl(chunk, BuildMetricsReporterImpl())
        buildMetrics[moduleName] = reporter
        return reporter
    }

    fun moduleBuildFinished(chunk: ModuleChunk, context: CompileContext) {
        val moduleName = chunk.name
        val metrics = buildMetrics.remove(moduleName)
        if (metrics == null) {
            log.warn("Service hasn't initialized for context")
            return
        }
        log.info("JpsStatisticsReportService: Service finished")
        metrics.buildFinish(chunk, context)
        finishedModuleBuildMetrics.add(metrics)
    }

    fun buildFinish(context: CompileContext) {
        val compileStatisticsData = finishedModuleBuildMetrics.map { it.flush(context) }
//            finishedModuleBuildMetrics.groupBy { it.getModuleName() }
//                .values.map {
//                    it.reduce { first, second ->
//                        first.join(second)
//                        first
//                    }.flush(context)
//                }

        httpService?.sendData(compileStatisticsData, loggerAdapter)
        fileReportSettings?.also {
            FileReportService.reportBuildStatInFile(
                it.buildReportDir, context.projectDescriptor.project.name, true, compileStatisticsData,
                BuildStartParameters(tasks = listOf(jpsBuildTaskName)), emptyList(), loggerAdapter
            )
        }
    }

    fun <T> reportMetrics(chunk: ModuleChunk, metric: JpsBuildTime, action: () -> T): T {
        val moduleName = chunk.name
        val metrics = buildMetrics[moduleName]
        if (metrics == null) {
            log.warn("Service hasn't initialized for context")
            return action.invoke()
        }
        log.info("JpsStatisticsReportService: report metrics")
        return metrics.measure(metric, action)
    }

    fun buildStarted(context: CompileContext) {
        loggerAdapter.info("Build started for $context")
    }


}



