package com.animora.ai

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

object VideoProcessor {

    fun extractFrames(
        context: Context,
        videoUri: Uri,
        maxFrames: Int = 10
    ): List<Bitmap> {

        val retriever = MediaMetadataRetriever()
        val frames = mutableListOf<Bitmap>()

        try {
            retriever.setDataSource(context, videoUri)

            val duration = retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: return emptyList()

            if (duration <= 0L) return emptyList()

            val frameCount = maxFrames.coerceIn(1, 10)

            for (i in 0 until frameCount) {
                val timeUs = (duration * 1000L * i) / frameCount

                val bitmap = retriever.getFrameAtTime(
                    timeUs,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )

                if (bitmap != null) {
                    frames.add(bitmap)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }

        return frames
    }
}