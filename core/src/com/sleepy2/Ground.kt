package com.sleepy2

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.GLOnlyTextureData
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.math.BSpline
import com.badlogic.gdx.math.Vector3
import kotlin.math.pow
import kotlin.system.exitProcess

class Ground : Actor() {
    var bt = Texture(Gdx.files.internal("ground.png"));
    var shader = ShaderProgram(Gdx.files.internal("ground.vs"),
            Gdx.files.internal("ground.fs"))

    var mesh: Mesh
    var cam = Matrix4()
    var scroll = Vector2(0.0f, 0.0f)
    var steepness = 500.0f
    var minh = 0.4f
    var maxh = 0.6f

    val fa = 289.0f
    var tr = TextureRegion()

    init {
        cam.setToOrtho(0f, 1280f, 0f, 720f, 0f, -2f)
        x = 0f
        y = 0f
        touchable = Touchable.enabled
        width = 1280f
        height = 100f

        tr.texture = bt;
        tr.setRegion(0, 0, 1, 1);
        val points = FloatArray(width.toInt() * 6)
        for (i in 0..points.size - 6 step 6) {
            points[i + 0] = i.toFloat() / 6f; // hauteur
            points[i + 1] = 100.0f; //haut
            points[i + 2] = 1.0f; // infl

            points[i + 3] = i.toFloat() / 6f; // hauteur
            points[i + 4] = 0.0f; // toujours en bas
            points[i + 5] = 0.0f; // infl
        }
        mesh = Mesh(true, points.size, 0,
                VertexAttribute(VertexAttributes.Usage.Position, 2, "pos"),
                VertexAttribute(VertexAttributes.Usage.Generic, 1,"influence"));
        mesh.setVertices(points, 0, points.size)

        print(shader.log)
    }

    fun updateShaderState() {
        shader.setUniformMatrix("view", cam)
        shader.setUniformf("scroll", scroll)
        shader.setUniformf("steep", steepness)
        shader.setUniformf("minh", minh)
        shader.setUniformf("maxh", maxh)

        shader.setUniformi("tex", 0)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null)
            return

        batch.end()

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        shader.begin()

        mesh.bind(shader)
        updateShaderState()
        bt.bind(0);
        mesh.render(shader, GL30.GL_TRIANGLE_STRIP)
        mesh.unbind(shader)
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        shader.end()

        val err = Gdx.gl.glGetError()
        if (err != 0) {
            print(Gdx.gl.glGetString(err))

        }
        batch.begin()
        //batch.draw(bt, x, y,width, height);
//        scroll.x += 1f;
    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (touchable && this.touchable != Touchable.enabled) return null
        if (!isVisible) return null

        if (y < getHeightAt(x))
            return this
        return null
    }


    fun permute(v: Vector3): Vector3 {
        return Vector3(
                (v.x * 36 + 1) * v.x % fa,
                (v.y * 36 + 1) * v.y % fa,
                (v.z * 36 + 1) * v.z % fa
        )
    }

    fun snoise(v: Float): Float {
        val C = arrayOf(0.211324865405187f,
                0.366025403784439f,  // 0.5*(sqrt(3.0)-1.0)
                -0.577350269189626f,  // -1.0 + 2.0 * C.x
                0.024390243902439f) // 1.0 / 41.0
        val v2 = Vector2 (v, v)
        var t = kotlin.math.floor(v + v2.dot(Vector2(C[1], C[1])))
        var i  = Vector2(t, t)
        t = i.dot(Vector2(C[0], C[0]));
        val x0 = v2.cpy().sub(i).add(t, t)

        var i1 = Vector2(0.0f, 1.0f)
        if (x0.x > x0.y)
            i1 = Vector2(1.0f, 0.0f);
        val x12 = arrayOf(
                x0.x + C[0] - i1.x,
                x0.y + C[0] - i1.y,
                x0.x + C[2],
                x0.y + C[2]
        )

        i = Vector2(i.x % fa, i.y % fa);
        var k = Vector3(0.0f, i1.y, 1.0f).add(i.y);
        var l = Vector3(0.0f, i1.x, 1.0f).add(i.x)
        val p = permute(permute(k).add(l));
        val x12xy = Vector2(x12[0], x12[1]);
        val x12zw = Vector2(x12[2], x12[3]);

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
        val h = Vector3(kotlin.math.abs(x.x) - 0.5f,
                        kotlin.math.abs(x.y) - 0.5f,
                        kotlin.math.abs(x.z) - 0.5f)
        val ox = Vector3(kotlin.math.floor(x.x + 0.5f),
                kotlin.math.floor(x.y + 0.5f),
                kotlin.math.floor(x.z + 0.5f))
        val a0 = x.sub(ox)

        m = Vector3(m.x / kotlin.math.sqrt(a0.x * a0.x + h.x * h.x),
                    m.y / kotlin.math.sqrt(a0.y * a0.y + h.y * h.y),
                    m.z / kotlin.math.sqrt(a0.z * a0.z + h.z * h.z))

        var g = Vector3(a0.x  * x0.x   + h.x  * x0.y,
        a0.y * x12[0] + h.y * x12[1],
        a0.z * x12[2] + h.z * x12[3]);
        return 130.0f * m.dot(g);
    }

    fun getHeightAt(x: Float): Float {
        val raw = snoise((x + scroll.x) / steepness);
        return 100.0f + (raw * (maxh - minh) + minh) * 100.0f
    }

    fun normalAt(x: Float): Vector2 {
        val h = getHeightAt(x)
        val i = getHeightAt(x + 2)
        val a = Vector2(x, h)
        val b = Vector2(x + 2, i)
        return b.sub(a).nor().rotate90(1)
    }
}
