package com.metzies.ntnoe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.metzies.ntnoe.app.R;

public class LangClasses extends Activity implements
		AdapterView.OnItemSelectedListener {

	private static TableLayout langtable;
	private static TableLayout sporttable;

	private static List<Event> evVec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_lang_classes);

		langtable = (TableLayout) findViewById(R.id.lang_classes_tl);
		sporttable = (TableLayout) findViewById(R.id.sport_classes_tl);

		evVec = new ArrayList<Event>();

		loadClasses();

		setTitle(getResources().getString(R.string.funcs_crenos));
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.light_blue)));
		getActionBar().setIcon(getResources().getDrawable(R.drawable.creneaux));

	}

	public void addLangRow(View v) {

		TableRow tr = (TableRow) getLayoutInflater().inflate(
				R.layout.langs_row, langtable, false);

		for (int i = 0; i != tr.getChildCount(); i++) {
			((Spinner) tr.getChildAt(i)).setOnItemSelectedListener(this);
		}

		langtable.addView(tr);

	}

	public void addSportRow(View v) {

		TableRow tr = (TableRow) getLayoutInflater().inflate(
				R.layout.sports_row, langtable, false);

		for (int i = 0; i != tr.getChildCount(); i++) {
			((Spinner) tr.getChildAt(i)).setOnItemSelectedListener(this);
		}

		sporttable.addView(tr);

	}

	public void loadClasses() {
		DataBaseHandler dbh = new DataBaseHandler(this);
		List<Event> langs = dbh.getUserLangs();
		List<Event> sports = dbh.getUserSports();
		dbh.close();

		Log.e("Devik langs size", langs.size() + "");
		Log.e("Devik sports size", sports.size() + "");

		if (!langs.isEmpty())
			for (int i = 0; i != langs.size(); i++) {
				addLangRow(null);
				Event current = langs.get(i);
				String summary = current.getSummary();
				int sequence = Integer.parseInt(current.getLocation());
				String sdate = current.getSdate();

				Spinner sp_langs = (Spinner) ((TableRow) langtable
						.getChildAt(i)).getChildAt(0);

				if (summary.contains("Anglais")) {
					sp_langs.setSelection(1);
				} else if (summary.contains("Espagnol")) {
					sp_langs.setSelection(2);
				} else if (summary.contains("Allemand")) {
					sp_langs.setSelection(3);
				} else if (summary.contains("Chinois")) {
					sp_langs.setSelection(4);
				} else if (summary.contains("Japonais")) {
					sp_langs.setSelection(5);
				} else if (summary.contains("Arabe")) {
					sp_langs.setSelection(6);
				} else if (summary.contains("Russe")) {
					sp_langs.setSelection(7);
				} else if (summary.contains("Autre langue")) {
					sp_langs.setSelection(8);
				}

				Spinner sp_creno = (Spinner) ((TableRow) langtable
						.getChildAt(i)).getChildAt(1);

				if (sdate.contains("Lu 13h30")) {
					sp_creno.setSelection(1);
				} else if (sdate.contains("Lu 15h15")) {
					sp_creno.setSelection(2);
				} else if (sdate.contains("Lu 16h45")) {
					sp_creno.setSelection(3);
				} else if (sdate.contains("Lu 18h30")) {
					sp_creno.setSelection(4);
				} else if (sdate.contains("Ma 13h30")) {
					sp_creno.setSelection(5);
				} else if (sdate.contains("Ma 15h15")) {
					sp_creno.setSelection(6);
				} else if (sdate.contains("Ma 16h45")) {
					sp_creno.setSelection(7);
				} else if (sdate.contains("Ma 18h30")) {
					sp_creno.setSelection(8);
				} else if (sdate.contains("Me 8h30")) {
					sp_creno.setSelection(9);
				} else if (sdate.contains("Me 10h30")) {
					sp_creno.setSelection(10);
				} else if (sdate.contains("Je 13h30")) {
					sp_creno.setSelection(11);
				} else if (sdate.contains("Je 15h15")) {
					sp_creno.setSelection(12);
				} else if (sdate.contains("Je 16h45")) {
					sp_creno.setSelection(13);
				} else if (sdate.contains("Je 18h30")) {
					sp_creno.setSelection(14);
				}

				Spinner sp_seq = (Spinner) ((TableRow) langtable.getChildAt(i))
						.getChildAt(2);

				switch (sequence) {
				case 1:
					sp_seq.setSelection(1);
					break;
				case 2:
					sp_seq.setSelection(2);
					break;
				case 3:
					sp_seq.setSelection(3);
					break;
				case 4:
					sp_seq.setSelection(4);
					break;
				case 5:
					sp_seq.setSelection(5);
					break;
				case 6:
					sp_seq.setSelection(6);
					break;
				case 7:
					sp_seq.setSelection(7);
					break;
				case 8:
					sp_seq.setSelection(8);
					break;
				}
			}
		else {
			addLangRow(null);
		}

		if (!sports.isEmpty()) {

			for (int w = 0; w != sports.size(); w++) {
				addSportRow(null);
				Event current = sports.get(w);
				String summary = current.getSummary();
				int sequence = Integer.parseInt(current.getLocation());
				String sdate = current.getSdate();

				Spinner sp_sports = (Spinner) ((TableRow) sporttable
						.getChildAt(w)).getChildAt(0);

				if (summary.contains("Football")) {
					sp_sports.setSelection(1);
				} else if (summary.contains("Basketball")) {
					sp_sports.setSelection(2);
				} else if (summary.contains("Handball")) {
					sp_sports.setSelection(3);
				} else if (summary.contains("Rugby")) {
					sp_sports.setSelection(4);
				} else if (summary.contains("Badminton")) {
					sp_sports.setSelection(5);
				} else if (summary.contains("Karate")) {
					sp_sports.setSelection(6);
				} else if (summary.contains("Aviron")) {
					sp_sports.setSelection(7);
				} else if (summary.contains("Escalade")) {
					sp_sports.setSelection(8);
				} else if (summary.contains("Autre sport")) {
					sp_sports.setSelection(9);
				}

				Spinner sp_crenos = (Spinner) ((TableRow) sporttable
						.getChildAt(w)).getChildAt(1);

				if (sdate.contains("Lu 18h00")) {
					sp_crenos.setSelection(1);
				} else if (sdate.contains("Lu 19h00")) {
					sp_crenos.setSelection(2);
				} else if (sdate.contains("Lu 20h00")) {
					sp_crenos.setSelection(3);
				} else if (sdate.contains("Lu 21h00")) {
					sp_crenos.setSelection(4);
				} else if (sdate.contains("Ma 18h00")) {
					sp_crenos.setSelection(5);
				} else if (sdate.contains("Ma 19h00")) {
					sp_crenos.setSelection(6);
				} else if (sdate.contains("Ma 20h00")) {
					sp_crenos.setSelection(7);
				} else if (sdate.contains("Ma 21h00")) {
					sp_crenos.setSelection(8);
				} else if (sdate.contains("Me 18h00")) {
					sp_crenos.setSelection(9);
				} else if (sdate.contains("Me 19h00")) {
					sp_crenos.setSelection(10);
				} else if (sdate.contains("Me 20h00")) {
					sp_crenos.setSelection(11);
				} else if (sdate.contains("Me 21h00")) {
					sp_crenos.setSelection(12);
				} else if (sdate.contains("Je 18h00")) {
					sp_crenos.setSelection(13);
				} else if (sdate.contains("Je 19h00")) {
					sp_crenos.setSelection(14);
				} else if (sdate.contains("Je 20h00")) {
					sp_crenos.setSelection(15);
				} else if (sdate.contains("Je 21h00")) {
					sp_crenos.setSelection(16);
				}

				Spinner sp_seq = (Spinner) ((TableRow) sporttable.getChildAt(w))
						.getChildAt(2);

				switch (sequence) {
				case 1:
					sp_seq.setSelection(1);
					break;
				case 2:
					sp_seq.setSelection(2);
					break;
				case 3:
					sp_seq.setSelection(3);
					break;
				case 4:
					sp_seq.setSelection(4);
					break;
				case 5:
					sp_seq.setSelection(5);
					break;
				case 6:
					sp_seq.setSelection(6);
					break;
				case 7:
					sp_seq.setSelection(7);
					break;
				case 8:
					sp_seq.setSelection(8);
					break;
				}
			}
		} else {
			addSportRow(null);
		}

	}

	// TODO creates stackoverflow error on phones with low ram and old android

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lang_classes, menu);

		if (!getIntent().getBooleanExtra("update", true)) {
			MenuItem item = menu
					.findItem(R.id.action_lang_classes_delete_crenos);
			item.setVisible(false);
			this.invalidateOptionsMenu();
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {

		case R.id.action_lang_classes_setChanges:
			setChanges();
			return true;

		case R.id.action_lang_classes_delete_crenos:

			AlertDialog.Builder builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));

			builder.setMessage(
					"Voulez-vous supprimer tous les créneaux personnels ?")
					.setCancelable(true)
					.setPositiveButton("Oui",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									new ICSAsync(LangClasses.this, null, evVec,
											5).execute();

									startActivity(new Intent(LangClasses.this,
											Attente.class).putExtra(
											"shouldAskServer", false));
									LangClasses.this.finish();
									LangClasses.this.onDestroy();

								}
							})
					.setNegativeButton("Non",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			builder.create().show();
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	/*
	 * important stuff going on here
	 */

	public void setChanges() {

		DataBaseHandler dbh = new DataBaseHandler(this);
		dbh.clearDescriptions();
		dbh.close();

		for (int i = 0; i != langtable.getChildCount(); i++) {
			readRow((TableRow) langtable.getChildAt(i));
		}

		for (int i = 0; i != sporttable.getChildCount(); i++) {
			readRow((TableRow) sporttable.getChildAt(i));
		}

		SharedPreferences preflogs = getSharedPreferences("logs",
				Context.MODE_PRIVATE);

		if (!getIntent().getBooleanExtra("update", false)) {

			Log.e("Devik", "Initializing not updating");

			new ICSAsync(this, preflogs.getString("baseFile",
					Environment.getDataDirectory()
							+ "/data/com.metzies.ntnoe.app/NTNOE/" + "EDT_"
							+ preflogs.getString("login", "shit") + ".ics"),
					evVec, 0).execute();

		} else {

			Log.e("Devik", "Updating not initializing");

			Log.e("evVec size is", evVec.size() + "");

			new ICSAsync(LangClasses.this, preflogs.getString("baseFile",
					Environment.getDataDirectory()
							+ "/data/com.metzies.ntnoe.app/NTNOE/" + "EDT_"
							+ preflogs.getString("login", "shit") + ".ics"),
					evVec, 3).execute();

		}

		Editor editor = preflogs.edit();
		editor.putBoolean("initialized", true);
		editor.commit();

		startActivity(new Intent(this, Attente.class).putExtra(
				"shouldAskServer", false));
		this.finish();
		this.onDestroy();

	}

	public static void readRow(TableRow tr) {

		if ((!((Spinner) tr.getChildAt(0)).getSelectedItem().toString()
				.contains("Langue") || !((Spinner) tr.getChildAt(0))
				.getSelectedItem().toString().contains("Sport"))
				&& !((Spinner) tr.getChildAt(1)).getSelectedItem().toString()
						.contains("Créneau")
				&& !((Spinner) tr.getChildAt(2)).getSelectedItem().toString()
						.contains("Séquence")) {

			parseNgenerate(((Spinner) tr.getChildAt(1)).getSelectedItem()
					.toString(), ((Spinner) tr.getChildAt(0)).getSelectedItem()
					.toString(), Integer.parseInt(((Spinner) tr.getChildAt(2))
					.getSelectedItem().toString()));

		}

	}

	public static void parseNgenerate(final String theclass,
			final String summary, final int original_sequence) {

		String crenodesc = "";

		if (theclass.contains("Lu")) {
			crenodesc = "Lu ";
		} else if (theclass.contains("Ma")) {
			crenodesc = "Ma ";
		} else if (theclass.contains("Me")) {
			crenodesc = "Me ";
		} else if (theclass.contains("Je")) {
			crenodesc = "Je ";
		} else if (theclass.contains("Ve")) {
			crenodesc = "Ve ";
		}

		if (theclass.contains((CharSequence) "13h30–15h00")) {
			crenodesc += "13h30";
		} else if (theclass.contains((CharSequence) "15h15–16h45")) {
			crenodesc += "15h15";
		} else if (theclass.contains((CharSequence) "16h45–18h15")) {
			crenodesc += "16h45";
		} else if (theclass.contains((CharSequence) "18h30–20h00")) {
			crenodesc += "18h30";
		} else if (theclass.contains((CharSequence) "18h00–19h00")) {
			crenodesc += "18h00";
		} else if (theclass.contains((CharSequence) "8h30–10h00")) {
			crenodesc += "8h30";
		} else if (theclass.contains((CharSequence) "10h30–12h00")) {
			crenodesc += "10h30";
		} else if (theclass.contains((CharSequence) "19h00–20h00")) {
			crenodesc += "19h00";
		} else if (theclass.contains((CharSequence) "20h00–21h00")) {
			crenodesc += "20h00";
		} else if (theclass.contains((CharSequence) "21h00–22h00")) {
			crenodesc += "21h00";
		}

		Event desc_ev;
		if (crenodesc.contains("00")) {
			desc_ev = new Event(crenodesc, summary, 1, original_sequence + "",
					0);
		} else {
			desc_ev = new Event(crenodesc, summary, 0, original_sequence + "",
					0);
		}

		evVec.add(desc_ev);
	}

	/*
	 * end of the class
	 */
}
