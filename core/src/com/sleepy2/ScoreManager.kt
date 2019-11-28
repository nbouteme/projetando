package com.sleepy2

import com.badlogic.gdx.Gdx

object ScoreManager {
    val prefs = Gdx.app.getPreferences("records")

    private var _score: Float = 0f
    private var _maxscore: Int = 0

    private var _broken = false;

    val broken: Boolean
    get() = _broken

    init {
        _maxscore = prefs.getInteger("maxscore")
    }

    var score: Float
        get()  = _score
        set(v) {
            _score = v
            if (_score > _maxscore) {
                _maxscore = _score.toInt()
                _broken = true;
            }
        }

    val maxScore: Int
        get()  = _maxscore

    fun preserve() {
        prefs.putInteger("maxscore", _maxscore)
        prefs.flush()
    }

    fun reset() {
        _score = 0f
        _maxscore = prefs.getInteger("maxscore")
        _broken = false;
    }
}