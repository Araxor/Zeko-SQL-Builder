package io.zeko.model

import io.zeko.db.sql.utilities.toCamelCase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

abstract class Entity {
    protected var map: MutableMap<String, Any?>

    constructor(map: Map<String, Any?>) {
        val camelCaseMap = mutableMapOf<String, Any?>()
        val mappings = propTypeMapping()
        for ((k, v) in map) {
            if (v == null) continue
            val prop = k.toCamelCase()

            if (mappings != null) {
                camelCaseMap[prop] = mapPropValue(mappings, prop, v)
            } else {
                camelCaseMap[prop] = v
            }
        }
        this.map = camelCaseMap.withDefault { null }
    }

    constructor(vararg props: Pair<String, Any?>) {
        this.map = mutableMapOf(*props).withDefault { null }
    }

    open fun tableName(): String = ""

    open fun dataMap(): MutableMap<String, Any?> = map

    open fun propTypeMapping(): Map<String, Type>? = null

    open fun mapPropValue(mappings: Map<String, Type>, prop: String, value: Any): Any {
        if (mappings.isNotEmpty()) {
            if (!mappings.containsKey(prop)) return value
            val convertType = mappings[prop]
            if (convertType != null) {
                return convertValueToType(value, convertType)
            }
        }
        return value
    }

    open fun convertValueToType(value: Any, type: Type): Any {
        val converted = when (type) {
            //tiny(1) hikari returns booleam, jasync returns byte
            Type.BOOL -> when (value) {
                is Boolean -> value
                is Byte -> value.toInt() > 0
                else -> false
            }
            Type.INT -> when (value) {
                is Int -> value
                is Byte -> value.toInt()
                is Long -> value.toInt()
                else -> value
            }
            Type.LONG -> when (value) {
                is Long -> value
                is Int -> value.toLong()
                is Byte -> value.toLong()
                else -> value
            }
            Type.DOUBLE -> when (value) {
                is Double -> value
                is Float -> value.toDouble()
                is Int -> value.toDouble()
                is Long -> value.toDouble()
                is Byte -> value.toLong()
                else -> value
            }
            Type.FLOAT -> when (value) {
                is Float -> value
                is Double -> value.toDouble()
                is Int -> value.toDouble()
                is Long -> value.toDouble()
                is Byte -> value.toLong()
                else -> value
            }
            Type.DATETIME -> {
                if (value !is String) {
                    val dateStr = value.toString()
                    LocalDateTime.parse(dateStr)
                } else {
                    LocalDateTime.parse(value)
                }
            }
            Type.DATE -> {
                if (value is LocalDate) {
                    value
                } else {
                    LocalDate.parse(value.toString())
                }
            }
            else -> value
        }
        return converted
    }

    open fun toParams(valueHandler: ((String, Any?) -> Any?)? = null): List<Any?> {
        val entries = dataMap().entries
        val params = arrayListOf<Any?>()
        entries.forEach { prop ->
            if (valueHandler != null) {
                params.add(valueHandler(prop.key, prop.value))
            } else {
                when (prop.value) {
                    is Enum<*> -> params.add((prop.value as Enum<*>).name)
                    else -> params.add(prop.value)
                }
            }
        }
        return params
    }

    override fun toString(): String {
        var str = this.tableName() + " { "
        dataMap().entries.forEach {
            str += "${it.key}-> ${it.value}, "
        }
        return str.removeSuffix(", ") + " }"
    }
}
