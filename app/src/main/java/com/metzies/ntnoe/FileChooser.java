/*
 * Ce fichier fait partie du projet NTNOE sur Android.
 * 
 * Les développeurs mettent ce projet à disposition de tous sous les conditions suivantes:
 * 
 * Vous avez le droit d'examiner et tester le code.
 * 
 * Vous avez le droit de copier et modifier le code à condition de citer clairement et publiquement vos sources.
 * 
 * Vous n'avez pas le droit de désigner ce code (modifié ou non) comme entièrement le votre.
 * 
 * Vous n'avez pas le droit de commercialiser ce code sans le consentement écrit des développeurs originaux.
 * 
 * Les développeurs gardent le droit de regard sur toute oeuvre dérivée de ce code là.
 * 
 * Les développeurs originaux fournissent ce code tel quel, sans garantie.
 * 
 * Pour plus d'information sur les termes de la licence, contacter pierre-yves.bigourdan@supelec.fr et kayssar.daher@supelec.fr
 * 
 * Le non-respect de ces termes vous expose à des poursuites judiciaires.
 * 
 */
package com.metzies.ntnoe;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

public class FileChooser extends ListActivity {
	/*
	 * module trouvé en ligne
	 * http://www.dreamincode.net/forums/topic/190013-creating
	 * -simple-file-chooser/
	 */

	private File currentDir;
	private FileArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		setTitle("Sélectionner le fichier .ics");
		getActionBar().setIcon(R.drawable.ic_action_go_to_today);
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(getResources().getColor(R.color.light_blue)));
		currentDir = Environment.getExternalStorageDirectory();
		fill(currentDir);
	}

	private void fill(File f) {

		File[] dirs = f.listFiles();
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();

		try {
			for (File ff : dirs) {
				if (ff.isDirectory())
					dir.add(new Option(ff.getName(), "Dossier", ff
							.getAbsolutePath()));
				else {
					fls.add(new Option(ff.getName(), "Taille" + ": "
							+ ff.length() / 1024 + "ko", ff.getAbsolutePath()));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard")) {
			dir.add(0, new Option("..", "Répertoire parent", f.getParent()));
		}
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
				dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase("Dossier")
				|| o.getData().equalsIgnoreCase("Répertoire parent")) {
			if (o.getPath().contains(
					Environment.getExternalStorageDirectory().toString())) {
				currentDir = new File(o.getPath());
				fill(currentDir);
			} else {
				Toast.makeText(this, "Vous ne pouvez pas aller plus loin",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			onFileClick(o);
		}
	}

	private void onFileClick(Option o) {
		if (o.getPath().contains(".ics")) {
			SharedPreferences preflogs = getSharedPreferences("logs",
					Context.MODE_PRIVATE);
			Editor editor = preflogs.edit();
			editor.putString("baseFile", o.getPath());
			editor.commit();

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			if (!preflogs.getBoolean("prof", false)) {
				builder.setMessage(
						"Voulez-vous introduire des créneaux personnels?")
						.setCancelable(true)
						.setPositiveButton("Oui",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										startActivity(new Intent(
												getApplicationContext(),
												LangClasses.class).putExtra(
												"update", false));
										FileChooser.this.finish();
									}
								})
						.setNegativeButton("Non",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										SharedPreferences preflogs = getSharedPreferences(
												"logs", Context.MODE_PRIVATE);

										Vector<Event> vec = new Vector<Event>();

										new ICSAsync(getApplicationContext(),
												preflogs.getString("baseFile",
														"shit"), vec, 1)
												.execute();

										Editor editor = preflogs.edit();
										editor.putBoolean("initialized", true);
										editor.commit();
										startActivity(new Intent(
												getApplicationContext(),
												Attente.class).putExtra(
												"shouldAskServer", false));
										FileChooser.this.finish();
									}
								});
				builder.create().show();
			} else {
				Vector<Event> vec = new Vector<Event>();

				new ICSAsync(getApplicationContext(), preflogs.getString(
						"baseFile", "shit"), vec, 2).execute();

				editor.putBoolean("initialized", true);
				editor.commit();
				startActivity(new Intent(getApplicationContext(), Attente.class)
						.putExtra("shouldAskServer", false));
				FileChooser.this.finish();
			}

		} else {
			Toast.makeText(this, "Le fichier choisi n'est pas un fichier .ics",
					Toast.LENGTH_SHORT).show();

		}
	}

	public void onBackPressed() {
		if (currentDir.getParent().contains(
				Environment.getExternalStorageDirectory().toString())) {
			currentDir = currentDir.getParentFile();
			fill(currentDir);
		} else {
			finish();
			startActivity(new Intent(getApplicationContext(), Signup.class));
		}
	}

}
