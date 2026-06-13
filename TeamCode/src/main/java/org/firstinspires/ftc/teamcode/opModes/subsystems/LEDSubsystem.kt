package org.firstinspires.ftc.teamcode.opModes.subsystems

import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.*
import kotlin.math.max
import kotlin.math.min

class LEDSubsystem(prism: GoBildaPrismDriver) {

    // internal state machine
    private enum class ActiveMode {
        NONE,
        SPIX,
        INTAKE
    }

    private val prism: GoBildaPrismDriver

    // caching to avoid unnecessary I2C writes like sachet said
    private var lastMode = ActiveMode.NONE
    private var lastSpindexerCount = -1
    private var lastIntakeRunning: Boolean? = null

    //cache of the last color written to each LED
    private val ledCache = Array(STRIP_LENGTH) { Color.TRANSPARENT }

    //dirty flag (thanks copilot for explaining what ts is) so we only call prism.show() when needed
    private var dirty = false

    //software blinking timer for intake mode
    private var blinkState = false
    private var lastBlinkTime = 0L

    init {
        this.prism = prism
        this.prism.setStripLength(STRIP_LENGTH)
    }

    //writes only if LED actually changed
    private fun setRange(start: Int, end: Int, color: Color) {
        var changed = false
        for (i in start..end) {
            if (ledCache[i] != color) {
                ledCache[i] = color
                prism.setSolidColor(start, end, color)
                changed = true
            }
        }
        if (changed) dirty = true
    }

    //flushes to hardware only when dirty
    private fun flush() {
        if (dirty) {
            prism.show()
            dirty = false
        }
    }

    /**
     * spindexer led states (can change if mohit and jackson wanna):
     * 0 = red, 1 = blue, 2 = yellow, 3 = green.
     */
    fun setSpindexerLights(ballCount: Int) {
        val count = max(0, min(ballCount, 3))

        // skip if nothing changed
        if (lastMode == ActiveMode.SPIX && lastSpindexerCount == count) return

        val color = when (count) {
            0 -> Color.RED
            1 -> Color.BLUE
            2 -> Color.YELLOW
            else -> Color.GREEN
        }

        // clear animations only when switching modes
        if (lastMode != ActiveMode.SPIX) prism.clearAllAnimations()

        setRange(FIRST_LED, LAST_LED, color)
        flush()

        lastMode = ActiveMode.SPIX
        lastSpindexerCount = count
        lastIntakeRunning = null
    }

    /**
     * intake led modes:
     *
     * running = flashing
     * stopped = solid white.
     */
    fun setIntakeLights(running: Boolean) {
        //skip if nothing changed
        if (lastMode == ActiveMode.INTAKE && lastIntakeRunning == running) return

        //clear animations only when switching modes
        if (lastMode != ActiveMode.INTAKE) prism.clearAllAnimations()

        if (!running) {
            //solid white
            setRange(FIRST_LED, LAST_LED, Color.WHITE)
            flush()
        }

        lastMode = ActiveMode.INTAKE
        lastIntakeRunning = running
        lastSpindexerCount = -1
    }

    //call this from your loop
    fun update(timeMs: Long) {
        if (lastMode == ActiveMode.INTAKE && lastIntakeRunning == true) {
            //software blink every 300ms
            if (timeMs - lastBlinkTime > 300) {
                blinkState = !blinkState
                lastBlinkTime = timeMs

                val color = if (blinkState) Color.WHITE else Color.TRANSPARENT
                setRange(FIRST_LED, LAST_LED, color)
                flush()
            }
        }
    }

    fun clear() {
        setRange(FIRST_LED, LAST_LED, Color.TRANSPARENT)
        flush()

        lastMode = ActiveMode.NONE
        lastSpindexerCount = -1
        lastIntakeRunning = null
    }

    //legacy wrapper thing that copilot told me to have
    fun setLights(mode: Int) {
        setSpindexerLights(mode)
    }

    fun setDecorative() {
        // only clear animations if not already in decorative mode
        if (lastMode != ActiveMode.NONE) prism.clearAllAnimations()

        prism.insertAndUpdateAnimation(
            GoBildaPrismDriver.LayerHeight.LAYER_0,
            PrismAnimations.Rainbow()
        )
        prism.insertAndUpdateAnimation(
            GoBildaPrismDriver.LayerHeight.LAYER_1,
            PrismAnimations.Sparkle()
        )

        lastMode = ActiveMode.NONE
    }

    fun setLights(index: Int, colorCode: Int) {
        if (index < 0 || index > 2) return

        val color = when (colorCode) {
            0 -> Color.TRANSPARENT
            1 -> Color.PURPLE
            2 -> Color.GREEN
            else -> return
        }

        val start = index * 4
        val end = min(start + 3, LAST_LED)

        setRange(start, end, color)
        flush()

        lastMode = ActiveMode.NONE
    }

    companion object {
        // if we ever add in another led strip we can change that stuff here
        private const val STRIP_LENGTH = 12
        private const val FIRST_LED = 0
        private val LAST_LED = STRIP_LENGTH - 1
    }
}
