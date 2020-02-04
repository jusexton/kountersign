package org.kountersign.cli.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter

class KounterSignCommand : CliktCommand(
    name = "kountersign",
    help = KounterSignCommand::class.java.getResource("/logo.txt").readText()
) {
    init {
        // TODO: In Clikt 2.5.0 this will be able to be removed.
        context { helpFormatter = CliktHelpFormatter(width = 1000) }
    }

    override fun run() = Unit
}
