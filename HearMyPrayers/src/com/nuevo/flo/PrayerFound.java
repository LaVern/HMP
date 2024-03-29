package com.nuevo.flo;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Provides access to the Prayer Data.
 */
public class PrayerFound extends ContentProvider {
	String TAG = "PrayerFound";

	public static String AUTHORITY = "com.nuevo.flo.PrayerFound";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/pray");

	public static final String REQUESTS_MIME_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.nuevo.flo";
	public static final String PRAYERS_MIME_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.nuevo.flo";

	private PrayerData mPrayer;

	// UriMatcher stuff
	private static final int SEARCH_REQUESTS = 0;
	private static final int GET_REQUESTS = 1;
	private static final int SEARCH_SUGGEST = 2;
	private static final int REFRESH_SHORTCUT = 3;
	private static final UriMatcher sURIMatcher = buildUriMatcher();

	/**
	 * Builds up a UriMatcher for search suggestion and shortcut refresh queries.
	 */
	private static UriMatcher buildUriMatcher() {
		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		// to get definitions...
		matcher.addURI(AUTHORITY, "prayer", SEARCH_REQUESTS);
		matcher.addURI(AUTHORITY, "prayer/#", GET_REQUESTS);
		// to get suggestions...
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);

		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT, REFRESH_SHORTCUT);
		matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_SHORTCUT + "/*", REFRESH_SHORTCUT);
		return matcher;
	}

	@Override
	public boolean onCreate() {
		mPrayer = new PrayerData(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		// Use the UriMatcher to see what kind of query we have and format the db query accordingly
		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return getSuggestions(selectionArgs[0]);
		case SEARCH_REQUESTS:
			if (selectionArgs == null) {
				throw new IllegalArgumentException("selectionArgs must be provided for the Uri: " + uri);
			}
			return search(selectionArgs[0]);
		case GET_REQUESTS:
			return getRequests(uri);
		case REFRESH_SHORTCUT:
			return refreshShortcut(uri);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { BaseColumns._ID, PrayerData.KEY_REQUESTS, PrayerData.KEY_PRAYERS,
		/* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
		                 (only if you want to refresh shortcuts) */
		SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return mPrayer.getRequestsMatches(query, columns);
	}

	private Cursor search(String query) {
		query = query.toLowerCase();
		String[] columns = new String[] { BaseColumns._ID, PrayerData.KEY_REQUESTS, PrayerData.KEY_PRAYERS };

		return mPrayer.getRequestsMatches(query, columns);
	}

	private Cursor getRequests(Uri uri) {
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { PrayerData.KEY_REQUESTS, PrayerData.KEY_PRAYERS };

		return mPrayer.getRequests(rowId, columns);
	}

	private Cursor refreshShortcut(Uri uri) {
		/* This won't be called with the current implementation, but if we include
		 * {@link SearchManager#SUGGEST_COLUMN_SHORTCUT_ID} as a column in our suggestions table, we
		 * could expect to receive refresh queries when a shortcutted suggestion is displayed in
		 * Quick Search Box. In which case, this method will query the table for the specific
		 * word, using the given item Uri and provide all the columns originally provided with the
		 * suggestion query.
		 */
		String rowId = uri.getLastPathSegment();
		String[] columns = new String[] { BaseColumns._ID, PrayerData.KEY_REQUESTS, PrayerData.KEY_PRAYERS, SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID };

		return mPrayer.getRequests(rowId, columns);
	}

	/**
	 * This method is required in order to query the supported types.
	 * It's also useful in our own query() method to determine the type of Uri received.
	 */
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
		case SEARCH_REQUESTS:
			return REQUESTS_MIME_TYPE;
		case GET_REQUESTS:
			return PRAYERS_MIME_TYPE;
		case SEARCH_SUGGEST:
			return SearchManager.SUGGEST_MIME_TYPE;
		case REFRESH_SHORTCUT:
			return SearchManager.SHORTCUT_MIME_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	// Other required implementations...

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

}
