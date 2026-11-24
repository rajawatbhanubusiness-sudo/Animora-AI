
package com.animora.ai

import android.content.Context
import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import java.nio.ByteOrder

data class DecodedAudio(
    val samples: ShortArray,
    val sampleRate: Int,
    val durationMs: Long
)

object AudioDecoder {

    private const val TARGET_SAMPLE_RATE = 11025
    private const val MAX_DURATION_MS = 600000L

    fun decode(
        context: Context,
        audioUri: Uri
    ): DecodedAudio? {

        val extractor = MediaExtractor()
        var codec: MediaCodec? = null

        try {
            extractor.setDataSource(context, audioUri)

            var audioTrack = -1

            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME)

                if (mime?.startsWith("audio/") == true) {
                    audioTrack = i
                    break
                }
            }

            if (audioTrack < 0) return null

            extractor.selectTrack(audioTrack)

            val inputFormat = extractor.getTrackFormat(audioTrack)
            val mime = inputFormat.getString(MediaFormat.KEY_MIME)
                ?: return null

            val durationMs =
                if (inputFormat.containsKey(MediaFormat.KEY_DURATION)) {
                    inputFormat.getLong(MediaFormat.KEY_DURATION) / 1000L
                } else {
                    0L
                }

            if (durationMs <= 0L || durationMs > MAX_DURATION_MS) {
                return null
            }

            codec = MediaCodec.createDecoderByType(mime)
            codec.configure(inputFormat, null, null, 0)
            codec.start()

            val output = ShortArrayBuilder()
            val info = MediaCodec.BufferInfo()

            var sampleRate = 0
            var channelCount = 0
            var pcmEncoding = AudioFormat.ENCODING_PCM_16BIT

            var inputFinished = false
            var outputFinished = false
            var frameIndex = 0L
            var nextOutputFrame = 0L

            while (!outputFinished) {

                if (!inputFinished) {
                    val inputIndex = codec.dequeueInputBuffer(10000)

                    if (inputIndex >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputIndex)
                            ?: return null

                        inputBuffer.clear()

                        val sampleSize = extractor.readSampleData(
                            inputBuffer, 0
                        )

                        if (sampleSize < 0) {
                            codec.queueInputBuffer(
                                inputIndex,
                                0,
                                0,
                                0L,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM
                            )
                            inputFinished = true
                        } else {
                            codec.queueInputBuffer(
                                inputIndex,
                                0,
                                sampleSize,
                                extractor.sampleTime,
                                0
                            )
                            extractor.advance()
                        }
                    }
                }

                when (val outputIndex =
                    codec.dequeueOutputBuffer(info, 10000)) {

                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        val format = codec.outputFormat

                        sampleRate = format.getInteger(
                            MediaFormat.KEY_SAMPLE_RATE
                        )

                        channelCount = format.getInteger(
                            MediaFormat.KEY_CHANNEL_COUNT
                        )

                        if (format.containsKey(
                                MediaFormat.KEY_PCM_ENCODING
                            )
                        ) {
                            pcmEncoding = format.getInteger(
                                MediaFormat.KEY_PCM_ENCODING
                            )
                        }
                    }

                    MediaCodec.INFO_TRY_AGAIN_LATER -> Unit

                    else -> {
                        if (outputIndex >= 0) {
                            val buffer = codec.getOutputBuffer(outputIndex)

                            if (buffer != null && info.size > 0) {
                                if (sampleRate <= 0 || channelCount <= 0) {
                                    return null
                                }

                                if (
                                    pcmEncoding != AudioFormat.ENCODING_PCM_16BIT &&
                                    pcmEncoding != AudioFormat.ENCODING_PCM_FLOAT
                                ) {
                                    return null
                                }

                                buffer.position(info.offset)
                                buffer.limit(info.offset + info.size)
                                buffer.order(ByteOrder.nativeOrder())

                                val bytesPerSample =
                                    if (pcmEncoding ==
                                        AudioFormat.ENCODING_PCM_FLOAT
                                    ) 4 else 2

                                val frameBytes =
                                    channelCount * bytesPerSample

                                val frameCount = info.size / frameBytes

                                val step = (
                                    sampleRate.toDouble() /
                                    TARGET_SAMPLE_RATE
                                ).coerceAtLeast(1.0)

                                for (frame in 0 until frameCount) {
                                    if (frameIndex >= nextOutputFrame) {
                                        var total = 0.0

                                        for (channel in 0 until channelCount) {
                                            val value =
                                                if (pcmEncoding ==
                                                    AudioFormat.ENCODING_PCM_FLOAT
                                                ) {
                                                    buffer.getFloat()
                                                } else {
                                                    buffer.getShort() / 32768.0
                                                }

                                            total += value
                                        }

                                        val mono = total / channelCount

                                        output.add(
                                            (mono * 32767.0)
                                                .toInt()
                                                .coerceIn(-32768, 32767)
                                                .toShort()
                                        )

                                        nextOutputFrame =
                                            (frameIndex + 1 +
                                                ((nextOutputFrame -
                                                    frameIndex) / step)
                                                    .toLong().coerceAtLeast(0))
                                    } else {
                                        buffer.position(
                                            buffer.position() + frameBytes
                                        )
                                    }

                                    frameIndex++
                                }
                            }

                            if (
                                info.flags and
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                            ) {
                                outputFinished = true
                            }

                            codec.releaseOutputBuffer(outputIndex, false)
                        }
                    }
                }

                if (outputFinished) break
            }

            if (sampleRate <= 0) return null

            return DecodedAudio(
                samples = output.toShortArray(),
                sampleRate = TARGET_SAMPLE_RATE,
                durationMs = durationMs
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            try {
                codec?.stop()
            } catch (_: Exception) {
            }

            codec?.release()
            extractor.release()
        }
    }

    private class ShortArrayBuilder {
        private var data = ShortArray(4096)
        private var size = 0

        fun add(value: Short) {
            if (size >= data.size) {
                data = data.copyOf(data.size * 2)
            }

            data[size++] = value
        }

        fun toShortArray(): ShortArray {
            return data.copyOf(size)
        }
    }
}
