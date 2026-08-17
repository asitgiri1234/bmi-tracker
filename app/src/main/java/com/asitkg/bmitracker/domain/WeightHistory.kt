package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.WeightEntry
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Turns raw measurements into the series the chart plots.
 *
 * Kept out of the UI so the window rules are unit-testable — the awkward cases
 * (entries older than the window, future-dated rows, ordering) are exactly the
 * ones a chart silently renders wrong.
 */
object WeightHistory {

    data class Point(val recordedAt: Instant, val date: LocalDate, val weightKg: Double)

    /**
     * Every measurement inside the window, oldest first.
     *
     * Each entry is plotted rather than one per day. Collapsing to a daily
     * value hides same-day progress and, more practically, means a chart can
     * never show a line until the app has been used across two calendar days.
     *
     * Days without a measurement are simply absent rather than zero-filled,
     * since a zero would plot as a spike to the floor of the chart.
     *
     * @param days window size, counting back from and including [today].
     */
    fun seriesFor(
        entries: List<WeightEntry>,
        days: Int = 7,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): List<Point> {
        if (days <= 0) return emptyList()
        val earliest = today.minusDays(days - 1L)

        return entries
            .asSequence()
            .map { entry -> Point(entry.recordedAt, entry.recordedAt.atZone(zone).toLocalDate(), entry.weightKg) }
            .filter { point -> !point.date.isBefore(earliest) && !point.date.isAfter(today) }
            .sortedBy { it.recordedAt }
            .toList()
    }
}
