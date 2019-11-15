package com.android.crazy.worker.worker

import java.util.*

/**
 * A class used to identify a work
 */
data class WorkId(private val _id: UUID = UUID.randomUUID()) {
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
