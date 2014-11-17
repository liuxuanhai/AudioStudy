package com.example.coder;

import java.nio.ByteBuffer;

import com.example.Config;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;

public class MediaExtractorActivity extends Activity implements Runnable {

	private static final long TIMEOUT_US = 50;

	private static final int NO_INPUT_BUFFER = -1;

	private static final String TAG = "MediaDecodecActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new Thread(this).start();
	}

	@Override
	public void run() {
		MediaCodec mediaCodec = null;
		MediaExtractor mediaExtractor = null;
		try {
			boolean flag = true;
			
			mediaExtractor = new MediaExtractor();
			mediaExtractor.setDataSource(Config.URI_MP3);
			int count = mediaExtractor.getTrackCount();
			if (count < 1) {
				return;
			}
			
			int index = 0;
			MediaFormat format = mediaExtractor.getTrackFormat(index);
			Log.d(TAG, " MediaFormat: " + format);
			
			mediaExtractor.selectTrack(index);
 			mediaCodec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
 			mediaCodec.configure(format, null, null, 0);
			mediaCodec.start();

			ByteBuffer[] codecInputBuffers = mediaCodec.getInputBuffers();
			ByteBuffer[] codecOutputBuffers = mediaCodec.getOutputBuffers();

			int streamType = AudioManager.STREAM_MUSIC;
			int sampleRateInHz = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
 			int channelConfig  =  format.getInteger(MediaFormat.KEY_CHANNEL_COUNT) == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
			int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat) * 2;
			int mode = AudioTrack.MODE_STREAM;
			AudioTrack audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, mode);
			audioTrack.play();
			
			byte[] byteBuffer = new byte[bufferSizeInBytes];
			int size = 0;
			while (flag && !isFinishing()) {
				feedInput(mediaCodec, mediaExtractor, codecInputBuffers);

				BufferInfo info = new BufferInfo();
				int bufferIndex = mediaCodec.dequeueOutputBuffer(info, TIMEOUT_US);
 				if (bufferIndex < 0) {
					continue;
				}
				if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
					flag = false;
					Log.d(TAG, "endStream");
				}

				ByteBuffer buffer = codecOutputBuffers[bufferIndex];
				size = info.size;
				if (byteBuffer.length < size) {
					byteBuffer = new byte[size];
				}
				
 				buffer.position(info.offset);
				buffer.get(byteBuffer, 0, size);
				audioTrack.write(byteBuffer, 0, size);
 				buffer.clear();
				mediaCodec.releaseOutputBuffer(bufferIndex, false);
			}

  		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mediaCodec != null) {
				mediaCodec.stop();
				mediaCodec.release();
			}
			
			if (mediaExtractor != null) {
				mediaExtractor.release();
			}
		}
	}

	public boolean feedInput(MediaCodec mediaCodec, MediaExtractor mediaExtractor, ByteBuffer[] codecInputBuffers) {
		long presentationTimeUs = 0;

		int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_US);
		if (inputBufferIndex != NO_INPUT_BUFFER) {
			ByteBuffer destinationBuffer = codecInputBuffers[inputBufferIndex];
			int sampleSize = mediaExtractor.readSampleData(destinationBuffer, 0);
			if (sampleSize < 0) {
				Log.w(TAG, "Media extractor had sample but no data.");
				mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
				return false;
			}

			presentationTimeUs = mediaExtractor.getSampleTime();
			mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, presentationTimeUs, 0);

			return mediaExtractor.advance();
		}
		return false;
	}
}
