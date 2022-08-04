package com.example.krunalshah.info6130_lab3

class GreenLight {

    private var mX = 0
    private var mY = 0
    private var mRadius = 0

    constructor(viewWidth: Int, viewHeight: Int){
        mX = viewWidth / 2
        mY = viewHeight / 2
        // Adjust the radius for the narrowest view dimension.
        mRadius = if (viewWidth <= viewHeight) mX / 3 else mY / 3
    }

    /**
     * Update the coordinates of the flashlight cone.
     *
     * @param newX Changed value for x coordinate.
     * @param newY Changed value for y coordinate.
     */
    fun update(newX: Int, newY: Int) {
        mX = newX
        mY = newY
    }

    fun getX(): Int {
        return mX
    }

    fun getY(): Int {
        return mY
    }

    fun getRadius(): Int {
        return mRadius
    }
}
