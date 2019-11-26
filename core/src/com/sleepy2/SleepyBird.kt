package com.sleepy2

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Files
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureRegion
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
import javax.xml.bind.util.ValidationEventCollector


class Button(action: () -> Unit, path: String = "./badlogic.jpg") : Actor() {
    var bt = Texture(path)

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


class MainMenu : Stage(ScreenViewport()) {
    init {
        var t = Table()
        var k = TextButton.TextButtonStyle()
        k.font = BitmapFont()
        var tb = TextButton("Main Menu", k)


        tb.x = 100.0f
        tb.y = 100.0f
//        t.add(tb)
        tb.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                SleepyBird.instance.stage = GameView()
                return true
            }
        })
        tb.scaleBy(6.0f);
        this.addActor(tb)
/*        this.addActor(Button({
            print("Actionned Menu!");
            SleepyBird.instance.stage = GameView()
        }, "./goodlogic.jpg"))*/
    }

}

class GameView : Stage {
    constructor(): super(ScreenViewport())

    init {
        var mon = GLProfiler(Gdx.graphics)
        mon.listener = GLErrorListener.THROWING_LISTENER
        mon.enable()

        val g = Ground()
        val k = Player(g);
        k.x = 100f
        k.y = 700f
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

    init {
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
