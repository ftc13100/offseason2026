package org.firstinspires.ftc.teamcode.opModes.teleOp//package org.firstinspires.ftc.teamcode.opModes.teleOp
//
//import com.qualcomm.hardware.limelightvision.LLResult
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp
//import dev.nextftc.core.components.BindingsComponent
//import dev.nextftc.core.components.SubsystemComponent
//import dev.nextftc.ftc.Gamepads
//import dev.nextftc.ftc.NextFTCOpMode
//import dev.nextftc.ftc.components.BulkReadComponent
//import dev.nextftc.hardware.driving.MecanumDriverControlled
//import dev.nextftc.hardware.impl.MotorEx
//import org.firstinspires.ftc.teamcode.opModes.subsystems.LimeLight.blueLime
//import org.firstinspires.ftc.teamcode.opModes.subsystems.LimeLight.blueLime.limelight
//import org.firstinspires.ftc.teamcode.opModes.subsystems.LimeLight.redLime
//import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
//import com.qualcomm.hardware.limelightvision.Limelight3A
//import com.qualcomm.robotcore.hardware.DcMotor
//import dev.nextftc.bindings.BindingManager
//import dev.nextftc.bindings.button
//import org.firstinspires.ftc.robotcore.external.navigation.Pose3D
//import kotlin.math.abs
//
//@TeleOp(name = "Tele")
//class Tele : NextFTCOpMode() {
////    init {
////        addComponents(
////            SubsystemComponent(turret, Shooter, blueLime),
////            BulkReadComponent,
////            BindingsComponent
////        )
////    }
////
////    private val frontLeftName = "frontLeft"
////    private val frontRightName = "frontRight"
////    private val backLeftName = "backLeft"
////    private val backRightName = "backRight"
////
////    private lateinit var frontLeftMotor: MotorEx
////    private lateinit var frontRightMotor: MotorEx
////    private lateinit var backLeftMotor: MotorEx
////    private lateinit var backRightMotor: MotorEx
////
////    private lateinit var driverControlled: MecanumDriverControlled
////
////    private lateinit var limelight: Limelight3A
////
////    override fun onInit() {
////        // Motors
////        frontLeftMotor = MotorEx(frontLeftName).reversed()
////        frontRightMotor = MotorEx(frontRightName)
////        backLeftMotor = MotorEx(backLeftName).reversed()
////        backRightMotor = MotorEx(backRightName)
////
////        listOf(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor).forEach {
////            it.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
////        }
////
////        // Limelight init
////        limelight = hardwareMap.get(Limelight3A::class.java, "limelight")
////        telemetry.msTransmissionInterval = 11
////        limelight.pipelineSwitch(1)
////        limelight.start()
////    }
////
////    override fun onStartButtonPressed() {
////        driverControlled = MecanumDriverControlled(
////            frontLeftMotor,
////            frontRightMotor,
////            backLeftMotor,
////            backRightMotor,
////            -Gamepads.gamepad1.leftStickY,
////            Gamepads.gamepad1.leftStickX,
////            Gamepads.gamepad1.rightStickX
////        )
////        driverControlled.scalar = 1.0
////
////        button { gamepad1.y }
////            .toggleOnBecomesTrue()
////            .whenBecomesTrue { driverControlled.scalar = 0.4 }
////            .whenBecomesFalse { driverControlled.scalar = 1.0 }
////
//////        button { gamepad1.right_bumper }
//////            .whenTrue {
//////                turret.toRight()
//////            }
//////            .whenFalse {
//////                turret.spinZero()
//////            }
//////
//////        button { gamepad1.left_bumper }
//////            .whenTrue {
//////                turret.toLeft()
//////            }
//////            .whenFalse {
//////                turret.spinZero()
//////            }
////    }
////
////    override fun onUpdate() {
////        BindingManager.update()
////
////        val result: LLResult? = limelight.latestResult
////
////        driverControlled.update()
////
////        if (result != null && result.isValid) {
////            //val botpose: Pose3D = result.botpose
////            telemetry.addData("tx (Horizontal Error)", "%.2f", result.tx)
////            telemetry.addData("ty (Vertical Error)", "%.2f", result.ty)
////            //telemetry.addData("Botpose", botpose.toString())
////
//////        if ( -4.0 < result.tx && result.tx < 4.0) {
//////                turret.spinZero()
//////           } else if (-4.0 > result.tx){
//////               turret.spinLeft()
//////           } else if (result.tx > 4.0) {
//////               turret.spinRight()
//////        }
////
////        } else {
////            telemetry.addData("Limelight", "Target not found")
////           // turret.stop()
////        }
////
////        telemetry.addData("Mode", "TeleOp Running")
////        telemetry.update()
////
////    }
//}