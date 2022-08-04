package com.example.krunalshah.info6130_lab3

import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null
) :
    SurfaceView(mContext, attrs), Runnable {
    private var mRunning = false
    private var mGameThread: Thread? = null
    private val mPath: Path
    private var mGreenLight: GreenLight? = null
    private val mPaint: Paint = Paint()
    private var mBitmap: Bitmap? = null
    private var mMap: Bitmap? = null
    private var mWinnerRect: RectF? = null
    private var mBitmapX = 0
    private var mBitmapY = 0
    private var mViewWidth = 0
    private var mViewHeight = 0
    private val mSurfaceHolder: SurfaceHolder = holder

    private var mediaPlayer: MediaPlayer? = null

    /**
     * We cannot get the correct dimensions of views in onCreate because
     * they have not been inflated yet. This method is called every time the
     * size of a view changes, including the first time after it has been
     * inflated.
     *
     * @param w Current width of view.
     * @param h Current height of view.
     * @param oldw Previous width of view.
     * @param oldh Previous height of view.
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        mGreenLight = GreenLight(mViewWidth, mViewHeight)

        // Set font size proportional to view size.
        mPaint.textSize = (mViewHeight / 5).toFloat()
        mBitmap = BitmapFactory.decodeResource(
            mContext.resources, R.drawable.treasure
        )
        mMap = BitmapFactory.decodeResource(
            mContext.resources, R.drawable.map
        )

        setUpBitmap()
    }

    /**
     * Runs in a separate thread.
     * All drawing happens here.
     */
    override fun run() {
        var canvas: Canvas
        mediaPlayer = MediaPlayer.create(context, R.raw.igotit_audio)

        while (mRunning) {
            // If we can obtain a valid drawing surface...
            if (mSurfaceHolder.surface.isValid) {

                // Helper variables for performance.
                val x: Int? = mGreenLight?.getX()
                val y: Int? = mGreenLight?.getY()
                val radius: Int? = mGreenLight?.getRadius()


                canvas = mSurfaceHolder.lockCanvas()

                // Fill the canvas with white and draw the bitmap.
                canvas.save()
                canvas.drawColor(Color.GREEN)
                canvas.drawBitmap(mBitmap!!, mBitmapX.toFloat(), mBitmapY.toFloat(), mPaint)

                // Add clipping region and fill rest of the canvas with black.
                mPath.addCircle(x!!.toFloat(), y!!.toFloat(), radius!!.toFloat(), Path.Direction.CCW)

                // The method clipPath(path, Region.Op.DIFFERENCE) was
                // deprecated in API level 26. The recommended alternative
                // method is clipOutPath(Path), which is currently available
                // in API level 26 and higher.
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    canvas.clipPath(mPath, Region.Op.DIFFERENCE)
                } else {
                    canvas.clipOutPath(mPath)
                }
                canvas.drawBitmap(mMap!!, 0.toFloat(), 0.toFloat(), null)
//                canvas.drawColor(Color.BLACK)
//                canvas.drawBitmap(BitmapFactory.decodeResource(resources, R.drawable.map), 0, 0, null)

                // If the x, y coordinates of the user touch are within a
                //  bounding rectangle, display the winning message.
                if (x > mWinnerRect!!.left && x < mWinnerRect!!.right && y > mWinnerRect!!.top && y < mWinnerRect!!.bottom) {
                    canvas.drawColor(Color.WHITE)
                    canvas.drawBitmap(mBitmap!!, mBitmapX.toFloat(), mBitmapY.toFloat(), mPaint)
                    canvas.drawText(
                        "GOT IT", (mViewWidth / 10).toFloat(), (mViewHeight / 2).toFloat(), mPaint
                    )
                    mediaPlayer?.start()
//                    music()
                }
                // Clear the path data structure.
                mPath.rewind()
                // Restore the previously saved (default) clip and matrix state.
                canvas.restore()
                // Release the lock on the canvas and show the surface's
                // contents on the screen.
                mSurfaceHolder.unlockCanvasAndPost(canvas)
            }
        }

        mediaPlayer?.stop()
    }

//    private fun music() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            mediaPlayer?.pause()
//        }, 1000)
//    }

    /**
     * Updates the game data.
     * Sets new coordinates for the flashlight cone.
     *
     * @param newX New x position of touch event.
     * @param newY New y position of touch event.
     */
    private fun updateFrame(newX: Int, newY: Int) {
        mGreenLight!!.update(newX, newY)
    }

    /**
     * Calculates a randomized location for the bitmap
     * and the winning bounding rectangle.
     */
    private fun setUpBitmap() {
        mBitmapX = Math.floor(
            Math.random() * (mViewWidth - mBitmap!!.width)
        ).toInt()
        mBitmapY = Math.floor(
            Math.random() * (mViewHeight - mBitmap!!.height)
        ).toInt()
        mWinnerRect = RectF(
            mBitmapX.toFloat(), mBitmapY.toFloat(),
            (mBitmapX + mBitmap!!.width).toFloat(),
            (mBitmapY + mBitmap!!.height).toFloat()
        )
    }

    /**
     * Called by MainActivity.onPause() to stop the thread.
     */
    fun pause() {
        mRunning = false
        try {
            // Stop the thread == rejoin the main thread.
            mGameThread!!.join()
        } catch (e: InterruptedException) {
        }
    }

    /**
     * Called by MainActivity.onResume() to start a thread.
     */
    fun resume() {
        mRunning = true
        mGameThread = Thread(this)
        mGameThread!!.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setUpBitmap()
                // Set coordinates of flashlight cone.
                updateFrame(x.toInt(), y.toInt())
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                // Updated coordinates for flashlight cone.
                updateFrame(x.toInt(), y.toInt())
                invalidate()
            }
            else -> {}
        }
        return true
    }

    init {
        //from getHolder()
        mPaint.color = Color.DKGRAY
        mPath = Path()
    }
}