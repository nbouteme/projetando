package com.sleepy2

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Audio
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.glutils.*
import com.badlogic.gdx.graphics.profiling.GLErrorListener
import com.badlogic.gdx.graphics.profiling.GLProfiler
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import javax.xml.bind.util.ValidationEventCollector


class Button(action: () -> Unit, path: String = "badlogic.jpg") : Actor() {
    var bt = Texture(Gdx.files.internal(path))

   init {
       this.x = (Math.random() * 100 + 100).toFloat()
       this.y = (Math.random() * 100 + 100).toFloat()
       this.width = 100.0f
       this.height = 50.0f
       print("button")
       this.addListener(object : InputListener() {
           override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
               action()
               return true
           }
       })
   }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if (batch == null)
            return
        batch.draw(bt, x, y)
    }
}

data class Point(var x: Int, var y: Int)
data class SpriteSheetProperties(val cellDims: Point, val cellCounts: Point, val frameCount: Int)


class MainMenu : Stage(GameScreen) {
    val mus = Gdx.audio.newMusic(Gdx.files.internal("title.ogg"))
    init {
        val patch = NinePatchDrawable(NinePatch(Texture(Gdx.files.internal("knob.png")), 12, 12, 12, 12))

        mus.isLooping = true;
        mus.play()
        var fontg = FreeTypeFontGenerator(Gdx.files.internal("l.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 64;
        parameter.borderWidth = 3f;

        val k = TextButton.TextButtonStyle(patch, patch, patch, fontg.generateFont(parameter))
        var tb = TextButton("Play", k)

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

        tb = TextButton("Quiz!", k)

        tb.x = this.viewport.worldWidth - tb.width - 100;
        tb.y = 100.0f

        tb.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                mus.stop()
                SleepyBird.instance.stage = GameView()
                // Todo
                return true
            }
        })

        this.addActor(tb)

        val t = Image(Texture(Gdx.files.internal("title.png")))
        t.x = viewport.worldWidth * 0.5f - t.width * 0.5f;
        t.y = viewport.worldHeight * 0.5f - t.height * 0.5f;
        this.addActor(t)

        parameter.size = 32;
        var text = Text("Presented by LowBudgetGames", fontg.generateFont(parameter))
        text.y = viewport.worldHeight - text.height - 600f;
        text.x = 750f;
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

class GameView : Stage {
    constructor(): super(GameScreen)


    init {
        var mon = GLProfiler(Gdx.graphics)
        mon.listener = GLErrorListener.THROWING_LISTENER
        mon.enable()

        ScoreManager.reset()
        val g = Ground()
        val k = Player(g, 100f, 500f);
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
        Gdx.gl.glClearColor(100.0f / 255, 149.0f / 255, 237.0f / 255, 1.0f);
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
