package org.firstinspires.ftc.teamcode.opModes.teleOp.limelight//package org.firstinspires.ftc.teamcode.opModes.teleOp.limelight
//
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
//import kotlin.math.abs
//
//@TeleOp(name = "NextLimelight")
//class NextLimelight : NextFTCOpMode() {
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
//    private var isAlignmentModeActive: Boolean = false
//
//    private val ALIGNMENT_POWER: Double = 0.2
//
//    private val ALIGNMENT_TOLERANCE: Double = 1.5
//
//    override fun onInit() {
//        // Motors
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
//        button { gamepad1.b }
//            .whenBecomesTrue {
//                isAlignmentModeActive = !isAlignmentModeActive
//            }
//        // If want B to stop alignment when it becomes false (released), use a separate binding but here its the toggle
//    }
//
//    override fun onUpdate() {
//        BindingManager.update()
//
//        val result: LLResult? = limelight.latestResult
//
//        if (isAlignmentModeActive && result != null && result.isValid) {
//            // Limelight Turning Alignment Logic Bang-Bang
//            val tx: Double = result.tx
//            var turnPower: Double = 0.0
//
//            if (abs(tx) > ALIGNMENT_TOLERANCE) {
//                // Target is outside the tolerance, apply fixed power to turn
//                turnPower = if (tx > 0) {
//                    ALIGNMENT_POWER
//                } else {
//                    -ALIGNMENT_POWER
//                }
//            } else {
//                isAlignmentModeActive = false
//            }
//
//            // Keep the manual drive/strafe inputs
//            // Use !! for non-null assertion on gamepad values
//            val drivePower = Gamepads.gamepad1.leftStickY.get()
//            val strafePower = Gamepads.gamepad1.leftStickX.get()
//
//            // Set the motor power using the .power property
//            frontLeftMotor.power = drivePower + strafePower + turnPower
//            frontRightMotor.power = drivePower - strafePower - turnPower
//            backLeftMotor.power = drivePower - strafePower + turnPower
//            backRightMotor.power = drivePower + strafePower - turnPower
//
//            telemetry.addData("Align Mode", "Toggled Align ACTIVE")
//            telemetry.addData("tx Error", "%.2f", tx)
//            telemetry.addData("Turn Power", "%.2f", turnPower)
//
//        } else {
//            // Manual Control
//            driverControlled.update()
//
//            // If active but lost the target stop the motors
//            if (isAlignmentModeActive) {
//                frontLeftMotor.power = 0.0
//                frontRightMotor.power = 0.0
//                backLeftMotor.power = 0.0
//                backRightMotor.power = 0.0
//                telemetry.addData("Align Mode", "Toggled Align LOST TARGET")
//            } else {
//                telemetry.addData("Align Mode", "Manual")
//            }
//        }
//
//        if (result != null && result.isValid) {
//            val botpose: Pose3D = result.botpose
//            telemetry.addData("tx (Horizontal Error)", "%.2f", result.tx)
//            telemetry.addData("ty (Vertical Error)", "%.2f", result.ty)
//            telemetry.addData("Botpose", botpose.toString())
//        } else {
//            telemetry.addData("Limelight", "Target not found")
//        }
//        telemetry.addData("Mode", "TeleOp Running")
//        telemetry.update()
//
//    }
//
//    override fun onStop() {
//        BindingManager.reset()
//    }
//}