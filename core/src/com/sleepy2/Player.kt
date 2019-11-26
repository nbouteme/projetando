package com.sleepy2

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import kotlin.math.roundToInt


class Player(ground: Ground) : Actor() {
    var bt = Texture(Gdx.files.internal("hoshi.png"))
    var rt = Texture(Gdx.files.internal("goodlogic.jpg"))

    val props = SpriteSheetProperties(Point(24, 24), Point(5, 5), 21);

    val scale = 2.0f
    var energy = 1000.0f
    var mass = 1.0f
    var tr = TextureRegion()
    var rr = TextureRegion()
    val ground = ground;
    var vel = Vector2(0f, 0f);
    var currframe = 0.0f
    var font = BitmapFont()

    private fun getFrameCoord(i: Int): Point {
        if ((i < 0) or (i >= props.frameCount))
            return Point(0, 0)
        return Point(i % props.cellCounts.x * props.cellDims.x, i / props.cellCounts.y * props.cellDims.y)
    }

    init {
        tr.texture = bt
        rr.texture = rt;
        val p = getFrameCoord(currframe.toInt())
        tr.setRegion(p.x, p.y, props.cellDims.x, props.cellDims.y)
        rr.setRegion(0, 0, 1, 1);
        font.data.scale(2f);
    }

    fun reflect(I: Vector2, N: Vector2): Vector2 {
        val i = Vector2(I)
        val n = Vector2(N)
        n.scl(n.dot(i) * 2)
        i.sub(n)
        return i
    }

    var preserve = false;
    override fun act(delt: Float) {
        super.act(delt)
        val delta = 1.0f / 60f;
        var active = Gdx.input.isButtonPressed(Input.Keys.A) || Gdx.input.isButtonPressed(Input.Buttons.LEFT) || Gdx.input.isTouched
        if (energy <= 0f) {
            energy = 0f
            active = false;
        }

        var hitted = ground.hit(x, y + 24, true);
        if (hitted == null)
            hitted = ground.hit(x, y, true)
        if (hitted != null) {
            val normal = ground.normalAt(x)
            if (normal.dot(vel) < 0) { // Le sol va influencer le mouvement
                if (active || preserve) {
                    val back = vel.x < 0;
                    vel.setAngle(normal.cpy().rotate90(-1).angle()); // suit la pente
                    if (back && normal.x < 0)
                        vel.rotate90(2)
                    preserve = true;
                } else { // permet de rebondir
                    var pot = vel.dot(vel)
                    vel.nor()
                    vel = reflect(vel, normal)
                    pot *= 0.5f;
                    if (pot < 500f)
                        pot = 0f;
                    vel.scl(Math.sqrt(pot.toDouble() * 0.5f).toFloat())
                }
            }
        }

        if (y < ground.getHeightAt(x))
            y = ground.getHeightAt(x);

        var dir = vel.x / scale;
        dir /= 60.0f;
        if (vel.len2() > 600.0f) {
            currframe = (currframe + dir) % props.frameCount
            if (currframe < 0.0f)
                currframe = props.frameCount - 1.0f;
        }

        if (y - ground.getHeightAt(x) >= 5.0f) { // on considere qu'on est sur le sol
            preserve = false;
        }

        val a = vel.angle();

        if (active && (a < 25f || a > 145f)) {
            mass = 4.2f;
            energy -= mass;
        }
        else
            mass = 1.0f

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            vel.add(15f, 0f);
            vel.y = 50f;
        }

        if (vel.len2() > 1.0f) {
            val d = vel.x * delta;
            ground.scroll.add(d, 0.0f);
            ground.steepness -= d / 100.0f;
            ground.minh -= d / 110.0f / 720f;
            ground.maxh += d / 70.0f / 720f;
            y += vel.y * delta
        }
        vel.y -= 500.981f * mass * delta;
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null)
            return

        batch.draw(tr, x - tr.regionWidth * scale * 0.5f, y, tr.regionWidth * scale, tr.regionHeight * scale)
        font.draw(batch, "Energy: ${energy.roundToInt()} Score: ${ground.scroll.x.roundToInt()}", 800f, 600f)
        //font.draw(batch, "Record: ${}", 800f, 600f)
        val p = getFrameCoord(currframe.toInt())
        tr.setRegion(p.x, p.y, props.cellDims.x, props.cellDims.y)
    }

}