package com.example.krunalshah.info6130_lab3

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private var mGameView: GameView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mGameView = GameView(this)
        setContentView(mGameView)
    }

    /**
     * Pauses game when activity is paused.
     */
    override fun onPause() {
        super.onPause()
        mGameView?.pause()
    }

    /**
     * Resumes game when activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        mGameView?.resume()
    }
}