package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.bylazar.telemetry.PanelsTelemetry
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.commands.utility.PerpetualCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake
import org.firstinspires.ftc.teamcode.opModes.subsystems.Spindexer

@TeleOp(name = "SpindexerTesting")
class SpindexerTele : NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(
                 Spindexer //, Gate, Intake
            ),
            BindingsComponent,
            BulkReadComponent,
        )
    }

    private val panelsTelemetry = PanelsTelemetry.telemetry
    private val timer = ElapsedTime()

    //tuning only
    val spindexCommand = PerpetualCommand(
        LambdaCommand()
            .setUpdate {
                Spindexer.spin()
            }
            .requires(Spindexer)
    )

    override fun onInit() {
        spindexCommand()
        Intake.spinSlowSpeed()()
        timer.reset()
    }

//    override fun onStartButtonPressed() {
//        button { gamepad2.left_bumper }
//            .whenBecomesTrue {
//                Gate.gate_in()
//                Intake.spinFast()
//            }
//            .whenBecomesFalse {
//                Gate.gate_stop()
//                Intake.spinStop()
//            }
//
//        button { gamepad2.x }
//            .whenBecomesTrue(Spindexer.autoIndex(0))
//
//        // Button Y represents b3 = 1
//        button { gamepad2.y }
//            .whenBecomesTrue(Spindexer.autoIndex(1))
//
//        // Button B represents b3 = 2
//        button { gamepad2.b }
//            .whenBecomesTrue(Spindexer.autoIndex(2))
//
//        button { gamepad2.a }
//            .whenBecomesTrue {
//                Spindexer.spinShot()
//            }
//            .whenBecomesFalse {
//                Spindexer.stopShot()
//            }
//    }

    override fun onUpdate() {
        updateSignals()
    }

    private fun updateSignals() {
        //telemetry.addData("Dexer Position", Spindexer.spindexer.currentPosition)
        panelsTelemetry.addData("Position", Spindexer.spindexer.currentPosition)
        panelsTelemetry.addData("Target", Spindexer.target)

        telemetry.addData("Spindexer", Spindexer.spindexer.motor.getCurrent(CurrentUnit.MILLIAMPS))
        telemetry.addData("intake", Intake.intake.motor.getCurrent(CurrentUnit.MILLIAMPS))


        telemetry.addData("Dexer Pos", Spindexer.spindexer.currentPosition)
        telemetry.addData("S0 ", Spindexer.detectColorRGB(Spindexer.color0))
        telemetry.addData("S1 ", Spindexer.detectColorRGB(Spindexer.color1))
        telemetry.addData("S2 ", Spindexer.detectColorRGB(Spindexer.color2))
        telemetry.update()
        panelsTelemetry.update(telemetry)

    }
}