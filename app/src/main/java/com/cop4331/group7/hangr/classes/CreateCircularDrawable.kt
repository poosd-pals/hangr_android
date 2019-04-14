package com.cop4331.group7.hangr.classes

import android.content.Context
import android.support.v4.widget.CircularProgressDrawable

class CreateCircularDrawable {
    companion object {
        fun make(context: Context, radius: Float = 50f): CircularProgressDrawable {
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = radius
            circularProgressDrawable.start()
            return circularProgressDrawable
        }
    }
}