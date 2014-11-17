package com.example.coder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.util.Log;

public class AACADTSEncoderActivity extends Activity implements Runnable{
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
		try {
			boolean flag = true;

 			int sampleRateInHz = 44100;
			int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
			int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat) * 2;
 
			MediaFormat format = MediaFormat.createAudioFormat("audio/mp4a-latm", sampleRateInHz, 2);
 			format.setInteger(MediaFormat.KEY_BIT_RATE, 128000);
 			format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
			format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, bufferSizeInBytes);
			
			mediaCodec = MediaCodec.createEncoderByType("audio/mp4a-latm");
			mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
			mediaCodec.start();

			ByteBuffer[] codecInputBuffers = mediaCodec.getInputBuffers();
			ByteBuffer[] codecOutputBuffers = mediaCodec.getOutputBuffers();

			 
			//AudioRecord audioRecord = new AudioRecord(AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
			//audioRecord.startRecording();
			
			byte[] byteBuffer = new byte[bufferSizeInBytes];
			
			FileOutputStream fileOutputStream = new FileOutputStream(new File(getCacheDir(), "out.adts"));
			FileInputStream fis = new FileInputStream("/mnt/sdcard/pm.pcm");
			int size = 0;
			while (flag && !isFinishing()) {
				feedInput(mediaCodec, fis, codecInputBuffers);

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
				int outPacketSize  = size + 7;
				if (byteBuffer.length < outPacketSize) {
					byteBuffer = new byte[outPacketSize];
				}
				
 				addADTStoPacket(byteBuffer, outPacketSize);
				buffer.position(info.offset);
				buffer.get(byteBuffer, 7, size);
				
				Log.d(TAG, "size " + size + " outPacketSize:" + outPacketSize);
				
				fileOutputStream.write(byteBuffer, 0, outPacketSize);
				
				buffer.clear();
				mediaCodec.releaseOutputBuffer(bufferIndex, false);
			}
			fileOutputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (mediaCodec != null) {
				mediaCodec.stop();
				mediaCodec.release();
			}

		}
	}

	public boolean feedInput(MediaCodec mediaCodec, FileInputStream fis, ByteBuffer[] codecInputBuffers) throws IOException {

		int inputBufferIndex = mediaCodec.dequeueInputBuffer(TIMEOUT_US);
		if (inputBufferIndex != NO_INPUT_BUFFER) {
			ByteBuffer destinationBuffer = codecInputBuffers[inputBufferIndex];
			byte[] buffer = new byte[destinationBuffer.capacity()];
			 int sampleSize = fis.read(buffer);
			 destinationBuffer.position(0);
			 destinationBuffer.put(buffer, 0, sampleSize);
 			//int sampleSize = record.read(destinationBuffer,  destinationBuffer.capacity());
			if (sampleSize < 0) {
				Log.w(TAG, "Media extractor had sample but no data.");
				mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
				return false;
			}
			Log.d(TAG, " queueInputBuffer " + inputBufferIndex + " sampleSize " + sampleSize);
			mediaCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, System.nanoTime()/1000, 0);

			return true;
		}
		return false;
	}
	
	private void addADTStoPacket(byte[] packet, int packetLen) {
	    int profile = 2;  //AAC LC
	                      //39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
	    int freqIdx = 4;  //44.1KHz
	    int chanCfg = 2;  //CPE

	    // fill in ADTS data
	    packet[0] = (byte)0xFF;
	    packet[1] = (byte)0xF9;
	    packet[2] = (byte)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>2));
	    packet[3] = (byte)(((chanCfg&3)<<6) + (packetLen>>11));
	    packet[4] = (byte)((packetLen&0x7FF) >> 3);
	    packet[5] = (byte)(((packetLen&7)<<5) + 0x1F);
	    packet[6] = (byte)0xFC;
	}
}
