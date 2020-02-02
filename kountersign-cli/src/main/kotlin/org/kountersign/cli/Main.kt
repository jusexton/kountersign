package org.kountersign.cli

import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption
import org.kountersign.cli.commands.KounterSignCommand
import org.kountersign.cli.commands.RandomCharacterPasswordCommand
import org.kountersign.cli.commands.RandomPassPhraseCommand

fun main(args: Array<String>) =
    KounterSignCommand()
        .subcommands(RandomCharacterPasswordCommand(), RandomPassPhraseCommand())
        .versionOption("0.1.0")
        .main(args)
