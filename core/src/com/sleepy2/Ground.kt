package com.sleepy2

import com.badlogic.gdx.Application
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

class Ground(val noise: Noise = Simplex()) : Actor() {
    var bt = Texture(Gdx.files.internal("ground.png"));
    var shader = ShaderProgram(Gdx.files.internal("ground.vs"),
            Gdx.files.internal("ground.fs"))
    var tr = TextureRegion()

    var mesh: Mesh

    var cam = Matrix4()
    var scroll = Vector2(0.0f, 0.0f)
    var steepness = 500.0f
    var minh = 0.4f
    var maxh = 0.6f


    init {
        if (Gdx.app.type == Application.ApplicationType.Android) {
            bt = Texture(Gdx.files.internal("groundhi.png"));
            steepness *= 2f;
        }

        bt.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
        bt.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        cam.setToOrtho(0f, Gdx.graphics.width.toFloat(),
                0f, Gdx.graphics.height.toFloat(),
                0f, -2f)
        x = 0f
        y = 0f
        touchable = Touchable.enabled
        width = GameScreen.worldWidth
        height = 100f

        scroll.x = Math.random().toFloat() * 50000f;
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
        shader.begin()
        shader.setUniformMatrix("view", cam)
        shader.setUniformi("tex", 0)
        shader.end()
    }

    private fun updateShaderState() {
        shader.setUniformf("scroll", scroll)
        shader.setUniformf("steep", steepness)
        shader.setUniformf("minh", minh)
        shader.setUniformf("maxh", maxh)

//        shader.setUniformf("ts", Vector2(bt.width.toFloat(), bt.height.toFloat()))

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


        batch.begin()

    }

    override fun hit(x: Float, y: Float, touchable: Boolean): Actor? {
        if (touchable && this.touchable != Touchable.enabled) return null
        if (!isVisible) return null

        if (y < getHeightAt(x))
            return this
        return null
    }

    fun getHeightAt(x: Float): Float {
        val raw = noise.eval((x + scroll.x) / steepness);
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
