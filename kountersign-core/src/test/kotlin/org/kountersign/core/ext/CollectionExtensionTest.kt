package org.kountersign.core.ext

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeout
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.platform.commons.logging.LoggerFactory
import org.kountersign.core.PerformanceTest
import java.time.Duration
import java.util.stream.Stream
import kotlin.random.Random

class RandomSequenceTest {
    @Test
    fun `should generate only correct number of elements asked for`() {
        val size = 3
        val sequence = listOf(1, 2, 3).randomSequence().take(size)
        assertEquals(size, sequence.count())
    }

    @Nested
    inner class Validation {
        @Test
        fun `should throw illegal state exception when called on empty collection`() {
            val instance = assertThrows<IllegalStateException> {
                // Make sure to call a function so that work is actually performed.
                // Sequences are lazy and if no work function is called, the sequence generator is never called.
                emptyList<Int>().randomSequence().take(1).sum()
            }

            val expectedMessage = "Cannot call randomSequence() on empty list"
            assertEquals(expectedMessage, instance.message)
        }
    }

    @Nested
    @PerformanceTest
    inner class Performance {
        private val logger = LoggerFactory.getLogger(Performance::class.java)

        @ParameterizedTest
        @ArgumentsSource(PerformanceArgumentProvider::class)
        fun `should generate random sequence in reasonable time`(take: Int, millis: Long) {
            logger.info {
                """
                    Random sequence consisted of '$take' elements
                    Maximum millis that were allowed: '${millis}ms'
                """.trimIndent()
            }

            assertTimeout(Duration.ofMillis(millis)) {
                listOf(1, 2, 3).randomSequence().take(take).sum()
            }
        }

        @Test
        fun `should maintain reasonable performance when called on large collection`() {
            val collection = generateSequence { Random.nextInt(1, 100) }.take(1000000).toList()

            assertTimeout(Duration.ofMillis(10)) {
                collection.randomSequence().take(10).sum()
            }
        }
    }

    /**
     * Argument provider for supplying expensive computational data and performance expectations
     */
    class PerformanceArgumentProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            // Take, Millis
            Arguments.of(10000, 20L),
            Arguments.of(100000, 100L),
            Arguments.of(10000000, 1000L)
        )
    }
}
