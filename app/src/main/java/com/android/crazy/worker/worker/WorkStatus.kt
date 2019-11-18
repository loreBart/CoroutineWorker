package com.android.crazy.worker.worker


/**
 * Enum used to wrap the internal [kotlinx.coroutines.Job] state
 */
enum class WorkStatus {
    UNKNOWN,
    ACTIVE,
    CANCELLED,
    COMPLETED;

    override fun toString(): String = when (this) {
        UNKNOWN   -> "UNKNOWN"
        ACTIVE    -> "ACTIVE"
        CANCELLED -> "CANCELLED"
        COMPLETED -> "COMPLETED"
    }

}

