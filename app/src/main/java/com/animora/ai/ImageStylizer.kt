
package com.animora.ai

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.roundToInt

object ImageStylizer {

    fun applyStyle(source: Bitmap, selectedStyle: String): Bitmap =
        when {
            selectedStyle.contains("Anime", true) -> applyAnime(source)
            selectedStyle.contains("3D Cartoon", true) -> apply3DCartoon(source)
            selectedStyle.contains("2D Cartoon", true) -> apply2DCartoon(source)
            selectedStyle.contains("Watercolor", true) -> applyWatercolor(source)
            selectedStyle.contains("Clay Animation", true) -> applyClay(source)
            selectedStyle.contains("Comic Book", true) -> applyComicBook(source)
            selectedStyle.contains("Pixel Art", true) -> applyPixelArt(source)
            selectedStyle.contains("Realistic Animation", true) -> applyRealistic(source)
            selectedStyle.contains("Fantasy Animation", true) -> applyFantasy(source)
            selectedStyle.contains("Vintage Cartoon", true) -> applyVintage(source)
            selectedStyle.contains("Paper Cutout", true) -> applyPaperCutout(source)
            selectedStyle.contains("Cyberpunk", true) -> applyCyberpunk(source)
            else -> source.copy(Bitmap.Config.ARGB_8888, false)
        }

    fun applyPixelArt(source: Bitmap, pixelSize: Int = 12): Bitmap {
        val size = pixelSize.coerceIn(2, 40)
        val small = Bitmap.createScaledBitmap(
            source,
            (source.width / size).coerceAtLeast(1),
            (source.height / size).coerceAtLeast(1),
            false
        )
        return Bitmap.createScaledBitmap(small, source.width, source.height, false)
    }

    fun applyAnime(source: Bitmap) = transform(source) { r, g, b ->
        rgb(posterize((r * 1.12f + 8f).coerceAtMost(255f), 6),
            posterize((g * 1.08f + 5f).coerceAtMost(255f), 6),
            posterize((b * 1.14f + 8f).coerceAtMost(255f), 6))
    }

    fun apply3DCartoon(source: Bitmap) = transform(source) { r, g, b ->
        rgb(posterize(contrast(r, 1.18f), 8),
            posterize(contrast(g, 1.18f), 8),
            posterize(contrast(b, 1.18f), 8))
    }

    fun apply2DCartoon(source: Bitmap) = transform(source) { r, g, b ->
        rgb(posterize(r, 5), posterize(g, 5), posterize(b, 5))
    }

    fun applyWatercolor(source: Bitmap) = transform(source) { r, g, b ->
        val light = (r + g + b) / 3f
        rgb(mix(r, light, 0.18f), mix(g, light, 0.18f), mix(b, light, 0.18f))
    }

    fun applyClay(source: Bitmap) = transform(source) { r, g, b ->
        rgb(contrast(r * 1.06f, 1.08f),
            contrast(g * 1.02f, 1.08f),
            contrast(b * 0.92f, 1.08f))
    }

    fun applyComicBook(source: Bitmap) = transform(source) { r, g, b ->
        val gray = 0.299f * r + 0.587f * g + 0.114f * b
        val ink = if (gray > 145f) 235 else 35
        rgb(ink.toFloat(), ink.toFloat(), ink.toFloat())
    }

    fun applyRealistic(source: Bitmap) = transform(source) { r, g, b ->
        rgb(contrast(r, 1.06f), contrast(g, 1.06f), contrast(b, 1.06f))
    }

    fun applyFantasy(source: Bitmap) = transform(source) { r, g, b ->
        rgb(mix(r, b, 0.12f), mix(g, 120f, 0.08f), mix(b, 255f, 0.18f))
    }

    fun applyVintage(source: Bitmap) = transform(source) { r, g, b ->
        val sr = 0.393f * r + 0.769f * g + 0.189f * b
        val sg = 0.349f * r + 0.686f * g + 0.168f * b
        val sb = 0.272f * r + 0.534f * g + 0.131f * b
        rgb(sr * 0.92f, sg * 0.86f, sb * 0.72f)
    }

    fun applyPaperCutout(source: Bitmap) = transform(source) { r, g, b ->
        rgb(posterize(r, 3), posterize(g, 3), posterize(b, 3))
    }

    fun applyCyberpunk(source: Bitmap) = transform(source) { r, g, b ->
        rgb((r * 1.18f + 18f).coerceAtMost(255f),
            (g * 0.88f).coerceAtMost(255f),
            (b * 1.28f + 20f).coerceAtMost(255f))
    }

    private fun transform(
        source: Bitmap,
        effect: (Float, Float, Float) -> Int
    ): Bitmap {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            pixels[i] = effect(
                Color.red(pixel).toFloat(),
                Color.green(pixel).toFloat(),
                Color.blue(pixel).toFloat()
            )
        }

        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
            it.setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    private fun posterize(value: Float, levels: Int): Float {
        val step = 255f / (levels - 1)
        return ((value.coerceIn(0f, 255f) / step).roundToInt() * step)
            .coerceIn(0f, 255f)
    }

    private fun contrast(value: Float, factor: Float): Float =
        ((value - 128f) * factor + 128f).coerceIn(0f, 255f)

    private fun mix(a: Float, b: Float, amount: Float): Float =
        (a * (1f - amount) + b * amount).coerceIn(0f, 255f)

    private fun rgb(r: Float, g: Float, b: Float): Int =
        Color.rgb(
            r.roundToInt().coerceIn(0, 255),
            g.roundToInt().coerceIn(0, 255),
            b.roundToInt().coerceIn(0, 255)
        )
}
