package org.kountersign.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import org.kountersign.core.generator.DigitPlacementPattern
import org.kountersign.core.generator.DigitPlacementStrategy
import org.kountersign.core.generator.RandomPassPhraseGenerator

class RandomPassPhraseCommand : CliktCommand(name = "passphrase") {
    private val words by option(
        "-w",
        "--words",
        help = "Words that will be used to generate pass phrase"
    ).split(",").defaultLazy {
        RandomPassPhraseCommand::class.java.getResource("/common-words.txt")
            .readText()
            .split("\n")
    }

    private val wordCount by option(
        "-n",
        "--number",
        help = "Number of words generated pass phrases will consist of"
    ).int().default(2).validate {
        require(it > 0) { "Word count must be a positive integer" }
    }

    private val capitalize by option(
        "-c",
        "--capitalize",
        help = "Denotes that words in pass phrase should be capitalized"
    ).flag(default = false)

    private val amount by option(
        "-a",
        "--amount",
        help = "Number of password that should be generated"
    ).int().default(1).validate {
        require(it > 0) { "Amount must be a positive integer" }
    }

    private val digits by option(
        "-i",
        help = "Digit characters that will be used when generating digit group"
    ).convert {
        it.split(",").map { number ->
            number.toInt()
        }.toSet()
    }.default(setOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0))

    private val pattern by option(
        "-p",
        "--pattern",
        help = "Determines where digits will be places in the generated pass phrase"
    ).enum<DigitPlacementPattern>(ignoreCase = true).default(DigitPlacementPattern.END)

    private val unique by option(
        "-u",
        "--unique",
        help = "Denotes whether generated digit groups should be the same or unique"
    ).flag(default = false)

    private val digitCount by option(
        "-d",
        "--digit-count",
        help = "Determines how many digits will be in a generated digit grouping"
    ).int().default(3).validate {
        require(it > 0) { "Digit count must be a positive integer" }
    }

    override fun run() {
        val strategy = DigitPlacementStrategy(
            digits = digits,
            digitCount = digitCount,
            unique = unique,
            pattern = pattern
        )

        val generator = RandomPassPhraseGenerator(words.toSet(), wordCount, capitalize, strategy)
        val passwordSequence = generator.generateSequence().take(amount)
        echo("Generated: ${passwordSequence.joinToString()}")
    }
}
