package com.example.pcm;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.Config;
import com.example.audio.R;

public class VolumeActivity extends Activity implements OnClickListener, Runnable {
	private AudioTrack mAudioTrack;
	private volatile float volume = 1.0f;
	private TextView mVolumeTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_volume);

		mVolumeTextView = (TextView) findViewById(R.id.textView1);
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		new Thread(this).start();
		updateVolumeText();
	}

	@Override
	public void run() {

		InputStream inputStream = null;
		try {
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
					8192, AudioTrack.MODE_STREAM);
			mAudioTrack.play();
			inputStream = new BufferedInputStream(new FileInputStream(Config.URI_PCM));

			byte[] buffer = new byte[8192];
			int len = 0;
			int frameSize = 2;
			int index = 0;
			int lv = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				for (int i = 0; i < (len / frameSize); i++) {
					index = i << 1;
					lv = buffer[index] & 0xFF | buffer[index + 1] << 8;
					lv *= volume;
					buffer[index] = (byte) (lv & 0xFF);
					buffer[index + 1] = (byte) (lv >> 8);
				}
				mAudioTrack.write(buffer, 0, len);
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

	private void updateVolumeText() {
		mVolumeTextView.setText(String.valueOf(volume));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button2:
			volume -= 0.1f;
			if (volume < 0) {
				volume = 0;
			}
			break;
		case R.id.button1:
			volume += 0.1f;
			if (volume > 1) {
				volume = 1f;
			}
			break;
		}
		updateVolumeText();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAudioTrack.release();
	}

}
