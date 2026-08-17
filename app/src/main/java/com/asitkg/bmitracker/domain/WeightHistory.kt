package com.asitkg.bmitracker.domain

import com.asitkg.bmitracker.domain.model.WeightEntry
import java.time.LocalDate
import java.time.ZoneId

/**
 * Turns raw measurements into the daily series the chart plots.
 *
 * Kept out of the UI so the bucketing rules are unit-testable — the awkward
 * cases (several weigh-ins in a day, gaps, entries older than the window) are
 * exactly the ones a chart silently renders wrong.
 */
object WeightHistory {

    data class DailyPoint(val date: LocalDate, val weightKg: Double)

    /**
     * One point per day that has data, oldest first.
     *
     * Days without a measurement are omitted rather than zero-filled, since a
     * zero would plot as a spike to the floor of the chart. The last reading of
     * a day wins, matching "current weight is the most recent entry".
     *
     * @param days window size, counting back from and including [today].
     */
    fun dailySeries(
        entries: List<WeightEntry>,
        days: Int = 7,
        today: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault(),
    ): List<DailyPoint> {
        if (days <= 0) return emptyList()
        val earliest = today.minusDays(days - 1L)

        return entries
            .asSequence()
            .map { entry -> entry to entry.recordedAt.atZone(zone).toLocalDate() }
            .filter { (_, date) -> !date.isBefore(earliest) && !date.isAfter(today) }
            .groupBy { (_, date) -> date }
            .map { (date, sameDay) ->
                val latest = sameDay.maxBy { (entry, _) -> entry.recordedAt }
                DailyPoint(date = date, weightKg = latest.first.weightKg)
            }
            .sortedBy { it.date }
    }
}
