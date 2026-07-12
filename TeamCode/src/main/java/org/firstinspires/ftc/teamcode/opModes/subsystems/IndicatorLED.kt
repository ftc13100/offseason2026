package org.firstinspires.ftc.teamcode.opModes.subsystems
import dev.nextftc.core.commands.Command
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode.hardwareMap
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.Color
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.GoBildaPrismDriver
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.PrismAnimations
import java.lang.Thread.sleep

object IndicatorLED : Subsystem {
    private lateinit var prism: GoBildaPrismDriver
    private var colorIndex = 0
    private var previousColorIndex = 0

    override fun initialize() {
        prism = hardwareMap.get(GoBildaPrismDriver::class.java, "led")
        updateLED()
    }

    override fun periodic() {
        colorIndex = Spindexer.pixelCount()
        if (Intake.intakeRunning) colorIndex += 4

        if (colorIndex != previousColorIndex) updateLED()

        previousColorIndex = colorIndex
    }

    // DO NOT RUN BLOCKING METHOD IN REGULAR OPMODE INITIALIZATION
    // Creates permanent artboards for LEDs, only needs to be run once on new LEDs or when any animation changes are made.
    fun createArtboards() {
        val layers = arrayOf(
            // Solid colors -> layers[colorIndex]
            PrismAnimations.Solid(Color.RED),
            PrismAnimations.Solid(Color.YELLOW),
            PrismAnimations.Solid(Color.BLUE),
            PrismAnimations.Solid(Color.GREEN),
            // Pulsing colors -> layers[4 + colorIndex]
            PrismAnimations.Snakes(5, 0, 6, *arrayOf(Color.RED)),
            PrismAnimations.Snakes(5, 0, 6, *arrayOf(Color.YELLOW)),
            PrismAnimations.Snakes(5, 0, 6, *arrayOf(Color.BLUE)),
            PrismAnimations.Snakes(5, 0, 6, *arrayOf(Color.GREEN))
        )

        for (lIndex in layers.indices) {
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, layers[lIndex])
            sleep(500)
            prism.saveCurrentAnimationsToArtboard(GoBildaPrismDriver.Artboard.entries[lIndex])
        }

        prism.clearAllAnimations()
    }

    fun updateLED() {
        prism.loadAnimationsFromArtboard(GoBildaPrismDriver.Artboard.entries[colorIndex])
    }

    val stop = run { prism.clearAllAnimations() }.setInterruptible(false) // Still can't run properly on program stop, runs anywhere else though
}