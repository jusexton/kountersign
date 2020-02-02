package org.kountersign.core.generator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.assertTimeout
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.junit.platform.commons.logging.LoggerFactory
import org.kountersign.core.PerformanceTest
import org.kountersign.core.text.ALL
import java.time.Duration

class CharacterGeneratorTest {
    private val logger = LoggerFactory.getLogger(CharacterGeneratorTest::class.java)

    @Test
    fun `should correctly generate password based on given characters and length`() {
        val testLength = 5

        val generator = RandomCharacterPasswordGenerator(ALL, testLength)
        val password = generator.generate()
        logger.info { "Generated password: $password" }

        assertEquals(testLength, password.length)

        // Tests that the generated password is only made up of characters the generator was configured with.
        assertTrue(password.all { ALL.contains(it) })
    }

    @Nested
    inner class Validation {
        @Test
        fun `should throw illegal argument exception when given empty list`() {
            val instance = assertThrows<IllegalArgumentException> {
                RandomCharacterPasswordGenerator(emptySet(), 5)
            }

            val expectedMessage = "At least one character is required"
            assertEquals(expectedMessage, instance.message)
        }

        @Test
        fun `should throw illegal argument exception when length is zero`() {
            val instance = assertThrows<IllegalArgumentException> {
                RandomCharacterPasswordGenerator(setOf('a'), 0)
            }

            val expectedMessage = "'length' should be a positive integer"
            assertEquals(expectedMessage, instance.message)
        }

        @ParameterizedTest
        @ValueSource(
            ints = [-1, -5, Int.MIN_VALUE]
        )
        fun `should throw illegal argument exception when length is negative`(value: Int) {
            val instance = assertThrows<IllegalArgumentException> {
                RandomCharacterPasswordGenerator(setOf('a'), value)
            }

            val expectedMessage = "'length' should be a positive integer"
            assertEquals(expectedMessage, instance.message)
        }
    }

    @Nested
    @PerformanceTest
    inner class Performance {
        @Test
        fun `should maintain reasonable performance when generating many passwords at once`() {
            val passwordSequence = assertTimeout(Duration.ofMillis(500)) {
                val generator = RandomCharacterPasswordGenerator(ALL, 12)
                generator.generateSequence().take(100000)
            }

            logger.info { "Generated passwords: ${passwordSequence.take(100).joinToString()}, ..." }
        }

        @Test
        fun `should maintain reasonable performance even when generating password with length 100000`() {
            val generator = RandomCharacterPasswordGenerator(ALL, 100000)

            assertTimeout(Duration.ofMillis(175)) {
                generator.generate()
            }
        }
    }
}

