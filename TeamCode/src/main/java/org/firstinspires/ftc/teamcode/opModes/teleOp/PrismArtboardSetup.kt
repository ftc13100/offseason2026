package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.HardwareDevice

@TeleOp(name = "Prism Artboard Setup", group = "Setup")
class PrismArtboardSetup : LinearOpMode() {

    override fun runOpMode() {
        val prismClass = findClass(
            "org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.GoBildaPrismDriver"
        )

        if (prismClass == null) {
            telemetry.addLine("Prism driver class not found")
            telemetry.update()
            waitForStart()
            return
        }

        @Suppress("UNCHECKED_CAST")
        val prism = hardwareMap.get(prismClass as Class<out HardwareDevice>, "led")

        val colorClass = findClass(
            "org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.Color"
        )

        if (colorClass == null) {
            telemetry.addLine("Prism Color class not found")
            telemetry.update()
            waitForStart()
            return
        }

        val red = getStaticField(colorClass, "RED")
        val blue = getStaticField(colorClass, "BLUE")
        val yellow = getStaticField(colorClass, "YELLOW")
        val green = getStaticField(colorClass, "GREEN")
        val transparent = getStaticField(colorClass, "TRANSPARENT")

        // FIXED: removed escaped quote
        val blinkClass = findClass(
            "org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.PrismAnimations\$Blink"
        )

        val solidClass = findClass(
            "org.firstinspires.ftc.teamcode.opModes.subsystems.Prism.PrismAnimations\$Solid"
        )

        val layer0 = getLayer0(prismClass)
        val artboardEnum = getArtboardEnum(prismClass)

        if (layer0 == null) {
            telemetry.addLine("Could not get LayerHeight.LAYER_0")
            telemetry.update()
            waitForStart()
            return
        }

        if (artboardEnum == null) {
            telemetry.addLine("Could not get Artboard enum")
            telemetry.update()
            waitForStart()
            return
        }

        callMethod(prism, "setStripLength", 12)

        fun applySolid(color: Any?) {
            callMethod(prism, "clearAllAnimations")
            val solid = createSolid(solidClass, color, 0, 11)
            if (solid != null) {
                callMethod(prism, "insertAndUpdateAnimation", layer0, solid)
            } else {
                telemetry.addLine("Could not create solid animation")
            }
        }

        fun applyBlink(color: Any?) {
            callMethod(prism, "clearAllAnimations")
            val blink = createBlink(blinkClass, color, transparent)
            if (blink != null) {
                callMethod(prism, "insertAndUpdateAnimation", layer0, blink)
            } else {
                telemetry.addLine("Could not create blink animation")
            }
        }

        fun saveToArtboard(index: Int) {
            val artboard = getArtboard(artboardEnum, index)
            if (artboard != null) {
                callMethod(prism, "saveCurrentAnimationsToArtboard", artboard)
                telemetry.addLine("Saved artboard $index")
            } else {
                telemetry.addLine("Could not get artboard $index")
            }
        }

        waitForStart()
        if (isStopRequested) return

        // Artboard 0: solid red (0 balls, stopped)
        applySolid(red)
        sleep(500)
        saveToArtboard(0)
        telemetry.update()
        sleep(500)

        // Artboard 1: blinking red (0 balls, running)
        applyBlink(red)
        sleep(500)
        saveToArtboard(1)
        telemetry.update()
        sleep(500)

        // Artboard 2: solid blue (1 ball, stopped)
        applySolid(blue)
        sleep(500)
        saveToArtboard(2)
        telemetry.update()
        sleep(500)

        // Artboard 3: blinking blue (1 ball, running)
        applyBlink(blue)
        sleep(500)
        saveToArtboard(3)
        telemetry.update()
        sleep(500)

        // Artboard 4: solid yellow (2 balls, stopped)
        applySolid(yellow)
        sleep(500)
        saveToArtboard(4)
        telemetry.update()
        sleep(500)

        // Artboard 5: blinking yellow (2 balls, running)
        applyBlink(yellow)
        sleep(500)
        saveToArtboard(5)
        telemetry.update()
        sleep(500)

        // Artboard 6: solid green (3 balls, stopped)
        applySolid(green)
        sleep(500)
        saveToArtboard(6)
        telemetry.update()
        sleep(500)

        // Artboard 7: blinking green (3 balls, running)
        applyBlink(green)
        sleep(500)
        saveToArtboard(7)
        telemetry.update()
        sleep(500)

        callMethod(prism, "clearAllAnimations")

        telemetry.addLine("✓ All artboards programmed successfully!")
        telemetry.update()

        while (opModeIsActive()) {
            sleep(100)
        }
    }

    private fun findClass(vararg classNames: String): Class<*>? {
        for (name in classNames) {
            try {
                return Class.forName(name)
            } catch (_: Throwable) {
                // try next
            }
        }
        return null
    }

    private fun getStaticField(clazz: Class<*>, fieldName: String): Any? {
        return try {
            clazz.getField(fieldName).get(null)
        } catch (_: Throwable) {
            null
        }
    }

    private fun getLayer0(prismClass: Class<*>): Any? {
        return try {
            val layerClass = prismClass.declaredClasses.firstOrNull {
                it.simpleName == "LayerHeight"
            } ?: return null
            layerClass.getField("LAYER_0").get(null)
        } catch (_: Throwable) {
            null
        }
    }

    private fun getArtboardEnum(prismClass: Class<*>): Class<*>? {
        return try {
            prismClass.declaredClasses.firstOrNull {
                it.simpleName == "Artboard"
            }
        } catch (_: Throwable) {
            null
        }
    }

    private fun getArtboard(artboardEnum: Class<*>, index: Int): Any? {
        return try {
            artboardEnum.getField("ARTBOARD_$index").get(null)
        } catch (_: Throwable) {
            null
        }
    }

    private fun createSolid(solidClass: Class<*>?, color: Any?, start: Int, end: Int): Any? {
        if (solidClass == null) return null
        val colorClass = color?.javaClass

        return try {
            solidClass.getConstructor(
                colorClass,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            ).newInstance(color, start, end)
        } catch (e: Throwable) {
            null
        }
    }

    private fun createBlink(blinkClass: Class<*>?, primary: Any?, secondary: Any?): Any? {
        if (blinkClass == null) return null
        val colorClass = primary?.javaClass

        // Try constructor with colors and timing
        return try {
            blinkClass.getConstructor(
                colorClass,
                colorClass,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            ).newInstance(primary, secondary, 500, 500)
        } catch (_: Throwable) {
            // Try simpler constructor
            try {
                blinkClass.getConstructor(
                    colorClass,
                    colorClass
                ).newInstance(primary, secondary)
            } catch (_: Throwable) {
                null
            }
        }
    }

    private fun callMethod(target: Any, methodName: String, vararg args: Any?): Boolean {
        val methods = target.javaClass.methods.filter { it.name == methodName }
        for (m in methods) {
            if (m.parameterTypes.size != args.size) continue
            try {
                m.invoke(target, *args)
                return true
            } catch (e: Throwable) {
                // try next overload
            }
        }
        return false
    }
}