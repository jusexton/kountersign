package org.kountersign.core.generator

import org.kountersign.core.ext.randomSequence
import org.kountersign.core.text.ALL

/**
 * @author Justin Sexton
 * @since 0.1.0
 */
interface CharacterPasswordGenerator : PasswordSequenceGenerator

/**
 * Password generator at a character level. Generates password based on given characters and a specific length.
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
open class RandomCharacterPasswordGenerator(
    private val characters: Set<Char> = ALL,
    private val length: Int = 10
) : CharacterPasswordGenerator {
    init {
        require(characters.isNotEmpty()) { "At least one character is required" }
        require(length > 0) { "'length' should be a positive integer" }
    }

    override fun generate() = generateCharacterSequence().take(length).joinToString("")

    private fun generateCharacterSequence() = characters.randomSequence()
}

/**
 * Password generator responsible for generating a random password once, then supplying nothing but that password
 * after every call to [generate].
 *
 * @see DigitPlacementStrategy
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
class OneTimeRandomCharacterPasswordGenerator(
    characters: Set<Char> = ALL,
    length: Int = 10
) : RandomCharacterPasswordGenerator(characters, length) {
    private val cache: String = super.generate()

    override fun generate(): String = cache
}
