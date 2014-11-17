package com.example.openmax;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;
import android.widget.Button;

import com.example.Config;
import com.example.audio.R;

public class OpenMaxActivity extends Activity {

	static {
		System.loadLibrary("TestAudio");
	}

	static final String TAG = "OpenMaxActivity";

 	private String mSinkString = null;

	private boolean mMediaPlayerIsPrepared = false;

	// member variables for native media player
	private boolean mIsPlayingStreaming = false;

	private GLViewVideoSink mNativeMediaPlayerVideoSink;
	private MyGLSurfaceView mGLView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.open_max_activity);
		
		mGLView = (MyGLSurfaceView) findViewById(R.id.glsurfaceview1);
		mNativeMediaPlayerVideoSink = new GLViewVideoSink(mGLView);
		// initialize native media system
		createEngine();
		// native MediaPlayer start/pause

		((Button) findViewById(R.id.start_native)).setOnClickListener(new View.OnClickListener() {

			boolean created = false;

			public void onClick(View view) {
				if (!created) {
					mNativeMediaPlayerVideoSink.useAsSinkForNative();
					created = createStreamingMediaPlayer(Config.URI_MPEG2_TS);
				}
				if (created) {
					mIsPlayingStreaming = !mIsPlayingStreaming;
					setPlayingStreamingMediaPlayer(mIsPlayingStreaming);
				}
			}

		});

		// native MediaPlayer rewind

		((Button) findViewById(R.id.rewind_native)).setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (mNativeMediaPlayerVideoSink != null) {
					rewindStreamingMediaPlayer();
				}
			}

		});

	}

	/** Called when the activity is about to be paused. */
	@Override
	protected void onPause() {
		mIsPlayingStreaming = false;
		setPlayingStreamingMediaPlayer(false);
		mGLView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	/** Called when the activity is about to be destroyed. */
	@Override
	protected void onDestroy() {
		shutdown();
		super.onDestroy();
	}

	static class GLViewVideoSink {

		private final MyGLSurfaceView mMyGLSurfaceView;

		GLViewVideoSink(MyGLSurfaceView myGLSurfaceView) {
			mMyGLSurfaceView = myGLSurfaceView;
		}

		void useAsSinkForNative() {
			SurfaceTexture st = mMyGLSurfaceView.getSurfaceTexture();
			Surface s = new Surface(st);
			setSurface(s);
			s.release();
		}

	}

	/** Native methods, implemented in jni folder */
	public static native void createEngine();

	public static native boolean createStreamingMediaPlayer(String filename);

	public static native void setPlayingStreamingMediaPlayer(boolean isPlaying);

	public static native void setSurface(Surface surface);

	public static native void rewindStreamingMediaPlayer();

	public static native void shutdown();
}
