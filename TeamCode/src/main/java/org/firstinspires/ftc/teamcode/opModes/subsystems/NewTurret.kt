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
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.opModes.teleOp.BiLinearShooter
import org.firstinspires.ftc.teamcode.opModes.teleOp.BiLinearShooter.goalFar

@Configurable
object NewTurret : Subsystem {

    //val backRightMotor = MotorEx("backRight").brakeMode()

    lateinit var turret1: Servo
    lateinit var turret2: Servo

    val TURRET_LIMIT_LOW = 22.2
    val TURRET_LIMIT_HIGH = 337.8

    var turretEInitialized : Boolean = false
    var turretEOffset = 0.0
    var targetServoPosition = 0.5  // 0.5 = straight back
    var targetAngleRobotRef: Double = 180.0 // 0 is facing forward, CCW increasing
    var targetAngleField: Double = 270.0 // 0 is right, increases CCW

    var targetAngleStatic: Double = 0.0
    var targetAngleAV: Double = 0.0

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
    }
}
