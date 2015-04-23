package com.metzies.ntnoe;

import java.io.File;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.ContextThemeWrapper;

import com.metzies.ntnoe.app.R;

public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		setTitle(getResources().getString(R.string.action_others_activity));
		getActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.overflow));
		getActionBar().setIcon(
				getResources().getDrawable(R.drawable.settings_color));
		if (getSharedPreferences("logs", Context.MODE_PRIVATE).getBoolean(
				"prof", false)) {
			((Preference) findPreference("gotolangues")).setSelectable(false);
		}

		/*
		 * 
		 */
		((Preference) findPreference("gotolangues"))
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						startActivity(new Intent(SettingsActivity.this,
								LangClasses.class).putExtra("update", true));

						return false;
					}
				});

		/*
		 * 
		 */
		((Preference) findPreference("gotosevents"))
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						startActivity(new Intent(SettingsActivity.this,
								SingleEvent.class));

						return false;
					}
				});
		/*
		 * 
		 */
		((Preference) findPreference("shareEDT"))
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						SharedPreferences preflogs = getSharedPreferences(
								"logs", Context.MODE_PRIVATE);

						File dirext = new File(Environment
								.getExternalStorageDirectory() + "/NTNOE/");

						if (dirext.isDirectory()) {
							String[] children = dirext.list();
							for (int i = 0; i < children.length; i++) {
								if (children[i].contains(".ics")) {
									new File(dirext, children[i]).delete();
								}
							}
						}

						new ICSAsync(getApplicationContext(), preflogs
								.getString("baseFile", "shit"),
								new Vector<Event>(), 4).execute();

						startActivity(new Intent(getApplicationContext(),
								Attente.class).putExtra("shouldAskServer",
								false));

						return false;
					}
				});

		/*
		 * 
		 */
		((Preference) findPreference("gotoabout"))
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						startActivity(new Intent(SettingsActivity.this,
								About.class));

						return false;
					}
				});

		/*
		 * 
		 */
		((Preference) findPreference("disconnect"))
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								new ContextThemeWrapper(SettingsActivity.this,
										R.style.AlertDialogCustom));
						builder.setMessage(
								"Êtes-vous sûr? Toutes vos informations seront perdues.")
								.setCancelable(true)
								.setPositiveButton("Oui",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												/*
												 * On essaie de tout remettre à
												 * 0
												 */
												// SharedPreferences preflogs =
												// getSharedPreferences(
												// "logs",
												// Context.MODE_PRIVATE);

												SharedPreferences preflogs = getSharedPreferences(
														"logs",
														Context.MODE_PRIVATE);

												Editor editor = preflogs.edit();
												editor.clear();
												editor.commit();

												DataBaseHandler dbh = new DataBaseHandler(
														SettingsActivity.this);
												dbh.deleteAll();
												dbh.close();

												File dirext = new File(
														Environment
																.getExternalStorageDirectory()
																+ "/NTNOE/");

												if (dirext.isDirectory()) {
													String[] children = dirext
															.list();
													for (int i = 0; i < children.length; i++) {
														if (children[i]
																.contains(".ics")) {
															new File(dirext,
																	children[i])
																	.delete();
														}
													}
												}
												dirext.delete();

												File dir = new File(
														preflogs.getString(
																"baseDir",
																Environment
																		.getDataDirectory()
																		+ "/data/com.metzies.ntnoe.app/NTNOE/"));
												if (dir.isDirectory()) {
													String[] children = dir
															.list();
													for (int i = 0; i < children.length; i++) {
														if (children[i]
																.contains(".ics")) {
															new File(dir,
																	children[i])
																	.delete();
														}
													}
												}

												startActivity(new Intent(
														getApplicationContext(),
														Signup.class));
												// finish();
											}
										})
								.setNegativeButton("Annuler",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												dialog.cancel();
											}
										});
						builder.create().show();

						return false;
					}
				});

	}

	@Override
	public void onPause() {
		onStop();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		onStop();
	}

}
