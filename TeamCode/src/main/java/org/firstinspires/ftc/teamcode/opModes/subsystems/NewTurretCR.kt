//package org.firstinspires.ftc.teamcode.opModes.subsystems
//
//import com.bylazar.configurables.annotations.Configurable
//import com.pedropathing.geometry.Pose
//import dev.nextftc.control.KineticState
//import dev.nextftc.control.builder.controlSystem
//import dev.nextftc.control.feedback.PIDCoefficients
//import dev.nextftc.core.subsystems.Subsystem
//import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
//import dev.nextftc.hardware.impl.CRServoEx
//import dev.nextftc.hardware.impl.MotorEx
//import org.firstinspires.ftc.teamcode.opModes.teleOp.ShooterController.goalClose
//import kotlin.math.abs
//import kotlin.math.atan2
//import kotlin.math.cos
//import kotlin.math.sin
//import dev.nextftc.control2.profiles.TrapezoidProfile
//import dev.nextftc.units.unittypes.AngleUnit
//
//@Configurable
//object NewTurretCR : Subsystem {
//    @JvmField var switchPIDmid = 400.0
//    @JvmField var switchPIDsmall = 150.0
//    @JvmField var target = 0.0
//    @JvmField var turretActive = false
//    @JvmField var goalTrackingActive = false
//    @JvmField var startPosition = 0.0
//
//    @JvmField var leftLimit = -5500.0
//    @JvmField var rightLimit = 5500.0
//    @JvmField var kVF = 0.0
//
//    @JvmField var targetAngleField = 0.0
//    @JvmField var turretAngle = 0.0
//    @JvmField var turretError = 0.0
//
//    @JvmField var posPIDCoefficientsLarge = PIDCoefficients(-0.00025, 0.0, -0.000)
//    @JvmField var posPIDCoefficientsMid = PIDCoefficients(-0.0008, 0.0, -0.000)
//    @JvmField var posPIDCoefficientsSmall = PIDCoefficients(-0.0015, 0.0, -0.00002)
//
//    @JvmField var manualMode = false
//
//    var power: Double = 0.0
//
//    val turret1: CRServoEx = CRServoEx("turret1")
//    val turret2: CRServoEx = CRServoEx("turret2")
//
//    // MotorEx used ONLY for its encoder port
//    lateinit var frontRightMotor: MotorEx
//
//    // Your specific gear ratio math
//    const val TURRET_TICKS_TO_DEGREES = 360.0 / (4000.0 * (141.0 / 47.0))
//    const val TURRET_OFFSET = -2.03852
//
//    var newX: Double = 0.0
//    var newY: Double = 0.0
//
//
//    val controlSystemLarge = controlSystem {
//        posPid(posPIDCoefficientsLarge)
//    }
//
//    val controlSystemMid = controlSystem {
//        posPid(posPIDCoefficientsMid)
//    }
//
//    val controlSystemSmall = controlSystem {
//        posPid(posPIDCoefficientsSmall)
//    }
//
//    val turretCurrentPos: Double
//        get() = frontRightMotor.currentPosition - startPosition
//
//    override fun initialize() {
//        // Initialize the motor port just to read the encoder
//        frontRightMotor = MotorEx("frontRight")
//    }
//
//    fun initPos() {
//        startPosition = frontRightMotor.currentPosition
//        target = startPosition
//        rightLimit = startPosition + 5500.0
//        leftLimit = startPosition - 5500.0
//        trackTarget()
//    }
//
//    fun trackTarget() {
//        goalTrackingActive = true
//        turretActive = false
//    }
//
//    fun stopTracking() {
//        goalTrackingActive = false
//        turretActive = false
//    }
//
//    fun toAngle(angleDegrees: Double) {
//        stopTracking()
//        turretActive = true
//        val ticks = angleDegrees / TURRET_TICKS_TO_DEGREES
//        target = (startPosition + ticks).coerceIn(leftLimit, rightLimit)
//    }
//
//    fun incrementAngle(angleDegrees: Double) {
//        stopTracking()
//        turretActive = true
//        val ticks = angleDegrees / TURRET_TICKS_TO_DEGREES
//        target = (target + ticks).coerceIn(leftLimit, rightLimit)
//    }
//
//    fun updateTarget() {
//        val turretPose = getTurretPose()
//        newX = abs(turretPose.x)
//        newY = abs(turretPose.y)
//
//        val headingDegrees = Math.toDegrees(follower.heading)
//        val angularVelDegrees = Math.toDegrees(follower.angularVelocity)
//
//        val targetAngleFieldRads = if (PoseStorage.blueAlliance) {
//            Math.PI - atan2(abs(goalClose.y - newY), abs(goalClose.x - newX))
//        } else {
//            atan2(abs(goalClose.y - newY), abs(goalClose.x - (144.0 - newX)))
//        }
//        targetAngleField = Math.toDegrees(targetAngleFieldRads)
//
//        val compensatedTargetAngle = targetAngleField + (angularVelDegrees * kVF)
//        turretAngle = headingDegrees - (turretCurrentPos * TURRET_TICKS_TO_DEGREES)
//
//        turretError = compensatedTargetAngle - turretAngle
//        while (turretError > 180.0) turretError -= 360.0
//        while (turretError < -180.0) turretError += 360.0
//
//        if (goalTrackingActive) {
//            target = (frontRightMotor.currentPosition - (turretError / TURRET_TICKS_TO_DEGREES))
//                .coerceIn(leftLimit, rightLimit)
//        }
//    }
//
//    override fun periodic() {
//        if (!manualMode && goalTrackingActive) {
//            updateTarget()
//        }
//
//        if (goalTrackingActive || turretActive) {
//            controlSystemLarge.goalClose = KineticState(target.coerceIn(leftLimit, rightLimit))
//            controlSystemMid.goalClose = KineticState(target.coerceIn(leftLimit, rightLimit))
//            controlSystemSmall.goalClose = KineticState(target.coerceIn(leftLimit, rightLimit))
//
//            // Calculate power based on the encoder state (frontRightMotor)
//            power = if (abs(target - frontRightMotor.currentPosition) > switchPIDmid) {
//                controlSystemLarge.calculate(frontRightMotor.state).coerceIn(-0.4,0.4)
//            } else if (abs(target-frontRightMotor.currentPosition) >switchPIDsmall){
//                controlSystemMid.calculate(frontRightMotor.state).coerceIn(-0.2,0.2)
//            } else {
//                controlSystemSmall.calculate(frontRightMotor.state).coerceIn(-0.125,0.125)
//            }
//
//            // Apply power to CR Servos
//            turret1.power = power
//            turret2.power = power
//        } else {
//            turret1.power = 0.0
//            turret2.power = 0.0
//        }
//    }
//
//    fun getTurretPose(): Pose {
//        return Pose(
//            follower.pose.x + TURRET_OFFSET * cos(follower.pose.heading),
//            follower.pose.y + TURRET_OFFSET * sin(follower.pose.heading),
//            follower.pose.heading
//        )
//    }
//}