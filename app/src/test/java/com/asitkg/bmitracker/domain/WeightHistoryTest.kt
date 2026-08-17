package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class WeightHistoryTest {

    private val zone = ZoneId.of("UTC")
    private val today = LocalDate.of(2026, 8, 17)

    private fun entry(date: LocalDate, hour: Int, weightKg: Double) = WeightEntry(
        profileId = 1L,
        weightKg = weightKg,
        recordedAt = date.atTime(LocalTime.of(hour, 0)).atZone(zone).toInstant(),
    )

    @Test
    fun `empty input produces an empty series`() {
        assertTrue(WeightHistory.dailySeries(emptyList(), today = today, zone = zone).isEmpty())
    }

    @Test
    fun `keeps one point per day`() {
        val entries = listOf(
            entry(today, 8, 70.0),
            entry(today, 20, 71.0),
        )
        val series = WeightHistory.dailySeries(entries, today = today, zone = zone)
        assertEquals(1, series.size)
    }

    @Test
    fun `the last reading of a day wins`() {
        val entries = listOf(
            entry(today, 8, 70.0),
            entry(today, 20, 71.5),
            entry(today, 12, 70.8),
        )
        val series = WeightHistory.dailySeries(entries, today = today, zone = zone)
        assertEquals(71.5, series.single().weightKg, 0.001)
    }

    @Test
    fun `excludes entries older than the window`() {
        val entries = listOf(
            entry(today.minusDays(10), 8, 80.0),
            entry(today.minusDays(2), 8, 70.0),
        )
        val series = WeightHistory.dailySeries(entries, days = 7, today = today, zone = zone)
        assertEquals(1, series.size)
        assertEquals(70.0, series.single().weightKg, 0.001)
    }

    @Test
    fun `includes the oldest day inside the window`() {
        // A 7-day window counts back from today inclusive, so today-6 is in.
        val entries = listOf(entry(today.minusDays(6), 8, 68.0))
        val series = WeightHistory.dailySeries(entries, days = 7, today = today, zone = zone)
        assertEquals(1, series.size)
    }

    @Test
    fun `excludes the day just outside the window`() {
        val entries = listOf(entry(today.minusDays(7), 8, 68.0))
        val series = WeightHistory.dailySeries(entries, days = 7, today = today, zone = zone)
        assertTrue(series.isEmpty())
    }

    @Test
    fun `excludes future entries`() {
        val entries = listOf(entry(today.plusDays(1), 8, 68.0))
        val series = WeightHistory.dailySeries(entries, today = today, zone = zone)
        assertTrue(series.isEmpty())
    }

    @Test
    fun `returns points in ascending date order`() {
        val entries = listOf(
            entry(today, 8, 70.0),
            entry(today.minusDays(3), 8, 72.0),
            entry(today.minusDays(1), 8, 71.0),
        )
        val series = WeightHistory.dailySeries(entries, today = today, zone = zone)
        assertEquals(listOf(today.minusDays(3), today.minusDays(1), today), series.map { it.date })
    }

    @Test
    fun `omits days without data rather than zero-filling`() {
        // Zero-filling would plot a spike to the chart floor.
        val entries = listOf(
            entry(today.minusDays(4), 8, 72.0),
            entry(today, 8, 70.0),
        )
        val series = WeightHistory.dailySeries(entries, today = today, zone = zone)
        assertEquals(2, series.size)
        assertTrue(series.none { it.weightKg == 0.0 })
    }

    @Test
    fun `a non-positive window is empty`() {
        val entries = listOf(entry(today, 8, 70.0))
        assertTrue(WeightHistory.dailySeries(entries, days = 0, today = today, zone = zone).isEmpty())
    }
}
