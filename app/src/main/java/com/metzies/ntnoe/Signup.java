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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

public class Signup extends Activity {

	private boolean prof = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		getWindow().setBackgroundDrawable(null);

		getActionBar().hide();
		SharedPreferences preflogs = getSharedPreferences("logs",
				Context.MODE_PRIVATE);

		/*
		 * on veut créer un répertoire pour les fichiers de l'appli, on examine
		 * quel répertoire est fonctionnel. on préfère le stockage en mémoire
		 * interne
		 */

		File dirext = new File(Environment.getExternalStorageDirectory()
				+ "/NTNOE/");

		if (dirext.isDirectory()) {
			String[] children = dirext.list();
			for (int i = 0; i < children.length; i++) {
				if (children[i].contains(".ics")) {
					new File(dirext, children[i]).delete();
				}
			}
		}
		dirext.delete();

		
		if ((new File(Environment.getDataDirectory()
				+ "/data/com.metzies.ntnoe.app/NTNOE/")).exists()) {

			preflogs.edit()
					.putString(
							"baseDir",
							Environment.getDataDirectory()
									+ "/data/com.metzies.ntnoe.app/NTNOE/")
					.commit();

			(new File(Environment.getExternalStorageDirectory() + "/NTNOE/"))
					.mkdirs();

			Log.e("Case 1", "existing internal memory");

		} else if ((new File(Environment.getDataDirectory()
				+ "/data/com.metzies.ntnoe.app/NTNOE/")).mkdirs()) {

			preflogs.edit()
					.putString(
							"baseDir",
							Environment.getDataDirectory()
									+ "/data/com.metzies.ntnoe.app/NTNOE/")
					.commit();

			(new File(Environment.getExternalStorageDirectory() + "/NTNOE/"))
					.mkdirs();

			Log.e("Case 2", "internal memory");

		} else if ((new File(Environment.getExternalStorageDirectory()
				+ "/NTNOE/")).mkdirs()) {

			preflogs.edit()
					.putString(
							"baseDir",
							Environment.getExternalStorageDirectory()
									+ "/NTNOE/").commit();

			Log.e("Case 3", "external memory");

		}

	}

	public void sendMessage(View view) {

		String login = ((EditText) findViewById(R.id.signup_login_nompre))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.signup_password))
				.getText().toString();
		SharedPreferences preflogs = getSharedPreferences("logs",
				Context.MODE_PRIVATE);
		Editor editor = preflogs.edit();
		editor.putBoolean("initialized", false);

		// editor.commit();
		// startActivity(new Intent(this,Nickname.class)); // this.finish();
		if (!login.contains(" ")) {
			editor.putString("login", login);
			if (!password.isEmpty()) {
				editor.putString("password", password);
				editor.commit();

				startActivity(new Intent(this, Attente.class).putExtra(
						"shouldAskServer", true));
				this.finish();
			} else {
				editor.commit();
				Toast.makeText(getApplicationContext(),
						"Peut-être rentrer un mot de passe, non?",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			editor.commit();
			Toast.makeText(
					getApplicationContext(),
					"Je sais pas ce que vous faites, mais ça c'est pas un login Supélec.",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void checkBoxed(View v) {
		prof = !prof;
		SharedPreferences preflogs = getSharedPreferences("logs",
				Context.MODE_PRIVATE);
		Editor editor = preflogs.edit();
		editor.putBoolean("prof", prof);
		editor.commit();
	}

	@Override
	public void onBackPressed() {
		this.finish();
		this.onStop();
		startActivity((new Intent(Intent.ACTION_MAIN)).addCategory(
				Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

}
