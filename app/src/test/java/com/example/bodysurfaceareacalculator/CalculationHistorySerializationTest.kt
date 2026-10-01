package com.example.bodysurfaceareacalculator

import com.example.bodysurfaceareacalculator.data.model.BsaFormula
import com.example.bodysurfaceareacalculator.data.model.CalculationHistoryItem
import com.example.bodysurfaceareacalculator.data.model.HeightUnit
import com.example.bodysurfaceareacalculator.data.model.WeightUnit
import com.example.bodysurfaceareacalculator.data.preferences.CalculationHistoryJsonSerializer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculationHistorySerializationTest {

    // 16. History serialization
    @Test
    fun testHistorySerializationRoundTrip() {
        val originalItems = listOf(
            CalculationHistoryItem(
                id = "item-1",
                timestamp = 1690000000000L,
                formattedDateTime = "Oct 1, 2026 · 10:30 AM",
                heightDisplay = "170 cm",
                heightUnit = HeightUnit.CENTIMETERS,
                weightDisplay = "70 kg",
                weightUnit = WeightUnit.KILOGRAMS,
                heightCm = 170.0,
                weightKg = 70.0,
                formula = BsaFormula.MOSTELLER,
                bsaM2 = 1.82,
                comparisonBsaM2 = 1.80
            ),
            CalculationHistoryItem(
                id = "item-2",
                timestamp = 1690000001000L,
                formattedDateTime = "Oct 1, 2026 · 11:15 AM",
                heightDisplay = "5 ft 8 in",
                heightUnit = HeightUnit.FEET_INCHES,
                weightDisplay = "160 lb",
                weightUnit = WeightUnit.POUNDS,
                heightCm = 172.72,
                weightKg = 72.57,
                formula = BsaFormula.DU_BOIS,
                bsaM2 = 1.85,
                comparisonBsaM2 = null
            )
        )

        val json = CalculationHistoryJsonSerializer.serialize(originalItems)
        assertTrue("JSON should contain array brackets", json.startsWith("[") && json.endsWith("]"))

        val deserialized = CalculationHistoryJsonSerializer.deserialize(json)
        assertEquals(2, deserialized.size)

        val first = deserialized[0]
        assertEquals("item-1", first.id)
        assertEquals("Oct 1, 2026 · 10:30 AM", first.formattedDateTime)
        assertEquals("170 cm", first.heightDisplay)
        assertEquals(HeightUnit.CENTIMETERS, first.heightUnit)
        assertEquals("70 kg", first.weightDisplay)
        assertEquals(WeightUnit.KILOGRAMS, first.weightUnit)
        assertEquals(170.0, first.heightCm, 0.001)
        assertEquals(70.0, first.weightKg, 0.001)
        assertEquals(BsaFormula.MOSTELLER, first.formula)
        assertEquals(1.82, first.bsaM2, 0.001)
        assertEquals(1.80, first.comparisonBsaM2!!, 0.001)

        val second = deserialized[1]
        assertEquals("item-2", second.id)
        assertEquals("5 ft 8 in", second.heightDisplay)
        assertEquals(BsaFormula.DU_BOIS, second.formula)
        assertEquals(1.85, second.bsaM2, 0.001)
        assertNull(second.comparisonBsaM2)
    }

    @Test
    fun testEmptyAndMalformedJson() {
        val emptyList = CalculationHistoryJsonSerializer.deserialize("")
        assertTrue(emptyList.isEmpty())

        val nullList = CalculationHistoryJsonSerializer.deserialize(null)
        assertTrue(nullList.isEmpty())

        val invalidList = CalculationHistoryJsonSerializer.deserialize("invalid json content")
        assertTrue(invalidList.isEmpty())
    }

    @Test
    fun testCapacityFiftyRecords() {
        val items = (1..60).map { i ->
            CalculationHistoryItem(
                id = "id-$i",
                timestamp = i.toLong(),
                formattedDateTime = "Date $i",
                heightDisplay = "$i cm",
                heightUnit = HeightUnit.CENTIMETERS,
                weightDisplay = "$i kg",
                weightUnit = WeightUnit.KILOGRAMS,
                heightCm = i.toDouble(),
                weightKg = i.toDouble(),
                formula = BsaFormula.MOSTELLER,
                bsaM2 = 1.8,
                comparisonBsaM2 = null
            )
        }

        // Taking top 50
        val trimmed = items.take(50)
        val json = CalculationHistoryJsonSerializer.serialize(trimmed)
        val deserialized = CalculationHistoryJsonSerializer.deserialize(json)
        assertEquals(50, deserialized.size)
        assertEquals("id-1", deserialized.first().id)
        assertEquals("id-50", deserialized.last().id)
    }
}
