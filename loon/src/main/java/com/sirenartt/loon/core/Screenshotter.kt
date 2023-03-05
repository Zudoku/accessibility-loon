package com.sirenartt.loon.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.Rect
import android.os.Build
import android.view.View
import com.google.android.libraries.accessibility.utils.log.LogUtils


internal class Screenshotter {
    /**
     * Returns a Bitmap with a rendering of the visible portion of the view and all of its children.
     * In some cases, the Bitmap may be smaller than the size of the display. This assumes that the
     * content of the display below and to the right of the View are of no interest.
     */
    fun getScreenshot(view: View): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getScreenShotPPlus(view)
        } else {
            getScreenshotPreP(view)
        }
    }

    companion object {

        private fun getScreenShotPPlus(view: View): Bitmap {
            val picture = Picture()
            val windowOffset = getWindowOffset(view)
            val canvas: Canvas = picture.beginRecording(
                windowOffset[0] + view.width, windowOffset[1] + view.height
            )
            view.computeScroll()
            canvas.translate(
                windowOffset[0] - view.scrollX.toFloat(),
                windowOffset[1] - view.scrollY.toFloat()
            )
            view.draw(canvas)

            // End recording before creating the Bitmap so that the Picture is fully initialized prior to
            // creating the Bitmap copy below. Matches the previous call to beginRecording. See b/80539264.
            picture.endRecording()
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Bitmap.createBitmap(
                    picture, picture.width, picture.height, Bitmap.Config.ARGB_8888
                )
            } else {
                throw RuntimeException()
            }
        }

        private fun getScreenshotPreP(view: View): Bitmap {
            // The drawing cache is a cheap, easy and compatible way to generate our screenshot.
            // However, because the cache is capped at a maximum size, this method may not work
            // for large views. So, we first try the drawing cache, and then if that fails
            // we fall back to building the screenshot bitmap manually.
            view.buildDrawingCache()
            var bitmap = view.drawingCache
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                bitmap.density = view.resources.displayMetrics.densityDpi
                view.computeScroll()
                val canvas = Canvas(bitmap)
                canvas.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
                view.draw(canvas)
            } else {
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)
                view.destroyDrawingCache()
            }
            return expandBitmapIfWindowOffset(bitmap, view)
        }

        // If the window is offset, expand the bitmap
        private fun expandBitmapIfWindowOffset(bitmap: Bitmap, view: View): Bitmap {
            val windowOffset = getWindowOffset(view)
            if (windowOffset[0] != 0 || windowOffset[1] != 0) {
                val destRect = Rect()
                if (view.getGlobalVisibleRect(destRect)) {
                    val biggerBitmap = Bitmap.createBitmap(
                        view.width + windowOffset[0],
                        view.height + windowOffset[1],
                        Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(biggerBitmap)
                    destRect.offset(windowOffset[0], windowOffset[1])
                    canvas.drawBitmap(bitmap,  /* src= */null, destRect,  /* paint= */null)
                    return biggerBitmap
                }
            }
            return bitmap
        }

        private fun getWindowOffset(view: View): IntArray {
            val locationOnScreen = IntArray(2)
            val locationInWindow = IntArray(2)
            view.getLocationOnScreen(locationOnScreen)
            view.getLocationInWindow(locationInWindow)

            // Usually these offsets will be zero, except when the view is in a dialog window.
            val xOffset = locationOnScreen[0] - locationInWindow[0]
            val yOffset = locationOnScreen[1] - locationInWindow[1]
            return intArrayOf(xOffset, yOffset)
        }
    }
}