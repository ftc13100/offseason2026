package org.firstinspires.ftc.teamcode.opModes.subsystems

import dev.nextftc.core.subsystems.Subsystem
import org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.GoBildaPrismDriver
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.PrismAnimations

class IndicatorLED : Subsystem {
    private lateinit var prism : GoBildaPrismDriver

    private var numArtifacts = 0
    private var flashing = false

    override fun initialize() {
        prism = hardwareMap.get(GoBildaPrismDriver::class.java, "prism")
    }

    fun createArtboards() {
        val layers = arrayOf(
            PrismAnimations.Solid()
            // All possible layers (flashing/sold for all three colors)
        )

        // Initialize parameters for every animation by looping through 'layers' and create artboards for each one to use in instant commands
        // During robot initialization, it is legal to flicker our lights, and we can add each one to an artboard in memory before shutting them all off
        // This only needs to be done once, and will save to the device to later recall from
    }

    fun updateLED() {
        // Load an artboard based on numArtifacts + flashing
    }

    val startFlash = instant {
        flashing = true
        updateLED()
    }

    val stopFlash = instant {
        flashing = false
        updateLED()
    }

    val setColorRed = instant {
        numArtifacts = 0
        updateLED()
    }

    val setColorYellow = instant {
        numArtifacts = 1
        updateLED()
    }

    val setColorBlue = instant {
        numArtifacts = 2
        updateLED()
    }

    val setColorGreen = instant {
        numArtifacts = 3
        updateLED()
    }
}