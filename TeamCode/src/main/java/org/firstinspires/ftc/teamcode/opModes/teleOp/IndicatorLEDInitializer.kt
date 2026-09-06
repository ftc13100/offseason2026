package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.opModes.subsystems.IndicatorLED


@TeleOp(name = "LED Initializer")
class IndicatorLEDInitializer : NextFTCOpMode() {
    init {
        addComponents(
            SubsystemComponent(
                IndicatorLED
            )
        )
    }

    override fun onStartButtonPressed() {
        IndicatorLED.createArtboards()
    }

    override fun onStop() {
        IndicatorLED.stop()
    }
}