package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.pedropathing.geometry.Pose
import kotlin.math.abs

object ZoneDetection {
    fun signedArea(a: Point, b: Point, c: Point) = abs((b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y))

    fun poseInTriangle(pose: Pose, triangle: Triangle): Boolean {
        val point = Point(pose.x, pose.y)

        val s1 = signedArea(triangle.pointA, triangle.pointB, point)
        val s2 = signedArea(triangle.pointB, triangle.pointC, point)
        val s3 = signedArea(triangle.pointC, triangle.pointA, point)
        val totalArea = signedArea(triangle.pointA, triangle.pointB, triangle.pointC)

        return abs(s1 + s2 + s3 - totalArea) <= 0.001
    }

    fun poseInRect(pose: Pose, rect: Rectangle): Boolean {
        return poseInTriangle(pose, Triangle(rect.pointA, rect.pointB, rect.pointC)) &&
               poseInTriangle(pose, Triangle(rect.pointC, rect.pointD, rect.pointA))
    }
}

data class Triangle (val pointA: Point, val pointB: Point, val pointC: Point)

fun Triangle.getArea(): Double {
    val x1 = this.pointA.x
    val y1 = this.pointA.y
    val x2 = this.pointB.x
    val y2 = this.pointB.y
    val x3 = this.pointC.x
    val y3 = this.pointC.y
    return abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)) / 2.0
}

data class Rectangle (val pointA: Point, val pointB: Point, val pointC: Point, val pointD: Point)

data class Point(val x: Double, val y: Double)