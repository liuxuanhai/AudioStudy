package com.example.framework;

import java.io.File;

import android.app.Activity;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.Bundle;

public class MediaRecorderActivity extends Activity {
	private MediaRecorder mRediaRecorder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			String path = new File(getCacheDir(), "audio.mp4").getAbsolutePath();
			mRediaRecorder = new MediaRecorder();
			mRediaRecorder.setAudioChannels(2);
			mRediaRecorder.setAudioSource(AudioSource.MIC);
			mRediaRecorder.setAudioSamplingRate(44100);
			mRediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
			mRediaRecorder.setOutputFile(path);
			mRediaRecorder.setAudioEncoder(AudioEncoder.AAC);
			mRediaRecorder.prepare();
			mRediaRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mRediaRecorder != null) {
			mRediaRecorder.release();
		}
	}
}

