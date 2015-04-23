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
import java.io.FileOutputStream;
import java.util.List;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/*
 *La classe qui lance le traitement en tâche de fond.
 *on se sert de Manipulator
 *
 */
public class ICSAsync extends AsyncTask<Void, Integer, Void> {

	private Context context;
	private String filePath;
	private List<Event> evVec;
	private SharedPreferences preflogs;
	private int worktype;

	/*
	 * worktype définit le travail à faire par la classe
	 * 
	 * 0 = le travail de base, delete all and put everything
	 * 
	 * 1 = delete all and put classes
	 * 
	 * 2 = update classes only
	 * 
	 * 3 = update langs only
	 * 
	 * 4 = export all to ics
	 * 
	 * 5 = delete langs
	 */

	public ICSAsync(Context appContext, String path, List<Event> evvec,
			int work_type) {
		context = appContext;
		filePath = path;
		evVec = evvec;
		preflogs = context.getSharedPreferences("logs", Context.MODE_PRIVATE);
		worktype = work_type;
	}

	@Override
	protected void onProgressUpdate(final Integer... values) {

		Toast.makeText(context, "Traitement en cours, patientez...",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			Looper.prepare();
		} catch (Exception ex) {
		}

		Manipulator man = new Manipulator(context);
		publishProgress();

		DataBaseHandler dbh = new DataBaseHandler(context);
		Editor editor = preflogs.edit();
		switch (worktype) {

		// initializiation with langs
		case 0:
			Log.e("Devik", "initialize with langs");

			dbh.deleteAll();
			if (!preflogs.getBoolean("prof", false)) {
				man.dbTransfer(filePath);
			} else {
				man.dbTransferProf(filePath);
			}
			dbh.deleteLangClasses();
			dbh.close();
			man.dbLangAdd(evVec);
			dbh.close();

			editor.putBoolean("initialized", true);
			editor.commit();
			return null;

			// initialization without langs
		case 1:
			Log.e("Devik", "initialize without langs");

			dbh.deleteAll();

			if (!preflogs.getBoolean("prof", false)) {

				man.dbTransfer(filePath);
			} else {
				man.dbTransferProf(filePath);
			}
			dbh.close();

			editor.putBoolean("initialized", true);
			editor.commit();
			return null;

			// update non langs
		case 2:

			Log.e("Devik", "update non langs");

			dbh.deleteClasses();
			if (!preflogs.getBoolean("prof", false)) {
				Log.e("Devik", "it's not a prof, manDBing");
				man.dbTransfer(filePath);
			} else {
				man.dbTransferProf(filePath);
			}
			dbh.close();
			return null;

			// update langs
		case 3:

			Log.e("Devik", "update langs");
			dbh.deleteLangClasses();
			dbh.deleteSportClasses();
			man.dbLangAdd(evVec);
			dbh.close();
			return null;

			// export all to ics
		case 4:
			Log.e("Devik", "exporting all the DB");

			List<Event> evlist = dbh.getAllEvents();
			dbh.close();

			ComponentList cl = new ComponentList();

			for (Event ev : evlist) {

				VEvent vev = new VEvent(new DateTime(ev.getSgreg().getTime()),
						new DateTime(ev.getEgreg().getTime()), ev.getSummary());

				vev.getProperties().add(new Description(ev.getType() + ""));
				vev.getProperties()
						.getProperty(Property.DESCRIPTION)
						.getParameters()
						.add(net.fortuna.ical4j.model.parameter.Encoding.QUOTED_PRINTABLE);
				if (ev.getLocation() != null) {
					vev.getProperties().add(new Location(ev.getLocation()));
					vev.getProperties()
							.getProperty(Property.LOCATION)
							.getParameters()
							.add(net.fortuna.ical4j.model.parameter.Encoding.QUOTED_PRINTABLE);
				}
				cl.add(vev);
			}

			net.fortuna.ical4j.model.Calendar calendar = new Calendar(cl);
			calendar.getProperties().add(
					new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
			calendar.getProperties().add(CalScale.GREGORIAN);
			calendar.getProperties().add(Version.VERSION_2_0);

			try {

				CalendarOutputter outputter = new CalendarOutputter();
				outputter.setValidating(false);

				FileOutputStream fout = new FileOutputStream(new File(
						Environment.getExternalStorageDirectory() + "/NTNOE/",
						"EDT_FULL" + ".ics"));

				outputter.output(calendar, fout);
				fout.close();
				Log.e("Devik", "finished exporting");

			} catch (Exception e) {
				System.out.println("EXPORT FAILED");
				e.printStackTrace();
			}
			return null;

			// delete langs
		case 5:
			Log.e("Devik", "deleting langs");

			dbh.deleteLangClasses();
			dbh.deleteSportClasses();
			dbh.clearDescriptions();
			dbh.close();
			return null;
			/*
			 * end of switch case block
			 */
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)une fois le
	 * traitement terminé, on lance EDT.java en nouvelle activité (pendant ce
	 * temps là, c'était Attente.java devant l'utilisateur)
	 */
	@Override
	protected void onPostExecute(Void param) {

		switch (worktype) {

		case 4:

			try {
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("vnd.android.cursor.dir/email");
				sharingIntent.putExtra(Intent.EXTRA_EMAIL,
						"kayssar.daher@supelec.fr");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri
						.fromFile(new File(Environment
								.getExternalStorageDirectory()
								+ "/NTNOE/EDT_FULL" + ".ics")));
				sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Mon EDT");
				context.startActivity(Intent.createChooser(sharingIntent,
						"Partager avec")
						.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		default:
			context.startActivity((new Intent(context, Edt.class))
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			break;
		}

	}

}
