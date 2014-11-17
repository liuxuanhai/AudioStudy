package com.example.pcm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.Config;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;

public class EncodeConvertActivity extends Activity implements Runnable {
	private static final String TAG = "MainActivity";
	private AudioTrack mAudioTrack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				8192, AudioTrack.MODE_STREAM);
		mAudioTrack.play();
		new Thread(this).start();
	}

	@Override
	public void run() { 

		InputStream inputStream = null;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(Config.URI_PCM_8BIT));

			byte[] buffer = new byte[8192];
			int len = 0;
			int frameSize = 2;
			byte[] bufferSend = new byte[8192 * 2];
			int index = 0;
			int lv = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				for (int i = 0; i < len; i++) {
					index = i << 1;
					lv = buffer[i] << 8;
					bufferSend[index] = (byte) (lv & 0xFF);
					bufferSend[index + 1] = (byte) (lv >> 8);
				}
				mAudioTrack.write(bufferSend, 0, len * frameSize);
				// Log.d(TAG, " write len " + len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAudioTrack.release();
	}

}
