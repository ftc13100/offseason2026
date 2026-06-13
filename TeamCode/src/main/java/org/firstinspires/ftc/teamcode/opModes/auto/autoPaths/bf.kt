
package org.firstinspires.ftc.teamcode.opModes.auto.blue

import SpindexerAuto
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.opModes.subsystems.Intake
import org.firstinspires.ftc.teamcode.opModes.subsystems.NewTurret
import org.firstinspires.ftc.teamcode.opModes.subsystems.PoseStorage
import org.firstinspires.ftc.teamcode.opModes.subsystems.Spindexer
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.Shooter
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.ShooterAngle
import org.firstinspires.ftc.teamcode.opModes.subsystems.shooter.TurretAuto
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@Autonomous(name = "bf")
class bf: NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(
                Shooter, ShooterAngle, Intake, PoseStorage,
                 SpindexerAuto, TurretAuto, NewTurret
            ),
            BulkReadComponent,
            PedroComponent(Constants::createFollower)
        )
    }

    val start = Pose(56.24784853700516, 8.247848537005176, Math.toRadians(180.0))

    val corn = Pose(16.0, 10.0, Math.toRadians(-160.0))
    val cornback = Pose(19.5, 10.0, Math.toRadians(180.0))

    val cornback2 = Pose(14.0, 10.0, Math.toRadians(200.0))


    val row = Pose(9.545094664371787, 35.25129087779688, Math.toRadians(180.0))

    val rowControl = Pose(47.66523235800346, 37.82530120481927, Math.toRadians(180.0))

    val sweepUp = Pose(10.0, 35.048192771084345, Math.toRadians(89.0))

    val sweepUpControl = Pose(8.69793459552496, 13.33304647160069, Math.toRadians(90.0))
    val sweepUpControl2 = Pose(4.821858864027539, 6.23493975903615, Math.toRadians(90.0))


    lateinit var startCorn : PathChain
    lateinit var path1 : PathChain

    lateinit var path2 : PathChain

    lateinit var cornStart : PathChain

    lateinit var startRow : PathChain
    lateinit var startSweep : PathChain

    lateinit var rowStart : PathChain
    lateinit var sweepDownStart : PathChain




    fun buildPaths() {

        startCorn = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(start, corn))
            .setLinearHeadingInterpolation(start.heading, corn.heading)
            .setNoDeceleration()
            .build()
        cornStart = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(cornback2, start))
            .setLinearHeadingInterpolation(corn.heading, start.heading)
            .build()
        path1 = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(corn, cornback))
            .setLinearHeadingInterpolation(corn.heading, cornback.heading)
            .build()
        path2 = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(cornback, cornback2))
            .setLinearHeadingInterpolation(cornback.heading, cornback2.heading)
            .build()
        startRow = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierCurve(start, rowControl, row))
            .setLinearHeadingInterpolation(start.heading, row.heading)
            .setNoDeceleration()
            .build()
        rowStart = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(row, start))
            .setLinearHeadingInterpolation(row.heading, start.heading)
            .build()
        startSweep = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierCurve(start, sweepUpControl, sweepUpControl2, sweepUp))
            .setTangentHeadingInterpolation()
            .build()

        sweepDownStart = PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(sweepUp, start))
            .setLinearHeadingInterpolation(sweepUp.heading, start.heading, 0.5)
            .build()
    }

    val autoRoutine: Command
        get() =
            SequentialGroup(
                ParallelGroup(
                    ShooterAngle.angle_ooga,
                    Shooter.spinAtSpeed(2100.0),
                ),
                Intake.spinFastAuto,
                SpindexerAuto.toShoot,
                ParallelGroup(
                    SpindexerAuto.toIntake,
                    FollowPath(startCorn),
                ),
                FollowPath(path1),
                FollowPath(path2),
                FollowPath(cornStart),
                SpindexerAuto.toShoot,
                ParallelGroup(
                    Intake.spinFastAuto,
                    SpindexerAuto.toIntake,
                   FollowPath(startRow, true, 0.7),
                ),
                FollowPath(rowStart),
                SpindexerAuto.toShoot,
                ParallelGroup(
                    Intake.spinFastAuto,
                    SpindexerAuto.toIntake,
                    FollowPath(startSweep)),
                FollowPath(sweepDownStart),
                SpindexerAuto.toShoot,
                ParallelGroup(
                    Intake.spinFastAuto,
                    SpindexerAuto.toIntake,
                    FollowPath(startSweep)),
                FollowPath(sweepDownStart),
                SpindexerAuto.toShoot,
                ParallelGroup(
                    ShooterAngle.angle_ooga,
                    Shooter.spinAtSpeed(2100.0),
                ),
                Intake.spinFastAuto,
                SpindexerAuto.toShoot,
                ParallelGroup(
                    SpindexerAuto.toIntake,
                    FollowPath(startCorn),
                ),
                FollowPath(path1),
                FollowPath(path2),
                FollowPath(cornStart),
                SpindexerAuto.toShoot,
                FollowPath(startCorn),
                )


    override fun onInit() {
        PedroComponent.Companion.follower.setMaxPower(1.0)
        Spindexer.toIntakePos

    }

    override fun onStartButtonPressed() {
        PedroComponent.Companion.follower.setStartingPose(start)
        buildPaths()
        PoseStorage.blueAlliance = true
        PoseStorage.redAlliance = false
        NewTurret.goalTrackingActive = true
        autoRoutine()
    }

    override fun onStop() {
        PoseStorage.poseEnd = PedroComponent.Companion.follower.pose
    }

    override fun onUpdate() {
        telemetry.addData("pos", "%.3f", SpindexerAuto.spindexer.currentPosition);

        telemetry.update()
    }

}
