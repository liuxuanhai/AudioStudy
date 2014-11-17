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

public class MixerActivity extends Activity implements Runnable {

	private static final int MAX_BIT = Short.MAX_VALUE;
	private static final int MIN_BIT = Short.MIN_VALUE;
	
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
		InputStream inputStream1 = null;
		InputStream inputStream2 = null;
		try {
			inputStream1 = new BufferedInputStream(new FileInputStream(Config.URI_PCM));
			inputStream2 = new BufferedInputStream(new FileInputStream(Config.URI_PCM_2));

			byte[] buffer = new byte[8192];
			int len = 0;
			int frameSize = 2;
			int index = 0;
			int lv = 0;
			byte[] buffer2 = new byte[8192];
			int len2 = 0;
 			while ((len = inputStream1.read(buffer)) != -1) {
				if ( (len2 = inputStream2.read(buffer2)) != -1 ) {
					for (int i = 0; i < (len / frameSize); i++) {
						index = i << 1;
						if (index >= len2) {
							break;
						}
						lv = buffer[index] & 0xFF | buffer[index + 1] << 8;
						lv += (buffer2[index] & 0xFF | buffer2[index + 1] << 8);
						if (lv > MAX_BIT) {
							lv = MAX_BIT;
						} else if (lv < MIN_BIT) {
							lv = MIN_BIT;
						}
						buffer[index] = (byte) (lv & 0xFF);
						buffer[index + 1] = (byte) (lv >> 8);
					}
				}
				mAudioTrack.write(buffer, 0, len);
				// Log.d(TAG, " write len " + len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream1 != null) {
				try {
					inputStream1.close();
				} catch (IOException e) {
				}
			}
			if (inputStream2 != null) {
				try {
					inputStream2.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//mAudioTrack.release();
	}

}
