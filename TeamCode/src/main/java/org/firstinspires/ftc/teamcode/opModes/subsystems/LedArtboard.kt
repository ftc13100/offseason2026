package org.firstinspires.ftc.teamcode.opModes.subsystems

import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.*
import kotlin.math.max
import kotlin.math.min

@Suppress("unused")
class LedArtboard(private val prism: GoBildaPrismDriver) {

    private enum class ActiveMode {
        OFF,
        ON
    }

    private var lastMode = ActiveMode.OFF
    private var lastBallCount = -1
    private var lastIntakeRunning: Boolean? = null
    private var lastArtboard = -1

    init {
        prism.setStripLength(STRIP_LENGTH)
    }

    fun setIntakeAndSpindexerLights(ballCount: Int, intakeRunning: Boolean) {
        val clamped = max(0, min(ballCount, 3))
        if (clamped == lastBallCount && lastIntakeRunning == intakeRunning) return

        prism.clearAllAnimations()

        val color = when (clamped) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.YELLOW
            else -> Color.GREEN
        }

        if (intakeRunning) {
            val blink = PrismAnimations.Blink(color, Color.TRANSPARENT)
            blink.setBrightness(75)
            blink.setPeriod(600)
            blink.setPrimaryColorPeriod(300)
            blink.setStartIndex(FIRST_LED)
            blink.setStopIndex(LAST_LED)
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, blink)
            lastMode = ActiveMode.ON
        } else {
            val solid = PrismAnimations.Solid(color, 75, FIRST_LED, LAST_LED)
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, solid)
            lastMode = ActiveMode.OFF
        }

        lastBallCount = clamped
        lastIntakeRunning = intakeRunning
        lastArtboard = -1
    }

    fun setSpindexerLights(ballCount: Int) {
        setIntakeAndSpindexerLights(ballCount, false)
    }

    fun setIntakeLights(running: Boolean) {
        val count = if (lastBallCount >= 0) lastBallCount else 0
        setIntakeAndSpindexerLights(count, running)
    }

    fun clear() {
        prism.clearAllAnimations()
        prism.show()
        lastMode = ActiveMode.OFF
        lastBallCount = -1
        lastIntakeRunning = null
        lastArtboard = -1
    }

    fun setLights(mode: Int) {
        setSpindexerLights(mode)
    }

    fun setDecorative() {
        prism.clearAllAnimations()

        val rainbow = PrismAnimations.Rainbow()
        rainbow.setBrightness(75)
        val sparkle = PrismAnimations.Sparkle()
        sparkle.setBrightness(75)

        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_0, rainbow)
        prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.LAYER_1, sparkle)

        lastMode = ActiveMode.OFF
        lastBallCount = -1
        lastIntakeRunning = null
        lastArtboard = -1
    }

    fun setLights(index: Int, @Suppress("UNUSED_PARAMETER") colorCode: Int) {
        if (index !in 0..2) return

        val color = when (colorCode) {
            0 -> Color.TRANSPARENT
            1 -> Color.PURPLE
            2 -> Color.GREEN
            else -> return
        }

        val start = index * 4
        val end = min(start + 3, LAST_LED)
        prism.setSolidColor(start, end, dim(color))
        prism.show()
    }

    private fun dim(color: Color): Color {
        return Color(
            (color.red * 0.75).toInt().coerceAtMost(255),
            (color.green * 0.75).toInt().coerceAtMost(255),
            (color.blue * 0.75).toInt().coerceAtMost(255)
        )
    }

    companion object {
        private const val STRIP_LENGTH = 12
        private const val FIRST_LED = 0
        private val LAST_LED = STRIP_LENGTH - 1
    }
}
