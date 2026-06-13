//package org.firstinspires.ftc.teamcode.opModes.teleOp//package org.firstinspires.ftc.teamcode.opModes.teleOp
//
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp
//import dev.nextftc.core.commands.utility.LambdaCommand
//import dev.nextftc.core.commands.utility.PerpetualCommand
//import dev.nextftc.core.components.SubsystemComponent
//import dev.nextftc.ftc.NextFTCOpMode
//import dev.nextftc.ftc.components.BulkReadComponent
//import com.bylazar.telemetry.PanelsTelemetry
//import com.qualcomm.robotcore.util.ElapsedTime
//import dev.nextftc.extensions.pedro.PedroComponent
//import org.firstinspires.ftc.teamcode.opModes.subsystems.NewTurret
//import org.firstinspires.ftc.teamcode.opModes.subsystems.Turret
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants
//
//@TeleOp(name = "TurretTele")
//class NewTurretTrack : NextFTCOpMode() {
//
//    init {
//        addComponents(
//            SubsystemComponent(NewTurret),
//            BulkReadComponent,
//            PedroComponent(Constants::createFollower)
//        )
//    }
//
//    private val panelsTelemetry = PanelsTelemetry.telemetry
//
//    private val timer = ElapsedTime()
//    val turretCommand = PerpetualCommand(
//        LambdaCommand()
//            .setUpdate {
//                NewTurret.turretTrackCommand()
//            }
//            .requires(NewTurret)
//    )
//
//    override fun onInit() {
//        turretCommand()
//        timer.reset()
//    }
//
//    override fun onUpdate() {
//        telemetry.addData("Target", NewTurret.turretState)
//        telemetry.addData("Angle", NewTurret.turretAngle)
//        panelsTelemetry.addData("Target", Turret.target)
//        panelsTelemetry.addData("Position", Turret.turret.currentPosition)
//        panelsTelemetry.update(telemetry)
//        telemetry.update()
//    }
//}
//
//
