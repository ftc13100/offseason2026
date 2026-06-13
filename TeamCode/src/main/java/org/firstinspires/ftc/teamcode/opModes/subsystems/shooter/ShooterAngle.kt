package org.firstinspires.ftc.teamcode.opModes.subsystems.shooter

import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode

object ShooterAngle : Subsystem {
    lateinit var servo: Servo
    override fun initialize() {
        servo = ActiveOpMode.hardwareMap.get(Servo::class.java, "angle")
    }
    var manualOffset: Double = 0.0
    val SHOOTER_ANGLE_MIN = 0.0
    val SHOOTER_ANGLE_MAX = 0.8
    var targetPosition = 0.0

    fun update() = InstantCommand { servo.position = targetPosition }

    val angle_up = InstantCommand {
        servo.position = SHOOTER_ANGLE_MAX
        //was 0.5
    }

//    val angle_kindaUP = InstantCommand {
//        servo.position = 0.525
//    }
//
//    val angle_middle = InstantCommand {
//        servo.position = 0.6
//    }

    val angle_down = InstantCommand {
        servo.position = SHOOTER_ANGLE_MIN
        // was 0.7
    }

    val angle_mid = InstantCommand {
        servo.position = 0.65
        // was 0.7
    }
    val angle_verymid = InstantCommand {
        servo.position = 0.58
        // was 0.7
    }

    val angle_ooga = InstantCommand {
        servo.position = 0.8
        // was 0.7
    }


    fun toPos(pos: Double) =
        InstantCommand {
            servo.position = pos.coerceIn(SHOOTER_ANGLE_MIN, SHOOTER_ANGLE_MAX)
        }

    fun adjustAngle(adj: Double) {
        toPos(servo.position + adj)()
    }
}

