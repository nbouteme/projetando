package com.sleepy2

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlin.math.roundToInt
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter




class Player(ground: Ground, _x: Float, _y: Float) : Actor() {
    var bt = Texture(Gdx.files.internal("hoshi.png"))
    var rt = Texture(Gdx.files.internal("goodlogic.jpg"))

    val props = SpriteSheetProperties(Point(24, 24), Point(5, 5), 21);

    var pscale = 2.0f
    var energy = 1000.0f
    var mass = 1.0f
    var tr = TextureRegion()
    var rr = TextureRegion()
    val ground = ground;
    var vel = Vector2(0f, 0f);
    var currframe = 0.0f
    var fontg = FreeTypeFontGenerator(Gdx.files.internal("l.ttf"))
    var font: BitmapFont
    val bounce = Gdx.audio.newSound(Gdx.files.internal("bounce.wav"))
    val end = Gdx.audio.newSound(Gdx.files.internal("end.wav"))
    var bgm = Gdx.audio.newMusic(Gdx.files.internal("green.ogg"));

    var isCheckScheduled = false;
    val res = ResultScreen(); // prepare un peu en avance pour laisser le temps de charger

    private fun getFrameCoord(i: Int): Point {
        if ((i < 0) or (i >= props.frameCount))
            return Point(0, 0)
        return Point(i % props.cellCounts.x * props.cellDims.x, i / props.cellCounts.y * props.cellDims.y)
    }

    init {
        if (Gdx.app.type == Application.ApplicationType.Android)
            pscale = 4f;
        x = _x;
        y = _y
        tr.texture = bt
        rr.texture = rt;
        val p = getFrameCoord(currframe.toInt())
        tr.setRegion(p.x, p.y, props.cellDims.x, props.cellDims.y)
        rr.setRegion(0, 0, 1, 1);
        val parameter = FreeTypeFontParameter()
        parameter.size = 64;
        parameter.borderWidth = 3f;
        font = fontg.generateFont(parameter);
        while(ground.normalAt(x).angle() > 70f) {
            ground.scroll.x += 10f; // on veut commencer sur une pente descendante
        }
        bgm.isLooping = true;
        bgm.play()
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
        if (energy < 1f) {
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
                        vel.rotate90(1).rotate90(1) // 180d, obligé de le faire 2 fois a cause de l'implémentation de rotate90
                    preserve = true;
                } else { // permet de rebondir
                    var pot = vel.dot(vel)
                    vel.nor()
                    vel = reflect(vel, normal)
                    pot *= 0.5f;

                    vel.scl(Math.sqrt(pot.toDouble() * 0.5f).toFloat())
                }
            }
        }

        if (y < ground.getHeightAt(x))
            y = ground.getHeightAt(x);

        var dir = vel.x / pscale;
        dir /= 60.0f * pscale;
        if (vel.len2() > 600.0f) {
            currframe = (currframe + dir) % props.frameCount
            if (currframe < 0.0f)
                currframe = props.frameCount - 1.0f;
        }

        if (y - ground.getHeightAt(x) >= 5.0f) { // on considere qu'on est sur le sol
            if (preserve)
                bounce.play();
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
            ScoreManager.score += d.toInt();
            ground.scroll.add(d, 0.0f);
            ground.steepness -= d / 100.0f;
            ground.minh -= d / 110.0f / GameScreen.worldHeight;
            ground.maxh += d / 70.0f / GameScreen.worldHeight;
            y += vel.y * delta
        }
        vel.y -= 500.981f * mass * delta;

        if (energy == 0f && !isCheckScheduled) {
            isCheckScheduled = true;
            val lpos = Vector2(ground.scroll.x, y);
            Timer.schedule(object: Timer.Task() {
                override fun run() {
                    val cpos = Vector2(ground.scroll.x, y);
                    if (cpos.sub(lpos).len() < 50f) {
                        bgm.stop()
                        end.play()
                        Timer.schedule(object: Timer.Task() {
                            override fun run() {
                                SleepyBird.instance.stage = res.visit();
                            }
                        }, 1.0f)
                    }
                    else
                        isCheckScheduled = false;
                }
            }, 2.0f);
        }
    }


    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null)
            return

        batch.draw(tr, x - tr.regionWidth * pscale * 0.5f, y, tr.regionWidth * pscale, tr.regionHeight * pscale)
        font.draw(batch, "Energy: ${energy.roundToInt()}\nScore: ${ScoreManager.score.toInt()}", 1100f, GameScreen.worldHeight - font.lineHeight)
        font.draw(batch, "Max Score: ${ScoreManager.maxScore}", 100f, GameScreen.worldHeight - font.lineHeight)
        //font.draw(batch, "Record: ${}", 800f, 600f)
        val p = getFrameCoord(currframe.toInt())
        tr.setRegion(p.x, p.y, props.cellDims.x, props.cellDims.y)
    }

}