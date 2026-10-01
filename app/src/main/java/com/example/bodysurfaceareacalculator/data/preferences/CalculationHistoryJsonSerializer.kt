package com.example.bodysurfaceareacalculator.data.preferences

import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.data.model.CalculationHistoryItem
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit

object CalculationHistoryJsonSerializer {

    fun serialize(items: List<CalculationHistoryItem>): String {
        val sb = StringBuilder()
        sb.append("[")
        items.forEachIndexed { index, item ->
            if (index > 0) sb.append(",")
            sb.append("{")
            sb.append("\"id\":").append(quote(item.id)).append(",")
            sb.append("\"timestamp\":").append(item.timestamp).append(",")
            sb.append("\"formattedDateTime\":").append(quote(item.formattedDateTime)).append(",")
            sb.append("\"heightDisplay\":").append(quote(item.heightDisplay)).append(",")
            sb.append("\"heightUnit\":").append(quote(item.heightUnit.name)).append(",")
            sb.append("\"weightDisplay\":").append(quote(item.weightDisplay)).append(",")
            sb.append("\"weightUnit\":").append(quote(item.weightUnit.name)).append(",")
            sb.append("\"heightCm\":").append(item.heightCm).append(",")
            sb.append("\"weightKg\":").append(item.weightKg).append(",")
            sb.append("\"formula\":").append(quote(item.formula.name)).append(",")
            sb.append("\"bsaM2\":").append(item.bsaM2).append(",")
            if (item.comparisonBsaM2 != null) {
                sb.append("\"comparisonBsaM2\":").append(item.comparisonBsaM2)
            } else {
                sb.append("\"comparisonBsaM2\":null")
            }
            sb.append("}")
        }
        sb.append("]")
        return sb.toString()
    }

    fun deserialize(json: String?): List<CalculationHistoryItem> {
        if (json.isNullOrBlank()) return emptyList()
        val trimmed = json.trim()
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) return emptyList()

        val content = trimmed.substring(1, trimmed.length - 1).trim()
        if (content.isEmpty()) return emptyList()

        val items = mutableListOf<CalculationHistoryItem>()
        val objectStrings = splitJsonObjects(content)

        for (objStr in objectStrings) {
            val map = parseJsonObject(objStr)
            val id = map["id"] ?: continue
            val timestamp = map["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
            val formattedDateTime = map["formattedDateTime"] ?: ""
            val heightDisplay = map["heightDisplay"] ?: ""
            val heightUnit = try {
                HeightUnit.valueOf(map["heightUnit"] ?: HeightUnit.CENTIMETERS.name)
            } catch (_: Exception) {
                HeightUnit.CENTIMETERS
            }
            val weightDisplay = map["weightDisplay"] ?: ""
            val weightUnit = try {
                WeightUnit.valueOf(map["weightUnit"] ?: WeightUnit.KILOGRAMS.name)
            } catch (_: Exception) {
                WeightUnit.KILOGRAMS
            }
            val heightCm = map["heightCm"]?.toDoubleOrNull() ?: 0.0
            val weightKg = map["weightKg"]?.toDoubleOrNull() ?: 0.0
            val formula = try {
                BsaFormula.valueOf(map["formula"] ?: BsaFormula.MOSTELLER.name)
            } catch (_: Exception) {
                BsaFormula.MOSTELLER
            }
            val bsaM2 = map["bsaM2"]?.toDoubleOrNull() ?: 0.0
            val comparisonBsaM2 = map["comparisonBsaM2"]?.let {
                if (it == "null") null else it.toDoubleOrNull()
            }

            items.add(
                CalculationHistoryItem(
                    id = id,
                    timestamp = timestamp,
                    formattedDateTime = formattedDateTime,
                    heightDisplay = heightDisplay,
                    heightUnit = heightUnit,
                    weightDisplay = weightDisplay,
                    weightUnit = weightUnit,
                    heightCm = heightCm,
                    weightKg = weightKg,
                    formula = formula,
                    bsaM2 = bsaM2,
                    comparisonBsaM2 = comparisonBsaM2
                )
            )
        }

        return items
    }

    private fun quote(value: String): String {
        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
        return "\"$escaped\""
    }

    private fun splitJsonObjects(content: String): List<String> {
        val objects = mutableListOf<String>()
        var depth = 0
        var inQuotes = false
        var isEscaped = false
        var startIdx = -1

        for (i in content.indices) {
            val c = content[i]
            if (isEscaped) {
                isEscaped = false
                continue
            }
            if (c == '\\') {
                isEscaped = true
                continue
            }
            if (c == '"') {
                inQuotes = !inQuotes
                continue
            }
            if (!inQuotes) {
                if (c == '{') {
                    if (depth == 0) startIdx = i
                    depth++
                } else if (c == '}') {
                    depth--
                    if (depth == 0 && startIdx != -1) {
                        objects.add(content.substring(startIdx, i + 1))
                        startIdx = -1
                    }
                }
            }
        }
        return objects
    }

    private fun parseJsonObject(objStr: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val trimmed = objStr.trim()
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) return map
        val inner = trimmed.substring(1, trimmed.length - 1)

        var i = 0
        while (i < inner.length) {
            // Find key start
            while (i < inner.length && inner[i] != '"') i++
            if (i >= inner.length) break
            i++ // skip opening quote
            val keyStart = i
            while (i < inner.length && inner[i] != '"') {
                if (inner[i] == '\\') i++
                i++
            }
            if (i >= inner.length) break
            val key = unescape(inner.substring(keyStart, i))
            i++ // skip closing quote

            // Find colon
            while (i < inner.length && inner[i] != ':') i++
            if (i >= inner.length) break
            i++ // skip colon

            // Find value start
            while (i < inner.length && inner[i].isWhitespace()) i++
            if (i >= inner.length) break

            if (inner[i] == '"') {
                i++ // skip opening quote
                val valStart = i
                while (i < inner.length && inner[i] != '"') {
                    if (inner[i] == '\\') i++
                    i++
                }
                val value = if (i <= inner.length) unescape(inner.substring(valStart, i)) else ""
                map[key] = value
                if (i < inner.length) i++ // skip closing quote
            } else {
                val valStart = i
                while (i < inner.length && inner[i] != ',' && inner[i] != '}') i++
                val value = inner.substring(valStart, i).trim()
                map[key] = value
            }

            // Skip to comma
            while (i < inner.length && inner[i] != ',') i++
            if (i < inner.length && inner[i] == ',') i++
        }

        return map
    }

    private fun unescape(value: String): String {
        return value
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
    }
}
