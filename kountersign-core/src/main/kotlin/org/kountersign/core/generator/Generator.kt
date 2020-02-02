package org.kountersign.core.generator

/**
 * Implementations simply must produce a sequence of some item(s)
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
interface SequenceGenerator<T> {
    fun generateSequence(): Sequence<T>
}

/**
 * Generates a password in the form of a string.
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
interface PasswordGenerator {
    fun generate(): String
}

/**
 * Generates a password in the form of a string and is able to produce sequences of generated passwords.
 *
 * @author Justin Sexton
 * @since 0.1.0
 */
interface PasswordSequenceGenerator : PasswordGenerator, SequenceGenerator<String> {
    override fun generateSequence(): Sequence<String> = generateSequence { generate() }
}

/**
 * @author Justin Sexton
 * @since 0.1.0
 */
interface CharacterPasswordGenerator : PasswordSequenceGenerator

/**
 * @author Justin Sexton
 * @since 0.1.0
 */
interface PassPhraseGenerator : PasswordSequenceGenerator
