package com.nuevo.flo;


import android.app.Activity;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;

/**
 * Displays a need and its prayer.
 */
public class PrayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vision);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Uri uri = getIntent().getData();
		Cursor cursor = managedQuery(uri, null, null, null, null);

		if (cursor == null) {
			finish();
		} else {
			cursor.moveToFirst();

			TextView requests = (TextView) findViewById(R.id.requests);
			TextView prayers = (TextView) findViewById(R.id.prayers);
			prayers.setMovementMethod(ScrollingMovementMethod.getInstance());

			int wIndex = cursor.getColumnIndexOrThrow(PrayerData.KEY_REQUESTS);
			int dIndex = cursor.getColumnIndexOrThrow(PrayerData.KEY_PRAYERS);

			requests.setText(cursor.getString(wIndex));
			prayers.setText(cursor.getString(dIndex));
		}
	}

	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		case android.R.id.home:
			Intent intent = new Intent(this, FindPrayer.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return false;
		}
	}
}
