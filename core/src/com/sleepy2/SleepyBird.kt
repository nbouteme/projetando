package com.sleepy2

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ScreenViewport


data class Point(var x: Int, var y: Int)
data class SpriteSheetProperties(val cellDims: Point, val cellCounts: Point, val frameCount: Int)

class MainMenu : Stage(GameScreen) {
    private val mus = Gdx.audio.newMusic(Gdx.files.internal("title.ogg"))

    init {
        val patch = NinePatchDrawable(NinePatch(Texture(Gdx.files.internal("knob.png")), 12, 12, 12, 12))

        mus.isLooping = true
        mus.play()
        val fontg = FreeTypeFontGenerator(Gdx.files.internal("l.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 64
        parameter.borderWidth = 3f

        val k = TextButton.TextButtonStyle(patch, patch, patch, fontg.generateFont(parameter))
        val tb = TextButton("Play", k)

        tb.x = 100.0f
        tb.y = 100.0f

        tb.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                mus.stop()
                SleepyBird.instance.stage = GameView()
                return true
            }
        })

        this.addActor(tb)

        /*
        tb = TextButton("Quiz!", k)

        tb.x = this.viewport.worldWidth - tb.width - 100
        tb.y = 100.0f

        tb.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                mus.stop()
                SleepyBird.instance.stage = GameView()
                // Todo
                return true
            }
        })
        */

        this.addActor(tb)

        val t = Image(Texture(Gdx.files.internal("title.png")))
        t.x = viewport.worldWidth * 0.5f - t.width * 0.5f
        t.y = viewport.worldHeight * 0.5f - t.height * 0.5f
        this.addActor(t)

        parameter.size = 32
        val text = Text("Presented by LowEffortGames©", fontg.generateFont(parameter))
        text.y = viewport.worldHeight - text.height - 600f
        text.x = 750f
        this.addActor(text)
        fontg.dispose()
    }

    override fun dispose() {
        super.dispose()
        mus.dispose()
    }
}

object GameScreen : ScreenViewport() {
    init {
        update(1280, 720)
    }
}

class GameView : Stage(GameScreen) {


    init {
        ScoreManager.reset()
        val g = Ground()
        val k = Player(g, 100f, 500f)
        this.addActor(g)
        this.addActor(k)
    }

}

class SleepyBird : ApplicationAdapter() {
    private lateinit var _stage: Stage
    var stage: Stage
    get() = this._stage
    set(value) {
        if (this::_stage.isInitialized)
            this._stage.dispose()
        this._stage = value
        Gdx.input.inputProcessor = _stage
    }

    override fun create() {
        instance = this
        stage = MainMenu()
    }

    override fun render() {
        Gdx.gl.glClearColor(100.0f / 255, 149.0f / 255, 237.0f / 255, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

    companion object {
        lateinit var instance: SleepyBird
    }
}
