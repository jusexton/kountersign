package org.kountersign.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.default
import com.github.ajalt.clikt.parameters.groups.mutuallyExclusiveOptions
import com.github.ajalt.clikt.parameters.groups.single
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import org.kountersign.core.generator.RandomCharacterPasswordGenerator
import org.kountersign.core.text.*

class RandomCharacterPasswordCommand : CliktCommand(name = "random") {
    private val characterSetChoices = mapOf(
        "lower" to LOWER_CASE,
        "upper" to UPPER_CASE,
        "letters" to LETTERS,
        "digits" to DIGITS,
        "symbols" to PUNCTUATION,
        "all" to ALL
    )

    private val characters by mutuallyExclusiveOptions(
        option(
            "-c",
            "--characters",
            help = "Characters used to randomly generate string"
        ).convert { it.toCharArray().toSet() },
        option(
            "-s",
            "--character-sets",
            help = "Characters used to randomly generate string"
        ).choice(characterSetChoices)
    ).single().default(ALL)

    private val length by option(
        "-l",
        "--length",
        help = "Length of randomly generated password"
    ).int().default(10).validate {
        require(it > 0) { "Length must be a positive integer" }
    }

    private val amount by option(
        "-a",
        "--amount",
        help = "Number of password that should be generated"
    ).int().default(1).validate {
        require(it > 0) { "Amount must be a positive integer" }
    }

    override fun run() {
        val generator = RandomCharacterPasswordGenerator(characters, length)
        val passwordSequence = generator.generateSequence().take(amount)
        echo("Generated: ${passwordSequence.joinToString()}")
    }
}
