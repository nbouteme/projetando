package com.sleepy2

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Pixmap.Format
import com.badlogic.gdx.graphics.TextureData
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.GdxRuntimeException
import java.nio.ByteBuffer
import java.nio.FloatBuffer

class Float1DTexture(width: Int,  mipMapLevel: Int, internal var internalFormat: Int, internal var format: Int, internal var type: Int, internal var buff: FloatArray) : TextureData {
    /** width and height  */
    internal var width = 0
    internal var isPrepared = false

    /** properties of opengl texture  */
    internal var mipLevel = 0
    internal val db : FloatBuffer;
    init {
        this.width = width
        this.mipLevel = mipMapLevel
        this.db = BufferUtils.newFloatBuffer(width * 4)
        this.db.put(buff)
        this.db.flip()
    }

    override fun getType(): TextureData.TextureDataType {
        return TextureData.TextureDataType.Custom
    }

    override fun isPrepared(): Boolean {
        return isPrepared
    }

    override fun prepare() {
        if (isPrepared) throw GdxRuntimeException("Already prepared")
        isPrepared = true
    }

    override fun consumeCustomData(target: Int) {
        Gdx.gl.glTexImage2D(target, mipLevel, internalFormat, width, 1, 0, format, type, db)
    }

    override fun consumePixmap(): Pixmap {
        throw GdxRuntimeException("This TextureData implementation does not return a Pixmap")
    }

    override fun disposePixmap(): Boolean {
        throw GdxRuntimeException("This TextureData implementation does not return a Pixmap")
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }

    override fun getFormat(): Format {
        return Format.RGBA8888
    }

    override fun useMipMaps(): Boolean {
        return false
    }

    override fun isManaged(): Boolean {
        return false
    }
}
