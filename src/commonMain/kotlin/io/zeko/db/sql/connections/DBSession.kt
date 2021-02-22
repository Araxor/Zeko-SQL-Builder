package io.zeko.db.sql.connections

interface DBSession {
    fun pool(): DBPool
    fun connection(): DBConn
    fun rawConnection(): Any
    fun setQueryLogger(logger: DBLogger): DBSession
    fun getQueryLogger(): DBLogger?

    suspend fun <A> once(operation: suspend (DBSession) -> A): A
    suspend fun <A> retry(numRetries: Int, delayTry: Long = 0, operation: suspend (DBSession) -> A)
    suspend fun <A> transaction(operation: suspend (DBSession) -> A): A
    suspend fun <A> transactionOpen(operation: suspend (DBSession) -> A): A
    suspend fun <A> transaction(numRetries: Int, delayTry: Long = 0, operation: suspend (DBSession) -> A)
    suspend fun close()
    suspend fun insert(sql: String, params: List<Any?>, closeStatement: Boolean = true, closeConn: Boolean = false): List<*>
    suspend fun update(sql: String, params: List<Any?>, closeStatement: Boolean = true, closeConn: Boolean = false): Int
    suspend fun queryPrepared(sql: String, params: List<Any?>, dataClassHandler: (dataMap: Map<String, Any?>) -> Any, closeStatement: Boolean = true, closeConn: Boolean = false): List<*>
    suspend fun queryPrepared(sql: String, params: List<Any?>): Any
    suspend fun queryPrepared(sql: String, params: List<Any?>, columns: List<String>, closeConn: Boolean = false): List<LinkedHashMap<String, Any?>>
    suspend fun query(sql: String, dataClassHandler: (dataMap: Map<String, Any?>) -> Any, closeStatement: Boolean = true, closeConn: Boolean = false): List<*>
    suspend fun query(sql: String): Any
    suspend fun query(sql: String, columns: List<String>, closeConn: Boolean = false): List<LinkedHashMap<String, Any?>>
}
