package org.firstinspires.ftc.teamcode.opModes.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.NormalizedColorSensor
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode.hardwareMap
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.GoBildaPrismDriver


@Configurable
object Spindexer : Subsystem {
    @JvmField var target = 0.0
    // Position PID used for indexing
    @JvmField var posPIDCoefficients = PIDCoefficients(-0.0015, 0.0, -0.000033)

    //ID 23: PPG = 2
    //ID 22: PGP = 1
    //ID 21: GPP = 0

    val controlSystem = controlSystem {
        posPid(posPIDCoefficients)
    }

    val SPINDEXER_TOLERANCE = 40.0
    val tolerance = KineticState(SPINDEXER_TOLERANCE)

    val spinAngle: Double
        get() = (360.0 / (4000.0) * spindexer.currentPosition)

    val spindexer = MotorEx("spindexer").brakeMode()
    lateinit var color0: NormalizedColorSensor
    lateinit var color1: NormalizedColorSensor
    lateinit var color2: NormalizedColorSensor
    lateinit var analogS: AnalogInput

    enum class State { PID, MANUAL }

    var state = State.MANUAL

    var cached0 = SpindexerColor.EMPTY
    var cached1 = SpindexerColor.EMPTY
    var cached2 = SpindexerColor.EMPTY

    var lastIntakeState = false

    private var startTime = 0L
    private var startPos = 0.0
    private var hasStopped = false

    val SPINDEXER_ENCODER_MAX = 4000.0
    val SPINDEXER_STEP = SPINDEXER_ENCODER_MAX / 3.0
    val SPINDEXER_ABS_ENC_V_MAX = 3.225
    val INTAKE_ABS_POS = 339.0
    var initDone = false
    var intakePos1 = 0.0
    var intakePos2 = 0.0
    var intakePos3 = 0.0
    var targetPosition = 0.0
    var targetReached = false
    val absEncV = { analogS.voltage }
    val absEncP = { analogS.voltage / SPINDEXER_ABS_ENC_V_MAX * SPINDEXER_ENCODER_MAX }
    val digEncV = { spindexer.currentPosition }
    fun digEncLimitV() : Double {
        var enc = spindexer.currentPosition % SPINDEXER_ENCODER_MAX
        if (enc < 0.0)
            enc += SPINDEXER_ENCODER_MAX
        return enc
    }

    override fun periodic() {

        if (!initDone) {
            var digEncOffset = (digEncLimitV() - absEncP()) % SPINDEXER_ENCODER_MAX
            if (digEncOffset < 0)
                digEncOffset += SPINDEXER_ENCODER_MAX

            var p1 = (INTAKE_ABS_POS + digEncOffset) % SPINDEXER_ENCODER_MAX

            if (p1 < SPINDEXER_STEP)
                intakePos1 = p1
            else {
                p1 = (p1 + SPINDEXER_STEP) % SPINDEXER_ENCODER_MAX
                if (p1 < SPINDEXER_STEP)
                    intakePos1 = p1
                else {
                    p1 = (p1 + SPINDEXER_STEP) % SPINDEXER_ENCODER_MAX
                    intakePos1 = p1
                }
            }

            intakePos2 = intakePos1 + SPINDEXER_STEP
            intakePos3 = intakePos2 + SPINDEXER_STEP

            initDone = true
        }

        val currentlyRunning = Intake.intakeRunning

        if (currentlyRunning) {
            cached0 = detectColorRGB(color0)
            cached1 = detectColorRGB(color1)
            cached2 = detectColorRGB(color2)
        }

        lastIntakeState = currentlyRunning

        when (state) {
            State.PID -> {
                spindexer.power =
                    controlSystem.calculate(spindexer.state).coerceIn(-0.8, 0.8)
            }

            State.MANUAL -> {
                return
            }
        }
    }

    fun forwardOnlyTarget(angleDeg: Double): Double {
        val targetInRev = angleToTicks(angleDeg)
        val currentRev = kotlin.math.floor(spindexer.currentPosition / (SPINDEXER_ENCODER_MAX))
        var newTarget = currentRev * (SPINDEXER_ENCODER_MAX) + targetInRev
        if (newTarget <= spindexer.currentPosition) {
            newTarget += (SPINDEXER_ENCODER_MAX)
        }
        return newTarget
    }

