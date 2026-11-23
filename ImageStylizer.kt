
package com.animora.ai

import android.graphics.Bitmap

object ImageStylizer {

    fun applyPixelArt(
        source: Bitmap,
        pixelSize: Int = 12
    ): Bitmap {

        val safePixelSize = pixelSize.coerceIn(2, 40)

        val smallWidth =
            (source.width / safePixelSize).coerceAtLeast(1)

        val smallHeight =
            (source.height / safePixelSize).coerceAtLeast(1)

        val smallBitmap = Bitmap.createScaledBitmap(
            source,
            smallWidth,
            smallHeight,
            false
        )

        return Bitmap.createScaledBitmap(
            smallBitmap,
            source.width,
            source.height,
            false
        )
    }
}
