package com.example;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.audio.R;

public class MainActivity extends Activity implements OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		startService(new Intent(this, AudioService.class));
		ListView listView = (ListView) findViewById(R.id.listView);
		List<ActivityItem> activity = loadActivity();
		ArrayAdapter<ActivityItem> arrayAdapter = new ArrayAdapter<ActivityItem>(this, android.R.layout.simple_list_item_1,
				android.R.id.text1, activity);
		listView.setAdapter(arrayAdapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ActivityItem activityItem = (ActivityItem) parent.getItemAtPosition(position);
		ComponentName component = new ComponentName(activityItem.activityInfo.packageName, activityItem.activityInfo.name);
		Intent intent = new Intent();
		intent.setComponent(component);
		startActivity(intent);
	}

	private List<ActivityItem> loadActivity() {

		ArrayList<ActivityItem> result = new ArrayList<ActivityItem>();

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory("com.example.audio.SAMPLE_CODE");
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> intentActivities = packageManager.queryIntentActivities(mainIntent, 0);
		if (intentActivities == null) {
			return result;
		}

		for (ResolveInfo info : intentActivities) {
			ActivityItem activityItem = new ActivityItem();
			activityItem.title = info.activityInfo.loadLabel(packageManager).toString();
			activityItem.activityInfo = info.activityInfo;
			result.add(activityItem);
		}

		return result;
	}

	public static class ActivityItem {
		private String title;
		private ActivityInfo activityInfo;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}
	}
}
