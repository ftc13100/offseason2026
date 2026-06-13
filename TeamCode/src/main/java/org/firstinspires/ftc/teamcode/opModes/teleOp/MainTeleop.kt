//package org.firstinspires.ftc.teamcode.opModes.teleOp
//
//import com.bylazar.telemetry.JoinedTelemetry
//import com.bylazar.telemetry.PanelsTelemetry
//import com.pedropathing.geometry.Pose
//import com.qualcomm.hardware.limelightvision.LLResult
//import com.qualcomm.hardware.limelightvision.Limelight3A
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp
//import com.qualcomm.robotcore.hardware.DcMotor
//import dev.nextftc.bindings.BindingManager
//import dev.nextftc.bindings.button
//import dev.nextftc.core.commands.CommandManager
//import dev.nextftc.core.commands.delays.WaitUntil
//import dev.nextftc.core.commands.groups.SequentialGroup
//import dev.nextftc.core.commands.utility.InstantCommand
//import dev.nextftc.core.components.BindingsComponent
//import dev.nextftc.core.components.SubsystemComponent
//import dev.nextftc.extensions.pedro.PedroComponent
//import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
//import dev.nextftc.ftc.Gamepads
//import dev.nextftc.ftc.NextFTCOpMode
//import dev.nextftc.ftc.components.BulkReadComponent
//import dev.nextftc.hardware.driving.MecanumDriverControlled
//import dev.nextftc.hardware.impl.MotorEx
//import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Gate
//import org.firstinspires.ftc.teamcode.opModes.subsystems.GoalFinder
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake.intake
//import org.firstinspires.ftc.teamcode.opModes.subsystems.PoseStorage
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Turret
//import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
//import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.ShooterAngle
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants
//import kotlin.math.abs
//
//@TeleOp(name = "MainTeleop")
//class MainTeleop : NextFTCOpMode() {
//    init {
//        addComponents(
//            SubsystemComponent(
//                ShooterAngle, Shooter, Gate, Intake, Turret, PoseStorage, GoalFinder
//            ),
//            BindingsComponent,
//            BulkReadComponent,
//            PedroComponent(Constants::createFollower)
//        )
//    }
//
//    private val frontLeftName = "frontLeft"
//    private val frontRightName = "frontRight"
//    private val backLeftName = "backLeft"
//    private val backRightName = "backRight"
//
//    private val shooterController = ShooterController
//
//    private lateinit var frontLeftMotor: MotorEx
//    private lateinit var frontRightMotor: MotorEx
//    private lateinit var backLeftMotor: MotorEx
//    private lateinit var backRightMotor: MotorEx
//
//    private lateinit var driverControlled: MecanumDriverControlled
//
//    lateinit var limelight: Limelight3A
//
//    private val startPose = PoseStorage.poseEnd  // Pose(72.0,72.0, Math.toRadians(90.0))
//    private val testingPose = Pose(72.0, 72.0, Math.toRadians(90.0))
//    private var testMode = false
//    private var currentShotDistance = 0.0
//    private var currentShotVelocity = 0.0
//    private var currentShotAngle = 0.0
//    private var gateOpen = false
//    private var intakeRunning = false
//    private var initialized = false
//
//    override fun onInit() {
//        if (abs(startPose.x) < 0.1 && abs(startPose.y) < 0.1) {
//            follower.setStartingPose(testingPose)
//            PoseStorage.blueAlliance = true
//            testMode = true
//        } else {
//            follower.setStartingPose(startPose)
//        }
//
//        frontLeftMotor = MotorEx(frontLeftName)
//        frontRightMotor = MotorEx(frontRightName)
//        backLeftMotor = MotorEx(backLeftName)
//        backRightMotor = MotorEx(backRightName)
//
//        listOf(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor).forEach {
//            it.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
//        }
//
//        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
//        telemetry.msTransmissionInterval = 11
//        limelight.pipelineSwitch(1)
//        limelight.start()
//        follower.update()
//
//        telemetry = JoinedTelemetry(telemetry, PanelsTelemetry.ftcTelemetry)
//        //Gate.gate_close()
//    }
//
//    override fun onStartButtonPressed() {
//        driverControlled = MecanumDriverControlled(
//            frontLeftMotor,
//            frontRightMotor,
//            backLeftMotor,
//            backRightMotor,
//            -Gamepads.gamepad1.leftStickY,
//            Gamepads.gamepad1.leftStickX,
//            Gamepads.gamepad1.rightStickX,
////            FieldCentric(imu)
//        )
//        driverControlled.scalar = 0.95
//        Shooter.stallShooter()
//
//////////////////////////////////////////////////////////////////////////////
////        GamePad 2 - Operator Commands
//////////////////////////////////////////////////////////////////////////////
//        //Intake artifact
//        button { gamepad1.left_bumper }
//            .toggleOnBecomesTrue()
//            .whenBecomesTrue {
//                Gate.gate_stop()
//                Intake.spinFast()
//                intakeRunning = true
//                gateOpen = false
//            }
//            .whenBecomesFalse {
//                Intake.spinStop()
//                intakeRunning = false
//            }
//
////        button { gamepad1.x }
////            .whenBecomesTrue {
////               if(PoseStorage.blueAlliance == true){
////                   Park = follower.pathBuilder()
////                       .addPath(BezierLine(,shootPose))
////                       .setLinearHeadingInterpolation(startPose.heading, shootPose.heading)
////                       .build()
////               }
////            }
//
//        //Outtake artifact
//        button { gamepad1.right_bumper }
//            .toggleOnBecomesTrue()
//            .whenBecomesTrue {
//                Gate.gate_stop()
//                Intake.spinReverse()
//                intakeRunning = true
//                gateOpen = false
//            }
//            .whenBecomesFalse {
//                Intake.spinStop()
//                intakeRunning = false
//            }
//        // Point to Target
//        button { gamepad1.a }
//            .whenBecomesTrue {
//                if (!GoalFinder.gfActive) {
//                    GoalFinder.findGoal()
//                } else {
//                    GoalFinder.stop()
//                }
//            }
//
//        // Drivetrain Slow-fast speed
//        button { gamepad1.y }
//            .whenTrue { driverControlled.scalar = 0.4 }
//            .whenFalse { driverControlled.scalar = 0.95 }
//
//        // Reset location and heading
//        Gamepads.gamepad1.leftTrigger.asButton { it > 0.5 } and Gamepads.gamepad1.rightTrigger.asButton { it > 0.5 }
//            .whenBecomesTrue {
//                if (PoseStorage.blueAlliance) {
//                    follower.pose = Pose(135.75, 8.5, Math.toRadians(-90.0))
//                } else {
//                    follower.pose = Pose(8.25, 8.5, Math.toRadians(-90.0))
//                }
//            }
//
//
//////////////////////////////////////////////////////////////////////////////
////        GamePad 2 - Operator Commands
//////////////////////////////////////////////////////////////////////////////
//
//        // Fine jump turret right
//        button { gamepad2.right_bumper }
//            .whenTrue {
//                Turret.turn(50.0)
//            }
//
//
//        // Fine jump turret left
//        button { gamepad2.left_bumper }
//            .whenTrue {
//                Turret.turn(-50.0)
//            }
//
//        // Coarse jump turret right
//        button { gamepad2.right_trigger > 0.5 }
//            .whenBecomesTrue {
//                Turret.turn(500.0)
//            }
//
//        // Coarse jump turret left
//        button { gamepad2.left_trigger > 0.5 }
//            .whenBecomesTrue {
//                Turret.turn(-500.0)
//            }
//
//        // Turret Tracking
//        button { gamepad2.a }
//            .whenBecomesTrue {
//                if (Turret.goalTrackingActive) {
//                    Turret.resetToStartPosition()
//                } else {
//                    Turret.trackTarget()
//                }
//            }
//
//        button { gamepad2.x }
//            .whenBecomesTrue {
//                Gate.gate_in()
//                gateOpen = true
//            }
//// Start shooter and set hood angle / Stop shooter
//        button { gamepad2.y }
//            .toggleOnBecomesTrue()
//            .whenBecomesTrue {
//                val currentShot = shooterController.getShot(GoalFinder.gfGoalDistance)
//
//                val commands = SequentialGroup(
//                    WaitUntil { currentShot != null },
//                    InstantCommand {
//                        currentShotVelocity = currentShot!!.velocity
//                        currentShotAngle = currentShot.angle
//                        currentShotDistance = currentShot.distance
//                        shooterController.applyShot(currentShot)
//                    },
//                    WaitUntil { Shooter.shooterReady && GoalFinder.gfReady },
//                    InstantCommand {
//                        Gate.gate_in()
//                        Intake.spinSlowSpeed()
//                        gateOpen = true
//                        intakeRunning = true
//                    }
//                )
//                commands()
//            }
//
//            .whenBecomesFalse {
//                Shooter.stallShooter()
//                Gate.gate_stop()
//                Intake.spinStop()
//                Turret.trackTarget()
//                gateOpen = false
//                intakeRunning = false
//            }
//
//        button { gamepad2.b }
//            .whenBecomesTrue {
//                if (GoalFinder.gfLLValid) {
//                    Turret.turn(GoalFinder.gfTurretAdj)
//                }
//            }
//
//        // Increase shooter velocity
//        button { gamepad2.dpad_up }
//            .whenBecomesTrue {
//                if (currentShotVelocity < 1800) {
//                    currentShotVelocity += 10.0
//                    Shooter.spinAtSpeed(currentShotVelocity).schedule()
//                }
//            }
//
//        // Decrease shooter velocity
//        button { gamepad2.dpad_down }
//            .whenBecomesTrue {
//                if (currentShotVelocity > 800) {
//                    currentShotVelocity -= 10.0
//                    Shooter.spinAtSpeed(currentShotVelocity).schedule()
//                }
//            }
//
//        // lower shooting hood
//        button { gamepad2.dpad_left }
//            .whenBecomesTrue {
//                if (currentShotAngle > 0.0 && currentShotAngle <= 0.681) {
//                    currentShotAngle += 0.02
//                    ShooterAngle.targetServoPosition = currentShotAngle
//                    CommandManager.scheduleCommand(
//                        ShooterAngle.update()
//                    )
//                }
//            }
//
//        // raise shooting hood
//        button { gamepad2.dpad_right }
//            .whenBecomesTrue {
//                if (currentShotAngle > 0.0 && currentShotAngle >= 0.519) {
//                    currentShotAngle -= 0.02
//                    ShooterAngle.targetServoPosition = currentShotAngle
//                    CommandManager.scheduleCommand(
//                        ShooterAngle.update()
//                    )
//                }
//            }
//
//        // Switch alliance (works only in test mode where Teleop was started without Auto)
//        button { gamepad2.left_stick_button }
//            .toggleOnBecomesTrue()
//            .whenBecomesTrue {
//                if (testMode) {
//                    PoseStorage.blueAlliance = false
//                    limelight.pipelineSwitch(2)
//                }
//            }
//            .whenBecomesFalse {
//                if (testMode) {
//                    PoseStorage.blueAlliance = true
//                    limelight.pipelineSwitch(1)
//                }
//            }
//
//        button { gamepad2.right_stick_button }
//            .whenBecomesTrue {
//                Turret.resetToStartPosition()
//            }
//
//    }
//
////    @JvmField
////    var llAveX : Double = 0.0
////
////    @JvmField
////    var llAveY : Double = 0.0
////
////    @JvmField
////    var llAveH : Double = 0.0
////
////    @JvmField
////    val LL_AVE_COEFF = 0.99
//
//    override fun onUpdate() {
//        BindingManager.update()
//        follower.update()
//
//        driverControlled.update()
//
//        if (!initialized) {
//            Turret.initPos()
//            initialized = true
//        }
//
//        val llResult: LLResult? = limelight.latestResult
//        val turnPower = GoalFinder.calculate(
//            follower.pose,
//            llResult,
//            PoseStorage.blueAlliance
//        )
//
//        if (GoalFinder.gfActive) {
//            frontLeftMotor.power = turnPower
//            frontRightMotor.power = -turnPower
//            backLeftMotor.power = turnPower
//            backRightMotor.power = -turnPower
//        } else {
//            // Manual Control
//            driverControlled.update()
//        } // end tracking goalClose
//
//        if (PoseStorage.blueAlliance) {
//            telemetry.addData("Alliance", "BLUE")
//        } else {
//            telemetry.addData("Alliance", "RED")
//        }
//
////
////        var llBotpose = Pose(Double.NaN, Double.NaN, Double.NaN)
////        var llTx = Double.NaN
////
////        if (llResult != null && llResult.isValid) {
////            // botpose gives in meters with 0,0 at center of field.
////            val position = llResult.botpose.position.toUnit(DistanceUnit.INCH)
////            val orientation = llResult.botpose.orientation
////
////            llBotpose =
////                FTCCoordinates.INSTANCE.convertToPedro(
////                    Pose(
////                        position.x,
////                        position.y,
////                        Math.toRadians(orientation.yaw + Turret.turretAzDeg()),
////                    )
////                )
////
////            llAveX = llAveX * LL_AVE_COEFF + llBotpose.x * (1 - LL_AVE_COEFF)
////            llAveY = llAveY * LL_AVE_COEFF + llBotpose.y * (1 - LL_AVE_COEFF)
////            llAveH = llAveH * LL_AVE_COEFF + llBotpose.heading * (1 - LL_AVE_COEFF)
////
////            llTx = llResult.tx
////        }
//
//        telemetry.addData(
//            "X",
//            "%3.1f, Y: %3.1f, Heading: %3.1f, Dist: %3.1f",
//            follower.pose.x,
//            follower.pose.y,
//            Math.toDegrees(follower.heading),
//            GoalFinder.gfGoalDistance
//        )
//
////        telemetry.addData(
////            "LL",
////            "Tx: %3.1f, X: %3.1f, Y: %3.1f, Heading: %3.1f",
////            llTx,
////            llAveX,
////            llAveY,
////            llAveH
////        )
//
//        telemetry.addData(
//            "Goal",
//            "%+3.1f, Error: %+3.1f, Ready: %b",
//            Math.toDegrees(GoalFinder.gfTargetAngle),
//            Turret.turretErrorTicks,
//            GoalFinder.gfReady
//        )
//        telemetry.addData(
//            "Turret",
//            "Pos: %4.0f, Az: %4.1f, Tracking: %b, Start: %.1f, %b",
//            Turret.turretCurrentPos,
//            Turret.turretAzDeg(),
//            Turret.goalTrackingActive,
//            Turret.startPosition,
//            initialized
//        )
//
//        telemetry.addData("Current Shot", "Dist: %3.1f, Vel: %4.1f, Ang: %.3f",
//            currentShotDistance, currentShotVelocity, currentShotAngle)
//
//        telemetry.addData(
//            "Shooter Vel",
//            "%4.0f, Ready: %s, Error: %4.0f",
//            Shooter.target,
//            if (Shooter.shooterReady) {
//                "Yes"
//            } else {
//                "No"
//            },
//            Shooter.target - Shooter.shooter.velocity,
//        )
//
//        telemetry.addData("Shooter Angle", "%.3f", currentShotAngle)
//        telemetry.addData(
//            "Gate", "%s", if (gateOpen) {
//                "Open"
//            } else {
//                "Closed"
//            }
//        )
//        telemetry.addData(
//            "Intake", "%s (Power: %+1.1f, Current: %3.2f mA)", if (intakeRunning) {
//                "Running"
//            } else {
//                "Stopped"
//            }, intake.power, intake.motor.getCurrent(CurrentUnit.MILLIAMPS)
//        )
//        telemetry.update()
//    }
//
//    override fun onStop() {
//        BindingManager.reset()
//        Shooter.stopShooter()
//        Turret.resetToStartPosition()
//        PoseStorage.poseEnd = follower.pose
//    }
//}