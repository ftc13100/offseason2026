package org.firstinspires.ftc.teamcode.opModes.teleOp.limelight//package org.firstinspires.ftc.teamcode.opModes.teleOp.limelight
//
//import com.acmerobotics.dashboard.FtcDashboard
//import com.acmerobotics.dashboard.config.Config
//import com.acmerobotics.dashboard.telemetry.MultipleTelemetry
//import com.qualcomm.hardware.limelightvision.LLResult
//import com.qualcomm.hardware.limelightvision.Limelight3A
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp
//import com.qualcomm.robotcore.hardware.DcMotor
//import dev.nextftc.bindings.BindingManager
//import dev.nextftc.bindings.button
//import dev.nextftc.core.components.BindingsComponent
//import dev.nextftc.core.components.SubsystemComponent
//import dev.nextftc.ftc.Gamepads
//import dev.nextftc.ftc.NextFTCOpMode
//import dev.nextftc.ftc.components.BulkReadComponent
//import dev.nextftc.hardware.driving.MecanumDriverControlled
//import dev.nextftc.hardware.impl.MotorEx
//import org.firstinspires.ftc.robotcore.external.navigation.Pose3D
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake.intake
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake.spinSlowSpeed
//import kotlin.math.abs
//
//@Config
//@TeleOp(name = "LimeLightTelemetry")
//class LimeLightTelemetryClean : NextFTCOpMode() {
//
//    init {
//        addComponents(
//            SubsystemComponent(),
//            BindingsComponent,
//            BulkReadComponent,
//        )
//    }
//
//    private val frontLeftName = "frontLeft"
//    private val frontRightName = "frontRight"
//    private val backLeftName = "backLeft"
//    private val backRightName = "backRight"
//
//    private lateinit var frontLeftMotor: MotorEx
//    private lateinit var frontRightMotor: MotorEx
//    private lateinit var backLeftMotor: MotorEx
//    private lateinit var backRightMotor: MotorEx
//
//    private lateinit var driverControlled: MecanumDriverControlled
//
//    private lateinit var limelight: Limelight3A
//
//    // Optional: simple alignment state if you want to add later
//    private var isAlignmentModeActive = false
//    private val ALIGNMENT_POWER = 0.10
//    private val ALIGNMENT_TOLERANCE = 0.9
//
//    override fun onInit() {
//        telemetry = MultipleTelemetry(FtcDashboard.getInstance().telemetry, telemetry)
//
//        frontLeftMotor = MotorEx(frontLeftName).reversed()
//        frontRightMotor = MotorEx(frontRightName)
//        backLeftMotor = MotorEx(backLeftName).reversed()
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
//
//        // NOTE: Pedro Pathing follower removed from this TeleOp to avoid motor conflicts.
//        // If you want odometry from Pedro Pathing, re-integrate a localizer-only object
//        // or call the follower in a read-only way that does not write motor outputs.
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
//            Gamepads.gamepad1.rightStickX
//        )
//        driverControlled.scalar = 1.0
//
//        button { gamepad1.y }
//            .toggleOnBecomesTrue()
//            .whenBecomesTrue { driverControlled.scalar = 0.4 }
//            .whenBecomesFalse { driverControlled.scalar = 1.0 }
//
//        // Example: use gamepad1.b to toggle a very simple Limelight alignment mode.
//        button { gamepad1.b }
//            .whenBecomesTrue {
//                isAlignmentModeActive = !isAlignmentModeActive
//            }
//
//        button { gamepad1.x }
//            .whenBecomesTrue {
//                intake.power = 0.7
//            }
//            .whenBecomesFalse {
//                intake.power = 0.0
//            }
//
//    }
//
//    override fun onUpdate() {
//        BindingManager.update()
//
//        val result: LLResult? = limelight.latestResult
//
//        if (isAlignmentModeActive && result != null && result.isValid) {
//            // simple bang-bang turning (keeps drive/strafe from sticks)
//            val tx = result.tx
//            var turnPower = 0.0
//            if (abs(tx) > ALIGNMENT_TOLERANCE) {
//                turnPower = if (tx > 0) ALIGNMENT_POWER else -ALIGNMENT_POWER
//            } else {
//                isAlignmentModeActive = false
//                turnPower = 0.0
//            }
//
//            // keep driver-controlled inputs but add turnPower
//            val drivePower = Gamepads.gamepad1.leftStickY.get()
//            val strafePower = Gamepads.gamepad1.leftStickX.get()
//
//            frontLeftMotor.power = drivePower + strafePower + turnPower
//            frontRightMotor.power = drivePower - strafePower - turnPower
//            backLeftMotor.power = drivePower - strafePower + turnPower
//            backRightMotor.power = drivePower + strafePower - turnPower
//
//            telemetry.addData("Align Mode", "ACTIVE")
//            telemetry.addData("tx Error", "%.2f", tx)
//            telemetry.addData("Turn Power", "%.2f", turnPower)
//        } else {
//            // Normal manual driving via NextFTC MecanumDriverControlled
//            driverControlled.update()
//
//            if (isAlignmentModeActive && (result == null || !result.isValid)) {
//                telemetry.addData("Align Mode", "ACTIVE - NO TARGET")
//                // optionally stop motors if you want to hold position while searching
//                // frontLeftMotor.power = 0.0 ; frontRightMotor.power = 0.0 ; backLeftMotor.power = 0.0 ; backRightMotor.power = 0.0
//            } else {
//                telemetry.addData("Align Mode", "Manual")
//            }
//        }
//
//        // Limelight telemetry
//        if (result != null && result.isValid) {
//            val botpose: Pose3D = result.botpose
//            telemetry.addData("tx (Horizontal Error)", "%.2f", result.tx)
//            telemetry.addData("ty (Vertical Error)", "%.2f", result.ty)
//            telemetry.addData("Botpose", botpose.toString())
//        } else {
//            telemetry.addData("Limelight", "Target not found")
//        }
//
//        telemetry.addData("Mode", "TeleOp Running")
//        telemetry.update()
//    }
//
//    override fun onStop() {
//        BindingManager.reset()
//    }
//}
