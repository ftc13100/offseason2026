package org.firstinspires.ftc.teamcode.opModes.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.opModes.teleOp.BiLinearShooter.goalClose
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import com.pedropathing.math.Vector
import com.qualcomm.robotcore.hardware.AnalogInput
import com.qualcomm.robotcore.hardware.DcMotorEx
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.opModes.teleOp.BiLinearShooter.goalFar

@Configurable
object NewTurret : Subsystem {

    lateinit var turret1: Servo
    lateinit var turret2: Servo
    lateinit var turretDigital: MotorEx

    val TURRET_LIMIT_LOW = 22.2
    val TURRET_LIMIT_HIGH = 337.8

    var targetServoPosition = 0.5  // 0.5 = straight back
    var targetAngleRobotRef: Double = 180.0 // 0 is facing forward, CCW increasing
    var targetAngleField: Double = 270.0 // 0 is right, increases CCW

    var targetAngleStatic: Double = 0.0
    var targetAngleAV: Double = 0.0
    val TURRET_MAX_TOLERANCE_DEGREES = 5 // Uses degrees TODO: Tune
    val TURRET_CALIBRATION_MAX_DELTA_DEGREES = 5 // Uses degrees TODO: Tune
    val TURRET_DIGITAL_TPD = 12000

    var turretOffset = 0.0
    var collectedTurretOffset = false
    var turretRelativePos = 1000.0
    var lastTurretRelativePos = 999.0
    var turretAbsolutePos = 0.0
    var idealAngle = 0.0

    var targetReached = true

    @JvmField var goalTrackingActive = false
    @JvmField var kVF = -4.5

    var turretX = 0.0
    var turretY = 0.0

    @JvmField var TURRET_OFFSET = -2.03852

    var manualOffsetAngle: Double = 0.0

//    val controlSystem = controlSystem {
//        posPid(posPIDCoefficients)
//    }

    override fun initialize() {
        turret1 = dev.nextftc.ftc.ActiveOpMode.hardwareMap.get(Servo::class.java, "turret1")
        turret2 = dev.nextftc.ftc.ActiveOpMode.hardwareMap.get(Servo::class.java, "turret2")
        val frontRightMotor = dev.nextftc.ftc.ActiveOpMode.hardwareMap.get(DcMotorEx::class.java, "frontRight")
        turretDigital = MotorEx(frontRightMotor)

        targetReached = false
    }

    //val encoderDPosition = { backRightMotor.currentPosition - turretEOffset}
    //val encoderDAngle = { 360.0 - (backRightMotor.currentPosition - turretEOffset) * (360.0/12000.0) }
    fun trackTarget() {
        goalTrackingActive = true
    }

    fun stopTracking() {
        goalTrackingActive = false
    }

    val toPos = InstantCommand {
        turret1.position = targetServoPosition
        turret2.position = targetServoPosition
    }

    // input angle (degrees) is robot centric, 0 is front
    // ccw increasing degrees
    fun toAngle(angle: Double) {
        targetAngleRobotRef = angle
        if (targetAngleRobotRef < 0.0) {
            targetAngleRobotRef += 360.0
        } else if (targetAngleRobotRef > 360.0) {
            targetAngleRobotRef -= 360.0
        }
        setServoPos()
    }

    fun adjustAngle(angle: Double) {
        stopTracking()
        targetAngleRobotRef += angle
        setServoPos()
    }
    fun setServoPos() {
        if (targetAngleRobotRef < 0) {
            targetAngleRobotRef += 360.0
        }
        if (targetAngleRobotRef > 360.0) {
            targetAngleRobotRef -= 360.0
        }

        idealAngle = targetAngleRobotRef

        if(targetAngleRobotRef < TURRET_LIMIT_LOW)
            targetAngleRobotRef = TURRET_LIMIT_LOW;
        if(targetAngleRobotRef > TURRET_LIMIT_HIGH)
            targetAngleRobotRef = TURRET_LIMIT_HIGH

        targetServoPosition = (targetAngleRobotRef - TURRET_LIMIT_LOW) / (TURRET_LIMIT_HIGH - TURRET_LIMIT_LOW)
        toPos()
    }

    fun getTurretPose(): Pose {
        return Pose(
            follower.pose.x + TURRET_OFFSET * cos(follower.pose.heading),
            follower.pose.y + TURRET_OFFSET * sin(follower.pose.heading),
            follower.pose.heading
        )
    }

    fun getTurretVelocity(
        robotVelocity: Vector,
        angularVelocity: Double,
        robotHeadingRadians: Double
    ): Vector {
        val result = Vector()  // initialize
        result.setOrthogonalComponents(
            robotVelocity.xComponent - angularVelocity * TURRET_OFFSET * sin(robotHeadingRadians),
            robotVelocity.yComponent + angularVelocity * TURRET_OFFSET * cos(robotHeadingRadians)
        )
        return result
    }

    override fun periodic() {
        if (!goalTrackingActive) return

        if (!collectedTurretOffset) {
            val deltaTurretHeading = abs(turretRelativePos - lastTurretRelativePos)
            if (deltaTurretHeading < TURRET_CALIBRATION_MAX_DELTA_DEGREES) {
                turretOffset = targetAngleAV - turretRelativePos
                collectedTurretOffset = true
            }
        }

        var angularVel = follower.angularVelocity // rad/sec

        var robotHeading = Math.toDegrees(follower.heading)

        turretX = getTurretPose().x
        turretY = getTurretPose().y

        if (robotHeading < 0.0) {
            robotHeading += 360.0
        }
        val turretRobotAdj = 360.0 - robotHeading

        val goal = if (turretY > 50.0) goalClose else goalFar

        // Compute target angle in degrees
        targetAngleField = if (PoseStorage.blueAlliance) {
            180.0 - Math.toDegrees(atan2(abs(goal.y - turretY), abs(goal.x - turretX)))
        } else {
            Math.toDegrees(atan2(abs(goal.y - turretY), abs(goal.x - (141.5 - turretX))))
        }

        targetAngleStatic = targetAngleField + turretRobotAdj;
        targetAngleAV = targetAngleField + turretRobotAdj + (angularVel * kVF)
        toAngle(targetAngleAV + manualOffsetAngle)

        lastTurretRelativePos = turretRelativePos
        turretRelativePos = turretDigital.currentPosition * TURRET_DIGITAL_TPD
        turretAbsolutePos = turretRelativePos + turretOffset

        if (collectedTurretOffset && abs(turretAbsolutePos - idealAngle) < TURRET_MAX_TOLERANCE_DEGREES) {
            targetReached = true
        } else {
            targetReached = false
        }
    }
}
