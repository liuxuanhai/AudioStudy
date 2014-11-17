package com.example.framework;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;

import com.example.Config;

public class AudioTrackActivity extends Activity implements Runnable {
	private Thread mPlaybackThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPlaybackThread = new Thread(this);
		mPlaybackThread.start();
	}

	@Override
	public void run() {
		int streamType = AudioManager.STREAM_MUSIC;
		int sampleRateInHz = 44100;
		int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
		int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat) * 2;
		int mode = AudioTrack.MODE_STREAM;
		AudioTrack audioTrack = new AudioTrack(streamType, sampleRateInHz, channelConfig,
												audioFormat, bufferSizeInBytes, mode);
		BufferedInputStream bis = null;
		try {
			audioTrack.play();
			bis = new BufferedInputStream(new FileInputStream(Config.URI_PCM));
			byte[] buffer = new byte[bufferSizeInBytes];
			int len = 0;
			while ((len = bis.read(buffer)) != -1 && !isFinishing()) {
				audioTrack.write(buffer, 0, len);
 			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			audioTrack.release();
		}
	}

}
