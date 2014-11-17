package com.example.framework;

import com.example.Config;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;

public class SoundPoolActivity extends Activity implements OnLoadCompleteListener {
	private SoundPool mSoundPool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int maxStreams = 1;
		int streamType = AudioManager.STREAM_MUSIC;
		int srcQuality = 0;
		mSoundPool = new SoundPool(maxStreams, streamType, srcQuality);
		String path = Config.URI_ALARM;
		int priority = 0;
		mSoundPool.load(path, priority);
		mSoundPool.setOnLoadCompleteListener(this);
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		float leftVolume = 1.0f;
		float rightVolume = 1.0f;
		int priority = 1;
		int loop = 0;
		float rate = 1.0f;
		mSoundPool.play(sampleId, leftVolume, rightVolume, priority, loop, rate);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mSoundPool != null) {
			mSoundPool.release();
		}
	}
}
