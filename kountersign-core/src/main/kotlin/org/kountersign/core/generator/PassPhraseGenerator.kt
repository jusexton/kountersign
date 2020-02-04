package org.kountersign.core.generator

import org.kountersign.core.ext.randomSequence

/**
 * Password generator at a string level. Generates password based on given words and a specific word count.
 * Also capable of injecting digits around words based on a given [digitPlacementStrategy].
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
class RandomPassPhraseGenerator(
    private val words: Set<String>,
    private val wordCount: Int,
    private val capitalizeWords: Boolean = false,
    private val digitPlacementStrategy: DigitPlacementStrategy? = null
) : PassPhraseGenerator {
    init {
        require(words.isNotEmpty()) { "At least one word is required" }
        require(wordCount > 0) { "'wordCount' should be a positive integer" }
    }

    override fun generate(): String {
        val wordSequence = when (capitalizeWords) {
            true -> generateWordSequence().take(wordCount).map { it.capitalize() }
            false -> generateWordSequence().take(wordCount)
        }

        return digitPlacementStrategy?.let {
            PassPhraseDigitInjector(it).inject(wordSequence)
        } ?: wordSequence.joinToString("")
    }

    private fun generateWordSequence() = words.randomSequence()
}

/**
 * Produces a string from given sequence and [DigitPlacementStrategy].
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
interface DigitInjector {
    val strategy: DigitPlacementStrategy

    /**
     * Produces [String] with injected digits from given [sequence]
     */
    fun inject(sequence: Sequence<String>): String
}

/**
 * Base implementation of the [DigitInjector].
 *
 * Capable of producing strings from a given [Sequence] and [DigitPlacementStrategy] with digits
 * injected into the correct locations.
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
class PassPhraseDigitInjector(override val strategy: DigitPlacementStrategy) : DigitInjector {
    override fun inject(sequence: Sequence<String>): String = with(strategy) {
        val digitGenerator = strategy.asPasswordGenerator()

        when (pattern) {
            DigitPlacementPattern.BEGIN -> sequence.joinToString(
                separator = "",
                prefix = digitGenerator.generate()
            )

            DigitPlacementPattern.END -> sequence.joinToString(
                separator = "",
                postfix = digitGenerator.generate()
            )

            DigitPlacementPattern.BEGIN_AND_END -> sequence.joinToString(
                separator = "",
                postfix = digitGenerator.generate(),
                prefix = digitGenerator.generate()
            )

            DigitPlacementPattern.BETWEEN_WORDS -> sequence.joinToString(
                separator = digitGenerator
            )

            DigitPlacementPattern.WRAP_AROUND_WORDS -> sequence.joinToString(
                separator = digitGenerator,
                prefix = digitGenerator.generate(),
                postfix = digitGenerator.generate()
            )
        }
    }

    private fun Sequence<String>.joinToString(
        separator: PasswordGenerator,
        prefix: CharSequence = "",
        postfix: CharSequence = ""
    ): String {
        val buffer = StringBuilder()
        buffer.append(prefix)

        var count = 0
        for (element in this) {
            if (++count > 1) buffer.append(separator.generate())
            buffer.append(element)
        }

        buffer.append(postfix)
        return buffer.toString()
    }
}

/**
 * Options detailing how and what digits should be injected into a sequence when using a [DigitInjector]
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
data class DigitPlacementStrategy(
    val digits: Set<Int> = setOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
    val digitCount: Int = 3,
    val unique: Boolean = false,
    val pattern: DigitPlacementPattern = DigitPlacementPattern.END
) {
    init {
        require(digits.isNotEmpty()) { "At least one digit is required" }
        require(digitCount > 0) { "'digitCount' should be a positive integer" }
        require(digits.all { it <= 9 || it >= 0 }) { "Supplied integers must be single digit positive or zero values" }
    }
}

/**
 * Creates password generator instance from [this] digit placement strategy instance
 */
fun DigitPlacementStrategy.asPasswordGenerator(): CharacterPasswordGenerator {
    // Adding 48 to give the integer ascii value.
    // This value can then be correctly converted to a character representing that single digit value
    val characterSet = digits.map { (it + 48).toChar() }.toSet()

    return when (unique) {
        true -> RandomCharacterPasswordGenerator(characterSet, digitCount)
        false -> OneTimeRandomCharacterPasswordGenerator(characterSet, digitCount)
    }
}

/**
 * Represents patterns that a [DigitInjector] can inject a sequence with.
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
enum class DigitPlacementPattern {
    /**
     * Prefixes generated pass phrase with generated digits
     *
     * ex. 123PassPhrase
     */
    BEGIN,

    /**
     * Postfixes generated pass phrase with generated digits
     *
     * ex. PassPhrase123
     */
    END,

    /**
     * Prefixes and postfixes generated pass phrase with generated digits
     *
     * ex. 123PassPhrase123
     */
    BEGIN_AND_END,

    /**
     * Injects generated digits between pass phrase words
     *
     * ex. Pass123Phrase
     */
    BETWEEN_WORDS,

    /**
     * Wraps generated pass phrase words with generated digits
     *
     * ex. 123Pass123Phrase123
     */
    WRAP_AROUND_WORDS
}
