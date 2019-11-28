package com.sleepy2

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class Simplex : Noise {

    val fa = 289.0f

    private fun permute(v: Vector3): Vector3 {
        return Vector3(
                (v.x * 36 + 1) * v.x % fa,
                (v.y * 36 + 1) * v.y % fa,
                (v.z * 36 + 1) * v.z % fa
        )
    }

    // Transcription du simplex du vertex shader
    override fun eval(v: Float): Float {
        val C = arrayOf(0.211324865405187f,
                0.366025403784439f,  // 0.5*(sqrt(3.0)-1.0)
                -0.577350269189626f,  // -1.0 + 2.0 * C.x
                0.024390243902439f) // 1.0 / 41.0
        val v2 = Vector2 (v, v)
        var t = floor(v + v2.dot(Vector2(C[1], C[1])))
        var i  = Vector2(t, t)
        t = i.dot(Vector2(C[0], C[0]))
        val x0 = v2.cpy().sub(i).add(t, t)

        var i1 = Vector2(0.0f, 1.0f)
        if (x0.x > x0.y)
            i1 = Vector2(1.0f, 0.0f)
        val x12 = arrayOf(
                x0.x + C[0] - i1.x,
                x0.y + C[0] - i1.y,
                x0.x + C[2],
                x0.y + C[2]
        )

        i = Vector2(i.x % fa, i.y % fa)
        var k = Vector3(0.0f, i1.y, 1.0f).add(i.y)
        var l = Vector3(0.0f, i1.x, 1.0f).add(i.x)
        val p = permute(permute(k).add(l))
        val x12xy = Vector2(x12[0], x12[1])
        val x12zw = Vector2(x12[2], x12[3])

        var m =
                Vector3(0.5f - x0.dot(x0),
                        0.5f - x12xy.dot(x12xy),
                        0.5f - x12zw.dot(x12zw))
        if (m.x < 0)
            m.x = 0f
        if (m.y < 0)
            m.y = 0f
        if (m.z < 0)
            m.z = 0f

        m.x = m.x.pow(4)
        m.y = m.y.pow(4)
        m.z = m.z.pow(4)

        val x = Vector3(2.0f * ((p.x * C[3]) % 1.0f) - 1.0f,
                2.0f * ((p.y * C[3]) % 1.0f) - 1.0f,
                2.0f * ((p.z * C[3]) % 1.0f) - 1.0f)
        val h = Vector3(abs(x.x) - 0.5f,
                abs(x.y) - 0.5f,
                abs(x.z) - 0.5f)
        val ox = Vector3(floor(x.x + 0.5f),
                floor(x.y + 0.5f),
                floor(x.z + 0.5f))
        val a0 = x.sub(ox)

        m = Vector3(m.x / sqrt(a0.x * a0.x + h.x * h.x),
                m.y / sqrt(a0.y * a0.y + h.y * h.y),
                m.z / sqrt(a0.z * a0.z + h.z * h.z))

        var g = Vector3(a0.x  * x0.x   + h.x  * x0.y,
                a0.y * x12[0] + h.y * x12[1],
                a0.z * x12[2] + h.z * x12[3])
        return 130.0f * m.dot(g)
    }
}