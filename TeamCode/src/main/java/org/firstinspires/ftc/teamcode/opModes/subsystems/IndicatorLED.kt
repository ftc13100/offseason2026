package org.firstinspires.ftc.teamcode.opModes.subsystems
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode.hardwareMap
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.Color
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.GoBildaPrismDriver
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.PrismAnimations
import java.lang.Thread.sleep

object IndicatorLED : Subsystem {
    private lateinit var prism: GoBildaPrismDriver
    private var colorIndex = 0 // Equivalent to number of artifacts
    private var flashing = false
    private var previousPixelCount = 0

    override fun initialize() {
        prism = hardwareMap.get(GoBildaPrismDriver::class.java, "led")
    }

    override fun periodic() {
        val pixelCount = Spindexer.pixelCount()

        if (pixelCount != previousPixelCount) colorIndex = pixelCount
        flashing = Intake.intakeRunning

        previousPixelCount = pixelCount

        updateLED()
    }

    fun createArtboards() {
        val layers = arrayOf(
            // Solid colors -> layers[colorIndex]
            PrismAnimations.Solid(Color.RED),
            PrismAnimations.Solid(Color.YELLOW),
            PrismAnimations.Solid(Color.BLUE),
            PrismAnimations.Solid(Color.GREEN),
            // Pulsing colors -> layers[4 + colorIndex]
        PrismAnimations.Pulse(Color.RED, Color.TRANSPARENT, 500),
            PrismAnimations.Pulse(Color.YELLOW, Color.TRANSPARENT),
            PrismAnimations.Pulse(Color.BLUE, Color.TRANSPARENT),
            PrismAnimations.Pulse(Color.GREEN, Color.TRANSPARENT)
        )

        for (lIndex in layers.indices) {
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, layers[lIndex])
            sleep(500)
            prism.saveCurrentAnimationsToArtboard(GoBildaPrismDriver.Artboard.entries[lIndex])
        }

        prism.clearAllAnimations()
    }

    fun updateLED() {
        if (flashing) prism.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.entries[colorIndex + 4])
        else prism.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.entries[colorIndex])
    }

    val stop = instant { prism.clearAllAnimations() }
}