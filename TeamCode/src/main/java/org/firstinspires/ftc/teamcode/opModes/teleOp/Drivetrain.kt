package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import dev.nextftc.bindings.BindingManager
import dev.nextftc.bindings.button
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.commands.CommandManager
import dev.nextftc.core.units.rad
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.FieldCentric
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake
import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake.intake
import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake.intakeRunning
import org.firstinspires.ftc.teamcode.opModes.subsystems.NewTurret
import org.firstinspires.ftc.teamcode.opModes.subsystems.PoseStorage
import org.firstinspires.ftc.teamcode.opModes.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.ShooterAngle
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.abs

private  const val TELEMETRY_INTERVAL:Int = 250

@TeleOp(name = "Drivetrain")
class Drivetrain : NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(
                Intake, Spindexer, Shooter, ShooterAngle, NewTurret, PoseStorage
            ),
            BindingsComponent,
            BulkReadComponent,
            PedroComponent(Constants::createFollower)
        )
    }

    private val frontLeftName = "frontLeft"
    private val frontRightName = "frontRight"
    private val backLeftName = "backLeft"
    private val backRightName = "backRight"


    private lateinit var frontLeftMotor: MotorEx
    private lateinit var frontRightMotor: MotorEx
    private lateinit var backLeftMotor: MotorEx
    private lateinit var backRightMotor: MotorEx

    private lateinit var driverControlled: MecanumDriverControlled

    private var lastLoopTime = 0.0
    private var maxLoopTime = 0.0
    private var loopTimeAverage = 0.0
    private var lastTelemetryTime = 0.0
    private var firstOnUpdate = true
    private var testMode = false
    private val startPose = PoseStorage.poseEnd
    private val testingPose = Pose(72.0, 72.0, Math.toRadians(90.0))

    override fun onInit() {

        if (abs(startPose.x) < 0.1 && abs(startPose.y) < 0.1) {
            follower.setStartingPose(testingPose)
            PoseStorage.blueAlliance = false
            testMode = true
        } else {
            follower.setStartingPose(startPose)
        }

        frontLeftMotor = MotorEx(frontLeftName)
        frontRightMotor = MotorEx(frontRightName)
        backLeftMotor = MotorEx(backLeftName)
        backRightMotor = MotorEx(backRightName)

        listOf(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor).forEach {
            it.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
        follower.update()
//        NewTurret.trackTarget()
    }

    override fun onStartButtonPressed() {
//        NewTurret.backRightMotor.atPosition(6000.0)
        NewTurret.trackTarget()

        driverControlled = MecanumDriverControlled(
            frontLeftMotor,
            frontRightMotor,
            backLeftMotor,
            backRightMotor,
            -Gamepads.gamepad1.leftStickY,
            Gamepads.gamepad1.leftStickX,
            Gamepads.gamepad1.rightStickX,
            mode = FieldCentric { follower.pose.heading.rad }
        )
        driverControlled.scalar = 1.0

        // Reset location and heading
        Gamepads.gamepad1.leftTrigger.asButton { it > 0.5 } and Gamepads.gamepad1.rightTrigger.asButton { it > 0.5 }
            .whenBecomesTrue {
                if (PoseStorage.blueAlliance) {
                    follower.pose = Pose(19.25, 121.5, Math.toRadians(140.0))
                } else {
                    follower.pose = Pose(19.25, 121.5, Math.toRadians(140.0)).mirror()
                }
            }

        // slow mode
        button { gamepad1.y }
            .whenTrue { driverControlled.scalar = 0.5 }
            .whenFalse { driverControlled.scalar = 1.0 }


        button { gamepad1.dpad_up }
            .whenBecomesTrue {
                if (NewTurret.goalTrackingActive)
                    Shooter.manualOffset += 20.0
                else
                    Shooter.adjustSpeed(20.0)
            }

        button { gamepad1.dpad_down }
            .whenBecomesTrue {
                if (NewTurret.goalTrackingActive)
                    Shooter.manualOffset -= 20.0
                else
                    Shooter.adjustSpeed(-20.0)
            }

        button { gamepad2.dpad_up }
            .whenBecomesTrue {
                if(NewTurret.goalTrackingActive)
                    Shooter.manualOffset += 50.0
                else
                    Shooter.adjustSpeed(50.0)
            }

        button { gamepad2.dpad_down }
            .whenBecomesTrue {
                if(NewTurret.goalTrackingActive)
                    Shooter.manualOffset -= 50.0
                else
                    Shooter.adjustSpeed( -50.0)
            }

        button { gamepad1.dpad_right }
            .whenBecomesTrue {
                if(NewTurret.goalTrackingActive)
                    ShooterAngle.manualOffset += 0.05
                else
                    ShooterAngle.adjustAngle( 0.05)
            }

        button { gamepad1.dpad_left }
            .whenBecomesTrue {
                if(NewTurret.goalTrackingActive)
                    ShooterAngle.manualOffset -= 0.05
                else
                    ShooterAngle.adjustAngle( -0.05)
            }

//        button { gamepad2.right_bumper}
//            .whenBecomesTrue {
//                if(NewTurret.goalTrackingActive)
//                    NewTurret.manualOffsetAngle += -10.0
//                else
//                    NewTurret.adjustAngle(-10.0)
//            }
//
//        button { gamepad2.left_bumper }
//            .whenBecomesTrue {
//                if(NewTurret.goalTrackingActive)
//                    NewTurret.manualOffsetAngle += 10.0
//                else
//                    NewTurret.adjustAngle(10.0)
//            }
//
//        button { gamepad2.right_trigger > 0.5 }
//            .whenTrue {
//                if(NewTurret.goalTrackingActive)
//                    NewTurret.manualOffsetAngle -= 0.1
//                else
//                    NewTurret.adjustAngle(-0.1)
//            }
//
//        button { gamepad2.left_trigger > 0.5 }
//            .whenTrue {
//                if (NewTurret.goalTrackingActive)
//                    NewTurret.manualOffsetAngle += 0.1
//                else
//                    NewTurret.adjustAngle(0.1)
//            }

        //Intake artifact
        button { gamepad1.left_bumper }
  //          .toggleOnBecomesTrue()
            .whenBecomesTrue {
                Spindexer.toIntakePos()
            }
            .whenTrue {
                Intake.spinHeld()
            }
            .whenBecomesFalse {
                Intake.spinStop()
            }

                //Outtake artifact
        button { gamepad1.right_bumper }
            .whenBecomesTrue {
                Intake.spinReverse()
            }
            .whenBecomesFalse {
                Intake.spinStop()
            }

        button { gamepad1.left_trigger > 0.4 }
            .whenTrue {
                Spindexer.spinShot()
            }
            .whenBecomesFalse {
                Spindexer.stopShot()
                Intake.spinStop()
            }

//        button { gamepad1.right_trigger > 0.4 }
//            .whenTrue {
//                Spindexer.spinShotIndex()
//            }
//            .whenBecomesFalse {
//                Spindexer.stopShot()
//                Intake.spinStop()
//            }

        button { gamepad2.a }
            .whenTrue {
                Spindexer.spinShotIndex()
            }
            .whenBecomesFalse {
                Spindexer.stopShot()
                Intake.spinStop()
            }

        button { gamepad2.x}
            .whenBecomesTrue {
                Spindexer.autoIndex(0)()
            }

        button { gamepad2.y}
            .whenBecomesTrue {
                Spindexer.autoIndex(1)()
            }

        button { gamepad2.b}
            .whenBecomesTrue {
                Spindexer.autoIndex(2)()
            }


        button {gamepad2.left_stick_button}
            .whenBecomesTrue {
                if(NewTurret.goalTrackingActive)
                    NewTurret.stopTracking()
                else {
                    NewTurret.trackTarget()
                    Shooter.manualOffset = 0.0
                    ShooterAngle.manualOffset = 0.0
                }
            }
        // Switch alliance (works only in test mode where Teleop was started without Auto)
        button { gamepad2.right_stick_button }
            .toggleOnBecomesTrue()
            .whenBecomesTrue {
                if (testMode) {
                    PoseStorage.blueAlliance = false
//                    limelight.pipelineSwitch(2)
                }
            }
            .whenBecomesFalse {
                if (testMode) {
                    PoseStorage.blueAlliance = true
//                    limelight.pipelineSwitch(1)
                }
            }
    }

    override fun onUpdate() {
        BindingManager.update()
        driverControlled.update()
        follower.update()

        val now = System.nanoTime() / 1_000_000.0

        if(firstOnUpdate)
        {
            lastTelemetryTime = now
            lastLoopTime = now
            firstOnUpdate = false
            return
        }

        if(NewTurret.goalTrackingActive) {
            val shot = if (!PoseStorage.blueAlliance) BiLinearShooter.getShot(
                NewTurret.turretX,
                NewTurret.turretY
            )
            else BiLinearShooter.getShot(141.5 - NewTurret.turretX, NewTurret.turretY)
            //BiLinearShooter.getShot(NewTurret.newX, NewTurret.newY) // have this and line under in a button and onStart
            BiLinearShooter.applyShot(shot) // rather than in onUpdate
        }

        val telemetryTime = (now - lastTelemetryTime)
        val loopTime = (now - lastLoopTime)
        if(loopTime > maxLoopTime) maxLoopTime = loopTime
        if((loopTimeAverage < 0.5 * loopTime) || (loopTimeAverage > 1.5 * loopTime))
            loopTimeAverage = loopTime
        else
            loopTimeAverage = loopTimeAverage * 0.99 + loopTime * 0.01

        if(telemetryTime > TELEMETRY_INTERVAL )
        {
            telemetry.addData("LT", "Av: %.2f, Max: %.2f", loopTimeAverage, maxLoopTime)

            telemetry.addData("Pos", "(%.1f, %.1f, %.1f), Tur: (%.1f, %.1f)", follower.pose.x, follower.pose.y, Math.toDegrees(follower.heading), NewTurret.turretX, NewTurret.turretY)


            telemetry.addData("Shooter", "V: %.0f, T: %.0f, Offset: %.0f",Shooter.shooter.velocity, Shooter.target, Shooter.manualOffset)
            telemetry.addData("Turret", "F: %.1f, R: %.1f, S: %.3f",NewTurret.targetAngleField, NewTurret.targetAngleRobotRef, NewTurret.targetServoPosition)
        //    telemetry.addData("TurretEnc", "E: %.0f, A: %.1f, Err: %.1f",NewTurret.encoderDPosition(), NewTurret.encoderDAngle())
//            telemetry.addData("TurretAng", "Static: %.1f, AngV: %.1f, Err: %.1f", NewTurret.targetAngleStatic, NewTurret.targetAngleAV, NewTurret.encoderDAngle() - NewTurret.targetAngleRobotRef)
            telemetry.addData("Hood", "Pos: %.2f, Offset: %.2f", ShooterAngle.servo.position, ShooterAngle.manualOffset)

//            telemetry.addData("Spindexer", "D: %.0f (%.0f), A: %.0f (%.3f)", Spindexer.digEncLimitV(), Spindexer.digEncV(), Spindexer.absEncP(), Spindexer.absEncV())
            telemetry.addData("SpindexerTarget", "%.0f, Error: %.0f, Done: %b", Spindexer.targetPosition, Spindexer.targetPosition - Spindexer.digEncV(),
                Spindexer.targetReached)
//            telemetry.addData("SpindexerConst", "I1: %.0f, I2: %.0f, I3: %.0f", Spindexer.intakePos1, Spindexer.intakePos2, Spindexer.intakePos3)

            telemetry.addData(
                "Intake", "%s",// (Power: %+1.1f, Current: %3.2f mA)",
                if (intakeRunning) {
                    "Running"
                } else {
                    "Stopped"
                }//, intake.power, intake.motor.getCurrent(CurrentUnit.MILLIAMPS)
            )

//            telemetry.addData("analog", "%.0f", (Spindexer.analogS.voltage/3.225 * 4000.0))
            telemetry.addData("Full?", Spindexer.isFull)
//            telemetry.addData("S0 ", Spindexer.detectColorRGB(Spindexer.color0))
//            telemetry.addData("Alpha", "%.3f", Spindexer.color0.normalizedColors.alpha)
//            telemetry.addData("S1 ", Spindexer.detectColorRGB(Spindexer.color1))
//            telemetry.addData("Alpha", "%.3f", Spindexer.color1.normalizedColors.alpha)
//            telemetry.addData("S2 ", Spindexer.detectColorRGB(Spindexer.color2))
//            telemetry.addData("Alpha", "%.3f", Spindexer.color2.normalizedColors.alpha)

            telemetry.update()

            lastTelemetryTime = now
        }
        lastLoopTime = now
    }

    override fun onStop() {
        BindingManager.reset()
    }
}