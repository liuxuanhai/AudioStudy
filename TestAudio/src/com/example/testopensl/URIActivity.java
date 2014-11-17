package com.example.testopensl;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.Config;
import com.example.audio.R;

public class URIActivity extends Activity implements OnClickListener {

	private boolean mMuteLeft = false;
	private boolean mMuteRight = false;

	static {
		System.loadLibrary("TestAudio");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		createEngine();
		findViewById(R.id.uri_soundtrack).setOnClickListener(this);
		findViewById(R.id.pause_uri).setOnClickListener(this);
		findViewById(R.id.play_uri).setOnClickListener(this);
		findViewById(R.id.uri_mute).setOnClickListener(this);
		findViewById(R.id.uri_unmute).setOnClickListener(this);
		findViewById(R.id.uri_solo_left).setOnClickListener(this);
		findViewById(R.id.uri_solo_right).setOnClickListener(this);
		findViewById(R.id.uri_mute_left).setOnClickListener(this);
		findViewById(R.id.uri_mute_right).setOnClickListener(this);
		findViewById(R.id.uri_get_channel_num).setOnClickListener(this);
		((SeekBar)findViewById(R.id.volume_uri)).setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int attenuation = 100 - seekBar.getProgress();
				int millibel = attenuation * -50;
				setVolumeUriAudioPlayer(millibel);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
 				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
			}
		});;
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.uri_soundtrack:
			createUriAudioPlayer(Config.URI_MP3);
			break;
		case R.id.pause_uri:
			setPlayingUriAudioPlayer(false);
			break;
		case R.id.play_uri:
			setPlayingUriAudioPlayer(true);
			break;
		case R.id.uri_mute:
			setMuteUriAudioPlayer(true);
			break;
		case R.id.uri_unmute:
			setMuteUriAudioPlayer(false);
			break;
		case R.id.uri_solo_left:
			setChannelSoloUriAudioPlayer(0, true);
			break;
		case R.id.uri_solo_right:
			setChannelSoloUriAudioPlayer(1, true); 
			break;
		case R.id.uri_mute_left:
			setChannelMuteUriAudioPlayer(0, mMuteLeft = !mMuteLeft);
			break;
		case R.id.uri_mute_right:
			setChannelMuteUriAudioPlayer(1, mMuteRight = !mMuteRight);
			break;
		case R.id.uri_get_channel_num:
			int num = getNumChannelsUriAudioPlayer();
			Toast.makeText(this, "声道数量 ：" + num, Toast.LENGTH_SHORT).show();
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

	public static native boolean createUriAudioPlayer(String uri);

	public static native void setPlayingUriAudioPlayer(boolean isPlaying);

	public static native void setChannelMuteUriAudioPlayer(int chan, boolean mute);

	public static native void setChannelSoloUriAudioPlayer(int chan, boolean solo);

	public static native int getNumChannelsUriAudioPlayer();

	public static native void setVolumeUriAudioPlayer(int millibel);

	public static native void setMuteUriAudioPlayer(boolean mute);

	public static native void enableStereoPositionUriAudioPlayer(boolean enable);

	public static native void setStereoPositionUriAudioPlayer(int permille);

	public static native void shutdown();

}
