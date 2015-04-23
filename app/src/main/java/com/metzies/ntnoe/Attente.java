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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.metzies.ntnoe.app.R;

public class Attente extends Activity {
	/*
	 * La classe attente affiche une sorte de progress bar quand l'appli est en
	 * train de faire des calculs en asynctask. Elle permet de lancer soit la
	 * récupération de fichier en asynctask grâce à RecupICS ou bien le
	 * traitement des fichiers grâce à ICSAsync. Pour distinguer entre ces deux
	 * lancements, on utilise un extra dans l'intent qui lance Attente.java Cet
	 * extra est un booléen qu'on appelle shouldAskServer. Si shouldAskServer ==
	 * true, on lance RecupICS, sinon on lance autre chose
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attente);
		getWindow().setBackgroundDrawable(null);
		Intent i = getIntent();
		getActionBar().hide();

        //on charge les sharedprefs comme ça on peut les entrer dans le RecupICS
		if (i.getExtras().getBoolean("shouldAskServer")) {
            SharedPreferences preflogs = getSharedPreferences("logs",
                    Context.MODE_PRIVATE);
            String login = preflogs.getString("login", "");
            String password = preflogs.getString("password", "");
            new RecupICS(login, password, Attente.this, false).execute();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 * 
	 * Pour gérer correctement la mémoire. a priori on en aura très rarement
	 * besoin
	 */
	@Override
	public void onPause() {
		finish();
		onStop();
		onDestroy();
	}

}
