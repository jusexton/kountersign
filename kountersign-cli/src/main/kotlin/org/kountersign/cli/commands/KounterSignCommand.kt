package org.kountersign.cli.commands

import com.github.ajalt.clikt.core.CliktCommand

class KounterSignCommand : CliktCommand(
    name = "kountersign",
    help = KounterSignCommand::class.java.getResource("/logo.txt").readText()
) {
    override fun run() = Unit
}
