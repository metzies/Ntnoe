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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

public class About extends Activity {
	// counter for the hidden popup
	private int clicks;
	private SharedPreferences preflogs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		getWindow().setBackgroundDrawable(null);
		getIntent();
		getActionBar().setTitle("A propos");
		TextView tx = (TextView) findViewById(R.id.app_version);
		Typeface custom_font = Typeface.createFromAsset(getAssets(),
				"minecrafter.ttf");
		tx.setTypeface(custom_font);
		clicks = 0;

		preflogs = getSharedPreferences("logs", Context.MODE_PRIVATE);

		custom_font = null;
		tx = null;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

	public void onBackPressed() {
		//on finish l'activité pour retourner à l'activité précédente
        finish();
	}

	public void suggestionSend(View v) {
		//cette méthode envoie l'intent pour envoyer un mail de suggestion au développeur quand on a ouvert le hidden popup
        try {
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent
					.setType("vnd.android.cursor.dir/email")
					.putExtra(Intent.EXTRA_EMAIL,
							new String[] { "kayssar.daher@supelec.fr" })
					.putExtra(
							Intent.EXTRA_SUBJECT,
							getResources()
									.getString(R.string.suggestions_title));
			startActivity(Intent.createChooser(sharingIntent, getResources()
					.getString(R.string.share_with)));
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(
					getApplicationContext(),
					getResources()
							.getString(R.string.could_not_send_suggestion),
					Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("InflateParams")
	public void showhiddenmessage(View v) {
		clicks++;

		if (!preflogs.getBoolean("prof", false) && clicks == 7) {

			AlertDialog.Builder builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));

			builder.setView(getLayoutInflater().inflate(R.layout.hidden, null))
					.setCancelable(false)
					.setNeutralButton("OK", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
				
			builder.create().show();
			clicks = 0;
		}
	}
}
