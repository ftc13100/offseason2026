package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import dev.nextftc.bindings.button
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.opModes.subsystems.NewTurret
import org.firstinspires.ftc.teamcode.opModes.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp(name = "Turret Test & Tune")
class TurretTeleOp : NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(NewTurret),
            BindingsComponent,
            BulkReadComponent,
            PedroComponent(Constants::createFollower)
        )
    }

    private val panelsTelemetry = PanelsTelemetry.telemetry

    private val frontLeftName = "frontLeft"
    private val frontRightName = "frontRight"
    private val backLeftName = "backLeft"
    private val backRightName = "backRight"

    private lateinit var frontLeftMotor: MotorEx
    private lateinit var frontRightMotor: MotorEx
    private lateinit var backLeftMotor: MotorEx
    private lateinit var backRightMotor: MotorEx

    private lateinit var driverControlled: MecanumDriverControlled

    override fun onInit() {
        NewTurret.stopTracking()
        NewTurret.toAngle(180.0)
        frontLeftMotor = MotorEx(frontLeftName)
        frontRightMotor = MotorEx(frontRightName)
        backLeftMotor = MotorEx(backLeftName)
        backRightMotor = MotorEx(backRightName)

        listOf(frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor).forEach {
            it.motor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        }
        follower.update()
    }

    override fun onStartButtonPressed() {
        //NewTurret.backRightMotor.atPosition(6000.0)
        driverControlled = MecanumDriverControlled(
            frontLeftMotor,
            frontRightMotor,
            backLeftMotor,
            backRightMotor,
            -Gamepads.gamepad1.leftStickY,
            Gamepads.gamepad1.leftStickX,
            Gamepads.gamepad1.rightStickX,
//            mode = FieldCentric { follower.pose.heading.rad }
        )
        driverControlled.scalar = 1.0

        button { gamepad1.x }
            .whenBecomesTrue {
                NewTurret.trackTarget()
            }

    }

    override fun onUpdate() {
        driverControlled.update()
        // Show current turret position
        telemetry.addData("X", follower.pose.x)
        telemetry.addData("Y", follower.pose.y)
        telemetry.addData("turretX", NewTurret.turretX)
        telemetry.addData("turretY", NewTurret.turretY)
        panelsTelemetry.addData("Angular Vel", follower.angularVelocity)
        panelsTelemetry.addData("ff", follower.angularVelocity * NewTurret.kVF)
        panelsTelemetry.addData("target pos", NewTurret.targetAngleStatic)
        //panelsTelemetry.addData("encoder", (360.0 - NewTurret.backRightMotor.currentPosition * (360.0/12000.0)))

        // Update panel and telemetry
        panelsTelemetry.update(telemetry)
        telemetry.update()
    }
}