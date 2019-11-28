package com.sleepy2

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.profiling.GLErrorListener
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextArea
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ScreenViewport
import kotlin.math.roundToInt
import com.badlogic.gdx.utils.IntArray
import com.badlogic.gdx.utils.BooleanArray



class ResultScreen : Stage {
    constructor(): super(GameScreen)

    var newrecord = Gdx.audio.newSound(Gdx.files.internal("new.wav"));
    var cont = Gdx.audio.newSound(Gdx.files.internal("continue.wav"));
    var fontg = FreeTypeFontGenerator(Gdx.files.internal("l.ttf"))
    var rec = Texture(Gdx.files.internal("rec.png"))
    var ok = Texture(Gdx.files.internal("ok.png"))


    val parameter = object: FreeTypeFontGenerator.FreeTypeFontParameter() {
        init {
            size = 64
            borderWidth = 3f;
        };
    }
    val parameterr = object: FreeTypeFontGenerator.FreeTypeFontParameter() {
        init {
            size = 64
            borderWidth = 3f;
            color = Color.RED
        };
    }
    val font = fontg.generateFont(parameter)
    val fontr = fontg.generateFont(parameterr)

    init {
        val patch = NinePatchDrawable(NinePatch(Texture(Gdx.files.internal("knob.png")), 12, 12, 12, 12))
        val k = TextButton.TextButtonStyle(patch, patch, patch, font)

        var tb = TextButton("Main Menu", k)

        tb.x = 100.0f
        tb.y = 100.0f

        tb.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                SleepyBird.instance.stage = MainMenu()
                return true
            }
        })
        this.addActor(tb)

        tb = TextButton("Play again", k)

        tb.x = viewport.worldWidth - tb.width - 100f;
        tb.y = 100.0f

        tb.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                SleepyBird.instance.stage = GameView()
                return true
            }
        })
        this.addActor(tb)

    }

    fun visit() : ResultScreen {
        val img: Image
        if (ScoreManager.broken) {
            newrecord.play()
            img = Image(rec);
        }
        else {
            cont.play()
            img = Image(ok);
            img.setScale(0.5f)
        }

        img.x = viewport.worldWidth * 0.5f - img.width * 0.5f * img.scaleX;
        img.y = viewport.worldHeight * 0.5f - img.height * 0.5f * img.scaleY;
        this.addActor(img)
        var text = Text("Score: ${ScoreManager.score.toInt()}", font)
        text.y = viewport.worldHeight - text.height;
        text.x = 100f;
        this.addActor(text)

        text = Text("Max Score: ${ScoreManager.maxScore}", font)
        if (ScoreManager.broken)
            text.font = fontr;
        text.y = viewport.worldHeight - text.height;
        text.x = 900f;
        this.addActor(text)

        return this;
    }

    override fun dispose() {
        super.dispose()
        newrecord.dispose()
        cont.dispose()
    }
}
