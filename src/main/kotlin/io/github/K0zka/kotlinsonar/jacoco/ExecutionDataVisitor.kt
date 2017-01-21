package io.github.K0zka.kotlinsonar.jacoco

import org.jacoco.core.data.*

class ExecutionDataVisitor : ISessionInfoVisitor, IExecutionDataVisitor {

    private val sessions = mutableMapOf<String, ExecutionDataStore>()

    private var executionDataStore: ExecutionDataStore? = null
    val merged = ExecutionDataStore()

    override fun visitSessionInfo(info: SessionInfo) {
        val sessionId = info.id
        if (sessions.get(sessionId) == null) {
            sessions.put(sessionId, ExecutionDataStore())
            executionDataStore = sessions.get(sessionId)
        }
    }

    override fun visitClassExecution(data: ExecutionData) {
        executionDataStore!!.put(data)
        merged.put(defensiveCopy(data))
    }

    fun getSessions(): Map<String, ExecutionDataStore> {
        return sessions
    }

    private fun defensiveCopy(data: ExecutionData): ExecutionData {
        val src = data.probes
        val dest = BooleanArray(src.size)
        System.arraycopy(src, 0, dest, 0, src.size)
        return ExecutionData(data.id, data.name, dest)
    }

}
