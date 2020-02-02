package org.kountersign.core.ext

/**
 * Generates sequence of random elements in [this] collection. Collection must have at least one element, otherwise
 * a [IllegalStateException] will be thrown.
 */
fun <T : Any> Collection<T>.randomSequence() = generateSequence {
    if (this.isEmpty()) {
        val message = "Cannot call randomSequence() on empty list"
        throw IllegalStateException(message)
    }

    this.random()
}

