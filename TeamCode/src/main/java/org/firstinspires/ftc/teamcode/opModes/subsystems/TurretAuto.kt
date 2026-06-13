package org.firstinspires.ftc.teamcode.opModes.subsystems.shooter

import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode

object TurretAuto : Subsystem {
    private lateinit var turret1: Servo
    private lateinit var turret2: Servo

    override fun initialize() {
        turret1 = ActiveOpMode.hardwareMap.get(Servo::class.java, "turret1")
        turret2 = ActiveOpMode.hardwareMap.get(Servo::class.java, "turret2")

    }


    val toLeft = InstantCommand {
        turret1.position = 0.77
    }
    val toLeft2 = InstantCommand {
        turret2.position = 0.77
    }
    val toLeftEvenMore = InstantCommand {
        turret1.position = 0.83
    }
    val toLeft2EvenMore = InstantCommand {
        turret2.position = 0.83
    }


    val toLeftMore = InstantCommand {
        turret1.position = 0.85
    }
    val toLeftMore2 = InstantCommand {
        turret2.position = 0.85
    }


}