    fun intakePos(adj : Double = 0.0): Double {
        // Get nearest intake position. If adj is 0, it should return same position if already at an intake position
        var curPos = (digEncLimitV() + adj) % SPINDEXER_ENCODER_MAX;
        var movePos = 0.0

        if(curPos <= intakePos1)
            movePos = intakePos1 - curPos
        else if(curPos <= intakePos2)
            movePos =  intakePos2 - curPos
        else if(curPos <= intakePos3)
            movePos = intakePos3 - curPos
        else
            movePos = intakePos1 + SPINDEXER_ENCODER_MAX - curPos

        if(movePos > (SPINDEXER_STEP - SPINDEXER_TOLERANCE))
            movePos -= SPINDEXER_STEP

        return spindexer.currentPosition + movePos
    }

    val toIntakePos = LambdaCommand("toIntakePos")
        .setStart {
            state = State.PID
            targetReached = false
            targetPosition = intakePos()
            controlSystem.goal = KineticState(targetPosition)
        }
        .setIsDone {
            targetReached = controlSystem.isWithinTolerance(tolerance)
            if (targetReached) {
                state = State.MANUAL
            }
            targetReached
        }
        .requires(this)

    // Indexing
    // PID state: schedules RunToPosition
    val index0 = LambdaCommand("Index0Overshoot")
        .setStart {
            Intake.spinSlowSpeed()()
            state = State.PID
            targetReached = false
            targetPosition = intakePos()
//            controlSystem.goal = KineticState(forwardOnlyTarget(0.0))
              controlSystem.goal = KineticState(targetPosition)
        }
        .setIsDone {
            targetReached = controlSystem.isWithinTolerance(tolerance)
            if (targetReached) {
                state = State.MANUAL
                Intake.spinStop()
            }
            targetReached
        }
        .requires(this)

    val index1 = LambdaCommand("Index1Overshoot")
        .setStart {
            Intake.spinSlowSpeed()()
            state = State.PID
            targetReached = false
            targetPosition = intakePos() + SPINDEXER_STEP
            //controlSystem.goal = KineticState(forwardOnlyTarget(120.0))
            controlSystem.goal = KineticState(targetPosition)
        }
        .setIsDone {
            targetReached = controlSystem.isWithinTolerance(tolerance)
            if (targetReached) {
                state = State.MANUAL
                Intake.spinStop()
            }
            targetReached
        }
        .requires(this)

    val index2 = LambdaCommand("Index2Overshoot")
        .setStart {
            Intake.spinSlowSpeed()()
            state = State.PID
            targetReached = false
            targetPosition = intakePos() + SPINDEXER_STEP * 2
            //controlSystem.goal = KineticState(forwardOnlyTarget(240.0))
            controlSystem.goal = KineticState(targetPosition)
        }
        .setIsDone {
            targetReached = controlSystem.isWithinTolerance(tolerance)
            if (targetReached) {
                state = State.MANUAL
                Intake.spinStop()
            }
            targetReached
        }
        .requires(this)

    // manual: periodic stops PID
    val spinShot = InstantCommand {
    //    Intake.spinSlowSpeed()() // shouldn't be necessary, also is bad for battery usage when shooting
        state = State.MANUAL
        spindexer.power = 0.9
    }
        .requires(this)

    val spinShotIndex = InstantCommand {
        //    Intake.spinSlowSpeed()() // shouldn't be necessary, also is bad for battery usage when shooting
        state = State.MANUAL
        spindexer.power = 0.5
    }
        .requires(this)

    val shootTimedOrPosition = LambdaCommand("shootTimedOrPosition")
        .setStart {
            state = State.MANUAL
            startTime = System.currentTimeMillis()
            startPos = spindexer.currentPosition
            hasStopped = false
            spindexer.power = 0.9
        }
        .setIsDone {
            val now = System.currentTimeMillis()
            val timeDone = (now - startTime) >= 500
            val positionDone =
                (spindexer.currentPosition - startPos) <= -SPINDEXER_ENCODER_MAX
            val done = timeDone || positionDone
            if (done && !hasStopped) {
                spindexer.power = 0.0
                hasStopped = true
            }
            done
        }
        .requires(this)

    val stopShot = InstantCommand {
        state = State.MANUAL
        spindexer.power = 0.0
        clearColors()
    }
        .requires(this)

