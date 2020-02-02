package org.kountersign.core.generator

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.provider.ValueSource
import org.junit.platform.commons.logging.LoggerFactory
import org.kountersign.core.PerformanceTest
import java.net.URL
import java.time.Duration
import java.util.stream.Stream

class PassPhraseGeneratorTest {
    private val logger = LoggerFactory.getLogger(PassPhraseGeneratorTest::class.java)

    @Nested
    inner class Capitalization {
        private val words = setOf("baseball", "dog")

        @Test
        fun `should correctly capitalize all words when specified`() {
            val generator = RandomPassPhraseGenerator(words, 3, capitalizeWords = true)
            val password = generator.generate()

            logger.info { "Generated Password: $password" }

            val result = password.contains('B') or password.contains('D')
            assertTrue(result)
        }

        @Test
        fun `should correctly not capitalize words when specified`() {
            val generator = RandomPassPhraseGenerator(words, 3, capitalizeWords = false)
            val password = generator.generate()

            logger.info { "Generated Password: $password" }

            val result = !password.contains('B') and !password.contains('D')
            assertTrue(result)
        }
    }

    @Nested
    inner class DigitPlacement {
        private val words = setOf("test", "words")

        @ParameterizedTest
        @ArgumentsSource(DigitPlacementArguments::class)
        fun `should build phrase correctly based on given digit placement strategy`(
            pattern: String,
            wordCount: Int,
            strategy: DigitPlacementStrategy
        ) {
            val password = RandomPassPhraseGenerator(
                words,
                wordCount = wordCount,
                digitPlacementStrategy = strategy
            ).generate()

            logger.info { "Generated Password: $password" }

            val regex = Regex(pattern)
            assertTrue(password.matches(regex))
        }
    }

    @Nested
    inner class Validation {
        @Test
        fun `should throw illegal argument exception when given empty list`() {
            val instance = assertThrows<IllegalArgumentException> {
                RandomPassPhraseGenerator(emptySet(), 5)
            }

            val expectedMessage = "At least one word is required"
            Assertions.assertEquals(expectedMessage, instance.message)
        }

        @Test
        fun `should throw illegal argument exception when length is zero`() {
            val instance = assertThrows<IllegalArgumentException> {
                RandomPassPhraseGenerator(setOf("a"), 0)
            }

            val expectedMessage = "'wordCount' should be a positive integer"
            Assertions.assertEquals(expectedMessage, instance.message)
        }

        @ParameterizedTest
        @ValueSource(
            ints = [-1, -5, Int.MIN_VALUE]
        )
        fun `should throw illegal argument exception when length is negative`(value: Int) {
            val instance = assertThrows<IllegalArgumentException> {
                RandomPassPhraseGenerator(setOf("a"), value)
            }

            val expectedMessage = "'wordCount' should be a positive integer"
            Assertions.assertEquals(expectedMessage, instance.message)
        }
    }

    @Nested
    @PerformanceTest
    inner class Performance {
        private val logger = LoggerFactory.getLogger(Performance::class.java)

        private val words =
            URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
                .readText()
                .split("\n")
                .toSet()

        @Test
        fun `should maintain reasonable performance when generating many passwords at once`() {
            assertTimeout(Duration.ofMillis(500)) {
                val generator = RandomPassPhraseGenerator(words, 3, capitalizeWords = true)
                val passwords = generator.generateSequence().take(100)

                logger.info { "Generated passwords: ${passwords.joinToString()}" }
            }
        }

        @Test
        fun `should maintain reasonable performance when given high volume of words`() {
            assertTimeout(Duration.ofMillis(75)) {
                val generator = RandomPassPhraseGenerator(words, 3, capitalizeWords = true)
                val password = generator.generate()

                logger.info { "Generated password: $password" }
            }
        }

        @Test
        fun `should maintain reasonable performance when injecting digits between many words`() {
            val password = assertTimeout(Duration.ofMillis(750)) {
                val strategy = DigitPlacementStrategy(pattern = DigitPlacementPattern.WRAP_AROUND_WORDS, unique = false)

                // Extremely computationally expensive operation.
                // 500ms is somewhat reasonable when reaching into the hundreds
                // Seems to take roughly 1ms per word
                val generator =
                    RandomPassPhraseGenerator(words, 350, capitalizeWords = true, digitPlacementStrategy = strategy)
                generator.generate()
            }

            logger.info { "Generated password: $password" }
        }
    }

    private class DigitPlacementArguments : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
            // Expected pattern, Word count, Digit placement strategy
            Arguments.of(
                "\\d{3}\\w+",
                2,
                DigitPlacementStrategy(pattern = DigitPlacementPattern.BEGIN, digitCount = 3)
            ),
            Arguments.of(
                "\\w+\\d{3}",
                2,
                DigitPlacementStrategy(pattern = DigitPlacementPattern.END, digitCount = 3)
            ),
            Arguments.of(
                "\\d{3}\\w+\\d{3}",
                2,
                DigitPlacementStrategy(pattern = DigitPlacementPattern.BEGIN_AND_END, digitCount = 3)
            ),
            Arguments.of(
                "\\w+\\d{3}\\w+",
                2,
                DigitPlacementStrategy(pattern = DigitPlacementPattern.BETWEEN_WORDS, digitCount = 3)
            ),
            Arguments.of(
                "\\d{3}\\w+\\d{3}\\w+\\d{3}",
                2,
                DigitPlacementStrategy(pattern = DigitPlacementPattern.WRAP_AROUND_WORDS, digitCount = 3)
            )
        )
    }
}
