package com.example.framework;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;

public class AudioRecordActivity extends Activity implements Runnable {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		new Thread(this).start();
	}

	@Override
	public void run() {
		BufferedOutputStream bos = null;
		AudioRecord audioRecord = null;
		try {
			int audioSource = AudioSource.MIC;
			int sampleRateInHz = 44100;
			int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
			int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
			int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat) * 2;
			audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
			audioRecord.startRecording();
			bos = new BufferedOutputStream(new FileOutputStream(new File(getCacheDir(), "audio.pcm")));
			byte[] buffer = new byte[bufferSizeInBytes];
			int len = 0;
			while (!isFinishing()) {
				len = audioRecord.read(buffer, 0, bufferSizeInBytes);
 				if (len <= 0) {
					break;
				}
				bos.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {}
			}
			if (audioRecord != null) {
				audioRecord.release();
			}
		}
	}

}
