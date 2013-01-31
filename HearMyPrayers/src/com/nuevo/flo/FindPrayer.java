package com.nuevo.flo;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class FindPrayer extends Activity {

	private TextView mTextView;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTextView = (TextView) findViewById(R.id.requests);
		mListView = (ListView) findViewById(R.id.prayers);

		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {

		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())) {

			Intent requestsIntent = new Intent(this, PrayerActivity.class);
			requestsIntent.setData(intent.getData());
			startActivity(requestsIntent);
			finish();
		} else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			// handles a search query
			String query = intent.getStringExtra(SearchManager.QUERY);
			showResults(query);
		}
	}

	private void showResults(String query) {

		Cursor cursor = managedQuery(PrayerFound.CONTENT_URI, null, null, new String[] { query }, null);

		if (cursor == null) {

			mTextView.setText(getString(R.string.no_results, new Object[] { query }));
		} else {

			int count = cursor.getCount();
			String countString = getResources().getQuantityString(R.plurals.search_results, count, new Object[] { count, query });
			mTextView.setText(countString);

			String[] from = new String[] { PrayerData.KEY_REQUESTS, PrayerData.KEY_PRAYERS };

			int[] to = new int[] { R.id.requests, R.id.prayers };

			//  cursor adapter for the prayers 
			SimpleCursorAdapter requests = new SimpleCursorAdapter(this, R.layout.need, cursor, from, to);
			mListView.setAdapter(requests);

			mListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

					Intent requestsIntent = new Intent(getApplicationContext(), PrayerActivity.class);
					Uri data = Uri.withAppendedPath(PrayerFound.CONTENT_URI, String.valueOf(id));
					requestsIntent.setData(data);
					startActivity(requestsIntent);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.search:
			onSearchRequested();
			return true;
		default:
			return false;
		}
	}
}
