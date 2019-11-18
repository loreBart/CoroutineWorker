package com.android.crazy.worker.worker

import java.util.*

/**
 * A class used for @see Work
 */
data class WorkId(private val _id: UUID = UUID.randomUUID()) {
    companion object {
        val NULL = WorkId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
    }

    /**
     * Returns the job identifier
     */
    fun id() : String = _id.toString()

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other is WorkId) {
            return _id == other._id
        }
        return false
    }


    override fun hashCode(): Int = _id.hashCode()
    override fun toString(): String = _id.toString()

}
