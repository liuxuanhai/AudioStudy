package com.example.testopensl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.Config;
import com.example.audio.R;

public class AudioTrackActivity extends Activity implements OnClickListener {

	static {
		System.loadLibrary("TestAudio");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_track);
		findViewById(R.id.btn_play).setOnClickListener(this);
		createEngine();
		createAudioPlayer(Config.URI_PCM);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_play:
			setPlayingAudioPlayer(true);
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		shutdown();
	}

	/** Native methods, implemented in jni folder */
	public static native void createEngine();

	public static native boolean createAudioPlayer(String uri);

	public static native void setPlayingAudioPlayer(boolean isPlaying);

	public static native void setVolumeAudioPlayer(int millibel);

	public static native void setMutAudioPlayer(boolean mute);

	public static native void shutdown();
}
