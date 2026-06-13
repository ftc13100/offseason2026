package org.firstinspires.ftc.teamcode.opModes.subsystems.LimeLight

import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode

object MohitBallTrack : Subsystem {

    lateinit var limelight: Limelight3A

    override fun initialize() {
            limelight = ActiveOpMode.hardwareMap.get(Limelight3A::class.java, "limelight")

            limelight.pipelineSwitch(8)

            limelight.start()
        }


    }
