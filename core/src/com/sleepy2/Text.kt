package com.sleepy2

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Actor

class Text(val t: String, var font: BitmapFont) : Actor() {
    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        font.draw(batch, t, x, y);
    }

    override fun getHeight(): Float {
        return font.lineHeight
    }
}