
package com.animora.ai

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

object AudioProcessor {

    fun getAudioDurationMs(
        context: Context,
        audioUri: Uri
    ): Long? {

        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(context, audioUri)

            retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull()?.takeIf { it > 0L }

        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}
