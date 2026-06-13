//package org.firstinspires.ftc.teamcode.opModes.subsystems
//
//import com.bylazar.configurables.annotations.Configurable
//import com.pedropathing.geometry.Pose
//import com.qualcomm.robotcore.hardware.DcMotor
//import com.qualcomm.robotcore.util.ElapsedTime
//import dev.nextftc.control.KineticState
//import dev.nextftc.control.builder.controlSystem
//import dev.nextftc.control.feedback.PIDCoefficients
//import dev.nextftc.core.subsystems.Subsystem
//import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
//import dev.nextftc.hardware.impl.MotorEx
//import org.firstinspires.ftc.teamcode.opModes.teleOp.ShooterController.goalClose
//import kotlin.math.abs
//import kotlin.math.atan2
//
//@Configurable
//object Turret : Subsystem {
//    @JvmField
//    var target = 0.0
//
//    @JvmField
//    var turretActive = false
//
//    @JvmField
//    var goalTrackingActive = false
//
//    @JvmField
//    var turretReady = false
//
//    @JvmField
//    var turretReadyMs = 0.0
//
//    @JvmField
//    var startPosition = 0.0
//
//    @JvmField
//    var leftLimit = -3000.0
//
//    @JvmField
//    var rightLimit = 3000.0
//
//    @JvmField
//    var targetAngle = 0.0
//
//    @JvmField
//    var turretAngle = 0.0
//
//    @JvmField
//    var heading = 0.0
//
//    @JvmField
//    var turretError = 0.0
//
//    @JvmField
//    var turretTolearanceCount = 0
//
//    val turretCurrentPos: Double
//        get() = turret.currentPosition - startPosition
//
//    val turretErrorTicks: Double
//        get() = turretError / TURRET_TICKS_TO_RADS
//
//    @JvmField
//    var posPIDCoefficients = PIDCoefficients(0.0097, 0.0, 0.00015)
//
//    val turret = MotorEx("turret").brakeMode()
//    private val runtime = ElapsedTime()
//
//    const val TURRET_TICKS_TO_RADS = (Math.PI * 2) / (1425.1 * (138 / 16))
//
//    val controlSystem = controlSystem {
//        posPid(posPIDCoefficients)
//    }
//
//    override fun initialize() {
////        turret.zero()
////        turret.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
////        turret.motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
//    }
//
//    fun initPos() {
//        startPosition = turret.currentPosition
//
//        target = startPosition
//        rightLimit = startPosition + 3000.0
//        leftLimit = startPosition - 3000.0
//        trackTarget()
//    }
//
//    /**
//     * PID-Only turn: updates target and turret logic handles the rest.
//     */
//    fun turn(posAdj: Double) {
//        target = (target + posAdj).coerceIn(leftLimit, rightLimit)
//        goalTrackingActive = false       // stop tracking if active
//        turretActive = true              // PID hold mode ON
//        turretReady = false
//        turretTolearanceCount = 0
//        runtime.reset()
//        // Ready automatically when close enough (handled in periodic)
//    }
//
//    fun resetToStartPosition() {
//        turn(startPosition - turret.currentPosition)
//    }
//
//    /**
//     * Enable auto tracking; PID will follow continuously in periodic.
//     */
//    fun trackTarget() {
//        goalTrackingActive = true
//        turretActive = false
//        turretReady = false
//        turretReadyMs = 0.0
//    }
//
//    /**
//     * Compute a new turret angle target during auto-tracking.
//     */
//    fun updateTarget() {
//        val x = abs(follower.pose.x)
//        val y = abs(follower.pose.y)
//        heading = follower.heading
//
//        targetAngle = if (PoseStorage.blueAlliance) {
//            Math.PI - atan2(abs(goalClose.y - y), abs(goalClose.x - x))
//        } else {
//            atan2(abs(goalClose.y - y), abs(goalClose.x - (144.0 - x)))
//        }
//
//        turretAngle =
//            heading - turretCurrentPos * TURRET_TICKS_TO_RADS
//
//        turretError = targetAngle - turretAngle
//        if (turretError > Math.PI) turretError -= 2 * Math.PI
//
//        if (goalTrackingActive) {
//            target =
//                (turret.currentPosition - turretError / TURRET_TICKS_TO_RADS).coerceIn(
//                    leftLimit,
//                    rightLimit
//                )
//        }
//    }
//
//    fun turretHeadingWithMargin(heading: Double): Double {
//        val curPos = turret.currentPosition.coerceIn(leftLimit + 500, rightLimit - 500)
//        return (heading - (curPos - startPosition) * TURRET_TICKS_TO_RADS)
//    }
//
//    fun turretHeading(heading: Double): Double {
//        return (heading - (turretCurrentPos) * TURRET_TICKS_TO_RADS)
//    }
//
//    fun turretAzDeg(): Double {
//        return Math.toDegrees((turretCurrentPos) * TURRET_TICKS_TO_RADS)
//    }
//
//    override fun periodic() {
//        val current = turret.currentPosition
//        updateTarget()
//
//        // 1. TARGET TRACKING MODE
//        if (goalTrackingActive) {
//            controlSystem.goalClose = KineticState(target)
//            turret.power = controlSystem.calculate(turret.state)
//            return
//        }
//
//        // 2. PID HOLD MODE
//        if (turretActive) {
//            controlSystem.goalClose = KineticState(position = target)
//            turret.power = controlSystem.calculate(turret.state)
//
//            // Determine when turret is "ready"
//            if (!turretReady && abs(current - target) < 4.0 && ++turretTolearanceCount >= 3) {
//                turretReady = true
//                turretReadyMs = runtime.milliseconds()
//            }
//            return
//        }
//
//        // 3. IDLE MODE
//        turret.power = 0.0
//    }
//}
