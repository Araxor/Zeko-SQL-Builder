package io.zeko.db.sql.exceptions

fun throwDuplicate(err: Exception) {
    if (err.message!!.contains("duplicate", true)) {
        var column: String? = null
        var entry: String? = null

        //MySQL
        if (err.message!!.contains("Duplicate entry '")) {
            val rgxFindField = "\\'([^\\']+)\\'".toRegex()

            rgxFindField.findAll(err.message!!).forEach {
                if (entry == null) {
                    entry = it.groups[1]?.value
                } else {
                    column = it.groups[1]?.value
                }
            }
        }
        //Apache Ignite/Ansi
        else if (err.message!!.startsWith("Duplicate key during")) {
            val rgxFindField = "\\[([^\\[\\]]+)\\]".toRegex()
            val str = rgxFindField.find(err.message!!)?.groups?.get(1)?.value.orEmpty()
            val parts = str.removePrefix("[").removeSuffix("]").split("\\=".toRegex(), 2)
            column = if (parts[0] == "key") "PRIMARY" else parts[0]
            entry = parts[1]
        }
        //Postgres
        else if (err.message!!.contains("duplicate key value violates")) {
            val rgxFindField = "\\\"([^\\\"]+)\\\"".toRegex()
            rgxFindField.find(err.message!!)?.let {
                column = it.groups[1]?.value
            }

            val rgxFindEntry = "\\(([^\\)]+)\\) already exists".toRegex()
            rgxFindEntry.find(err.message!!)?.let {
                entry = it.groups[1]?.value
            }
        }

        throw DuplicateKeyException(column + "", entry + "", err.message)
    }
}
