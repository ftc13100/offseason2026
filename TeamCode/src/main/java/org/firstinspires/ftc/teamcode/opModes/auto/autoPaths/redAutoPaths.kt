package org.firstinspires.ftc.teamcode.opModes.auto.autoPaths

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathChain
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent

object redAutoPaths : Subsystem {
    val start = Pose(19.3, 121.6, Math.toRadians(140.0)).mirror()
    val shoot = Pose(56.0, 84.0, Math.toRadians(-131.0)).mirror()

    val PGP = Pose(8.5, 58.0, Math.toRadians(-174.0)).mirror()
    val PGPcontrol = Pose(41.17, 65.82).mirror()
    val PGPback = Pose(56.0, 84.0, Math.toRadians(-155.0)).mirror()

    val gate = Pose(6.1, 59.5, Math.toRadians(148.0)).mirror()
    val eat = Pose(12.0, 59.5,  Math.toRadians(148.0)).mirror()
    val gateBack = Pose(58.0, 84.0, Math.toRadians(-147.0)).mirror()

    val PPG = Pose(19.0, 84.0, Math.toRadians(180.0)).mirror()
    val leave = Pose(59.46987951807229,115.07056798623064, Math.toRadians(-90.0)).mirror()




    lateinit var startShoot : PathChain
    lateinit var shootPGP: PathChain
    lateinit var PGPshoot: PathChain
    lateinit var shootGate: PathChain
    lateinit var shootPPG: PathChain

    lateinit var gateEat: PathChain
    lateinit var eatShoot: PathChain
    lateinit var goLeave: PathChain

    lateinit var PPGshoot: PathChain


    fun buildPaths() {

        startShoot= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(start, shoot))
            .setLinearHeadingInterpolation(start.heading, shoot.heading)
            .build()
        shootPGP= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierCurve(shoot, PGPcontrol, PGP))
            .setTangentHeadingInterpolation()
            .build()
        PGPshoot= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierCurve(PGP, PGPcontrol, PGPback))
            .setLinearHeadingInterpolation(PGP.heading, PGPback.heading)
            .build()
        shootGate= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierCurve(PGPback, PGPcontrol, gate))
            .setLinearHeadingInterpolation(PGPback.heading, gate.heading, 0.5)
            .build()
        eatShoot= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierCurve(gate, PGPcontrol, gateBack))
            .setTangentHeadingInterpolation()
            .setReversed()
            .build()
        shootPPG= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(gateBack, PPG))
            .setTangentHeadingInterpolation()
            .setNoDeceleration()
            .build()
        PPGshoot= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(PPG, gateBack))
            .setLinearHeadingInterpolation(PPG.heading, gateBack.heading)
            .build()
        goLeave= PedroComponent.Companion.follower.pathBuilder()
            .addPath(BezierLine(PPG, leave))
            .setLinearHeadingInterpolation(PPG.heading, leave.heading)
            .build()
    }


}