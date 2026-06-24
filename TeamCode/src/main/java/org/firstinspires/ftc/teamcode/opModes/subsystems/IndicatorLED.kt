package org.firstinspires.ftc.teamcode.opModes.subsystems
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode.hardwareMap
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.Color
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.GoBildaPrismDriver
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.PrismAnimations

object IndicatorLED : Subsystem {
    private lateinit var prism : GoBildaPrismDriver
    private var colorIndex = 0 // Equivalent to number of artifacts
    private var flashing = false
    private var previousPixelCount = 0;

    override fun initialize() {
        prism = hardwareMap.get(GoBildaPrismDriver::class.java, "prism")
        createArtboards() // Temporary, will move to a better place
    }

    override fun periodic() {
        val pixelCount = Spindexer.pixelCount()

        if (pixelCount != previousPixelCount) colorIndex = pixelCount
        flashing = Intake.intakeRunning

        previousPixelCount = pixelCount
    }

    fun createArtboards() {
        val layers = arrayOf(
            // Solid colors -> layers[colorIndex]
            PrismAnimations.Solid(Color.RED),
            PrismAnimations.Solid(Color.YELLOW),
            PrismAnimations.Solid(Color.BLUE),
            PrismAnimations.Solid(Color.GREEN),
            // Pulsing colors -> layers[4 + colorIndex]
            PrismAnimations.Pulse(Color.RED, Color.TRANSPARENT),
            PrismAnimations.Pulse(Color.YELLOW, Color.TRANSPARENT),
            PrismAnimations.Pulse(Color.BLUE, Color.TRANSPARENT),
            PrismAnimations.Pulse(Color.GREEN, Color.TRANSPARENT)
        )

        for (lIndex in layers.indices) {
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, layers[lIndex])
            prism.saveCurrentAnimationsToArtboard(GoBildaPrismDriver.Artboard.entries[lIndex])
        }

        prism.clearAllAnimations()
    }

    fun updateLED() {
        if (flashing) prism.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.entries[colorIndex + 4])
        else prism.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.entries[colorIndex])
    }

//    val startFlash = instant {
//        flashing = true
//        updateLED()
//    }
//
//    val stopFlash = instant {
//        flashing = false
//        updateLED()
//    }
//
//    val setColorRed = instant {
//        colorIndex = 0
//        updateLED()
//    }
//
//    val setColorYellow = instant {
//        colorIndex = 1
//        updateLED()
//    }
//
//    val setColorBlue = instant {
//        colorIndex = 2
//        updateLED()
//    }
//
//    val setColorGreen = instant {
//        colorIndex = 3
//        updateLED()
//    }
}