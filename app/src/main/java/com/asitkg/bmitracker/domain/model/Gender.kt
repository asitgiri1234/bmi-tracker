package com.asitkg.bmitracker.domain.model

enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    ;

    companion object {
        /** Tolerant lookup for persisted values; unknown input falls back to [OTHER]. */
        fun fromName(value: String?): Gender =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
    }
}
