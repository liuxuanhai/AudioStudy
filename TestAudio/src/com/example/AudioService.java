package com.example;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AudioService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Notification notification = new Notification();
 		notification.tickerText = "音乐";
		startForeground(5, notification);
	}

}