    fun autoIndex(b3: Int) = InstantCommand {
        state = State.PID
        when (desiredIndex(b3, PoseStorage.motif)) {
            0 -> index0()
            1 -> index1()
            2 -> index2()
        }
    }.requires(this)

    // tuning only
    fun spin() {
        controlSystem.goal = KineticState(position = target)
        spindexer.power = controlSystem.calculate(spindexer.state)
    }

    fun angleToTicks(angle : Double): Double {
        val ticks = angle * (4000.0)/360
        return ticks
    }

    fun ticksToAngle(ticks : Double): Double {
        val angle = ticks * 360/(4000.0)
        return angle
    }

    enum class SpindexerColor { PURPLE, GREEN, EMPTY }

    fun readAllColors() {
        detectColorRGB(color0)
        detectColorRGB(color1)
        detectColorRGB(color2)
    }

    fun detectColorRGB(sensor: NormalizedColorSensor): SpindexerColor {
        val colors = sensor.normalizedColors

        // Proximity, if Alpha low, slot empty
        if (colors.alpha < 0.15) return SpindexerColor.EMPTY

        return when {
            // If Green dominant
            colors.green > colors.red && colors.green > colors.blue -> {
                SpindexerColor.GREEN
            }
            // If Blue dominant (Purple)
            colors.blue > colors.red && colors.blue > colors.green -> {
                SpindexerColor.PURPLE
            }
            else -> SpindexerColor.EMPTY
        }
    }

    private fun colorToDigit(color: SpindexerColor): Int =
        when (color) {
            SpindexerColor.EMPTY -> 0
            SpindexerColor.GREEN -> 2
            SpindexerColor.PURPLE -> 1
        }

    fun computeDexIndex(b3: Int, b4: Int): Int {
        val b0 = colorToDigit(cached0)
        val b1 = colorToDigit(cached1)
        val b2 = colorToDigit(cached2)
        return b0 * 81 + b1 * 27 + b2 * 9 + b3 * 3 + b4
    }

    fun desiredIndex(b3: Int, b4: Int): Int =
        dexing[computeDexIndex(b3, b4)]

    val dexing = intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 2, 0, 1,
        1, 2, 0, 0, 1, 2, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 2, 2, 0, 1, 1, 2, 0, 2, 0, 1, 1, 2, 0, 0,
        1, 2, 1, 2, 0, 0, 1, 2, 2, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0,
        0, 0, 1, 0, 0, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0, 1, 2, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0, 1, 1, 2, 0,
        0, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 1, 1, 2, 0, 0, 1, 2, 1, 2, 0, 0, 1, 2, 2, 0, 1, 1,
        2, 0, 0, 1, 2, 2, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 2, 2, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0,
        1, 1, 2, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 2, 2, 0, 1, 1, 2, 0, 0, 1, 2, 2, 0, 1, 1, 2, 0,
        0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0)

    val isFull: Boolean
        get() {
            var purples = 0
            var greens = 0

            val cached = listOf(cached0, cached1, cached2)

            for (c in cached) {
                if (c == SpindexerColor.PURPLE) purples++
                if (c == SpindexerColor.GREEN) greens++
            }

            return purples + greens == 3
        }

    fun clearColors() {
        cached0 = SpindexerColor.EMPTY
        cached1 = SpindexerColor.EMPTY
        cached2 = SpindexerColor.EMPTY
    }

    val isBusy: Boolean
        get() = state == State.PID || (state == State.MANUAL && spindexer.power > 0.2)

    fun pixelCount(): Int {
        var count = 0
        if (cached0 != SpindexerColor.EMPTY) count++
        if (cached1 != SpindexerColor.EMPTY) count++
        if (cached2 != SpindexerColor.EMPTY) count++
        return count
    }

    override fun initialize() {
        analogS = hardwareMap.get(AnalogInput::class.java, "analogS")
        color0 = hardwareMap.get(NormalizedColorSensor::class.java, "cs0")
        color0.gain = 12.0f
        color1 = hardwareMap.get(NormalizedColorSensor::class.java, "cs1")
        color1.gain = 12.0f
        color2 = hardwareMap.get(NormalizedColorSensor::class.java, "cs2")
        color2.gain = 12.0f
    }
}