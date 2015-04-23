package com.metzies.ntnoe;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "events.db";

	// Contacts table name
	private static final String TABLE_EVENTS = "events";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";

	private static final String KEY_START = "start";
	private static final String KEY_END = "end";
	private static final String KEY_TYPE = "type";

	private static final String KEY_SUMMARY = "summary";
	private static final String KEY_LOCATION = "location";

	private static final String KEY_WEEK = "week";
	private static final String KEY_DAY = "day";

	public DataBaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_START
				+ " TEXT," + KEY_END + " TEXT," + KEY_TYPE + " INTEGER,"
				+ KEY_SUMMARY + " TEXT," + KEY_LOCATION + " TEXT," + KEY_WEEK
				+ " INTEGER," + KEY_DAY + " INTEGER" + ")";
		db.execSQL(CREATE_EVENTS_TABLE);
	}

	/*
	 * 0 id
	 * 
	 * 1 sdate
	 * 
	 * 2 edate
	 * 
	 * 3 type
	 * 
	 * 4 summary
	 * 
	 * 5 location
	 * 
	 * 6 week
	 * 
	 * 7 day
	 */

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */

	// Adding new event
	void addEvent(Event ev) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();

		values.put(KEY_TYPE, ev.getType());
		values.put(KEY_START, ev.getSdate());
		values.put(KEY_END, ev.getEdate());
		values.put(KEY_SUMMARY, ev.getSummary());
		values.put(KEY_LOCATION, ev.getLocation());
		values.put(KEY_WEEK, ev.getWeek());
		values.put(KEY_DAY, ev.getDay());

		// Inserting Row
		db.insert(TABLE_EVENTS, null, values);
		db.close(); // Closing database connection
	}

	public static ContentValues getValues(Event ev) {
		ContentValues values = new ContentValues();

		values.put(KEY_TYPE, ev.getType());
		values.put(KEY_START, ev.getSdate());
		values.put(KEY_END, ev.getEdate());
		values.put(KEY_SUMMARY, ev.getSummary());
		values.put(KEY_LOCATION, ev.getLocation());
		values.put(KEY_WEEK, ev.getWeek());
		values.put(KEY_DAY, ev.getDay());

		return values;
	}

	void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_EVENTS, null, null);
		db.close();
		this.close();
	}

	void deleteClasses() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		for (int i = 9; i != 14; i++) {
			db.delete(TABLE_EVENTS, KEY_TYPE + "=?",
					new String[] { String.valueOf(i) });
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		this.close();
	}

	void clearDescriptions() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		for (int i = 0; i != 2; i++) {
			db.delete(TABLE_EVENTS, KEY_TYPE + "=?",
					new String[] { String.valueOf(i) });
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		this.close();
	}

	void deleteLangClasses() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		db.delete(TABLE_EVENTS, KEY_TYPE + "=?", new String[] { "5" });
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		this.close();
	}

	void deleteSportClasses() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();

		db.delete(TABLE_EVENTS, KEY_TYPE + "=?", new String[] { "14" });
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		this.close();
	}

	// Getting single Contact
	Event getEventByWeek(int theweek) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.query(TABLE_EVENTS, null, KEY_WEEK + "=?",
				new String[] { String.valueOf(theweek) }, null, null, null,
				null);

		if (c != null)
			c.moveToFirst();

		Event ev = new Event(c.getString(1), c.getString(2), c.getString(4),
				Integer.parseInt(c.getString(3)), c.getString(5),
				Integer.parseInt(c.getString(6)), Integer.parseInt(c
						.getString(7)));
		c.close();
		db.close();
		this.close();

		// return contact
		return ev;
	}

	public List<Event> getWeekEvents(int week) {
		List<Event> evlist = new ArrayList<Event>();

		SQLiteDatabase db = this.getWritableDatabase();

		/*
		 * we use day + 1 because monday is stored as 2 for some reason (and so
		 * on for the other days)
		 */

		Cursor c = db.query(TABLE_EVENTS, null, KEY_WEEK + "=?",
				new String[] { String.valueOf(week) }, null, null, null, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Event ev = new Event(c.getString(1), c.getString(2),
						c.getString(4), Integer.parseInt(c.getString(3)),
						c.getString(5), Integer.parseInt(c.getString(6)),
						Integer.parseInt(c.getString(7)));
				evlist.add(ev);
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		this.close();

		// return contact list
		return evlist;
	}

	public List<Event> getUserLangs() {
		List<Event> evlist = new ArrayList<Event>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.query(TABLE_EVENTS, null, KEY_TYPE + "=?",
				new String[] { "0" }, null, null, null, null);

		if (c.moveToFirst()) {
			do {
				Event ev = new Event(c.getString(1), c.getString(4),
						Integer.parseInt(c.getString(3)), c.getString(5),
						Integer.parseInt(c.getString(7)));
				evlist.add(ev);
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		this.close();

		return evlist;
	}

	public List<Event> getUserSports() {
		List<Event> evlist = new ArrayList<Event>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.query(TABLE_EVENTS, null, KEY_TYPE + "=?",
				new String[] { "1" }, null, null, null, null);

		if (c.moveToFirst()) {
			do {
				Event ev = new Event(c.getString(1), c.getString(4),
						Integer.parseInt(c.getString(3)), c.getString(5),
						Integer.parseInt(c.getString(7)));
				evlist.add(ev);
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		this.close();

		return evlist;
	}

	public List<Event> getSevents() {
		List<Event> evlist = new ArrayList<Event>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor c = db.query(TABLE_EVENTS, null, KEY_TYPE + "=?",
				new String[] { "8" }, null, null, null, null);

		if (c.moveToFirst()) {
			do {

				Event ev = new Event(c.getString(1), c.getString(2),
						c.getString(4), Integer.parseInt(c.getString(3)),
						c.getString(5), Integer.parseInt(c.getString(6)),
						Integer.parseInt(c.getString(7)));
				evlist.add(ev);
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		this.close();

		return evlist;
	}

	public List<Event> getDayEvents(int week, int day) {
		List<Event> evlist = new ArrayList<Event>();
		// Select All Query

		SQLiteDatabase db = this.getWritableDatabase();

		/*
		 * we use day + 1 because monday is stored as 2 for some reason (and so
		 * on for the other days)
		 */

		Cursor c = db.query(TABLE_EVENTS, null, KEY_WEEK + "=?" + " and "
				+ KEY_DAY + "=?",
				new String[] { String.valueOf(week), String.valueOf(day + 1) },
				null, null, null, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Event ev = new Event(c.getString(1), c.getString(2),
						c.getString(4), Integer.parseInt(c.getString(3)),
						c.getString(5), Integer.parseInt(c.getString(6)),
						Integer.parseInt(c.getString(7)));
				evlist.add(ev);
			} while (c.moveToNext());
		}
		c.close();
		db.close();
		this.close();

		// return contact list
		return evlist;
	}

	// Getting All Events
	public List<Event> getAllEvents() {
		List<Event> evlist = new ArrayList<Event>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_EVENTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				if (Integer.parseInt(c.getString(6)) != 0) {
					Event ev = new Event(c.getString(1), c.getString(2),
							c.getString(4), Integer.parseInt(c.getString(3)),
							c.getString(5), Integer.parseInt(c.getString(6)),
							Integer.parseInt(c.getString(7)));
					evlist.add(ev);
				}

			} while (c.moveToNext());
		}
		c.close();
		db.close();
		this.close();

		// return contact list
		return evlist;
	}

	// Deleting single contact
	public void deleteEvent(String summary) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EVENTS, KEY_SUMMARY + " = ?", new String[] { summary });
		db.close();
	}

}