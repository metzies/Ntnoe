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

import java.util.GregorianCalendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

/*
 * cette classe ajoute un évènement ponctuel à l'edt
 * on affiche un sélectionneur d'heure pour le début et un autre pour la fin
 * et bien sûr un sélectionneur de date
 */
public class SingleEvent extends FragmentActivity {

	private static GregorianCalendar sgreg;
	private static GregorianCalendar egreg;
	private static LinearLayout sevent_ll;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_single_event);

		getWindow().setBackgroundDrawable(null);

		sevent_ll = (LinearLayout) findViewById(R.id.sevent_list_ll);

		sgreg = new GregorianCalendar();

		egreg = new GregorianCalendar();

		loadSevents();

		setTitle("Evènement");

	}

	private void addSeventRow(View v) {

		LinearLayout srow = (LinearLayout) getLayoutInflater().inflate(
				R.layout.sevent_row,
				(LinearLayout) findViewById(R.id.sevent_list_ll), false);

		(sevent_ll).addView(srow);

	}

	private void loadSevents() {
		DataBaseHandler dbh = new DataBaseHandler(this);
		List<Event> evlist = dbh.getSevents();
		dbh.close();

		Log.e("Devik", "there are " + evlist.size() + "sevents");

		if (!evlist.isEmpty()) {
			for (int i = 0; i != evlist.size(); i++) {

				Event ev = evlist.get(i);

				addSeventRow(null);

				((TextView) ((LinearLayout) sevent_ll.getChildAt(i))
						.getChildAt(0)).setText(ev.getSummary());
				String date = ev.getSdate().substring(8, 10) + "/"
						+ ev.getSdate().substring(5, 7) + "/"
						+ ev.getSdate().substring(0, 4);
				((TextView) ((LinearLayout) sevent_ll.getChildAt(i))
						.getChildAt(1)).setText(date);
			}
		} else {
			((RelativeLayout) ((TextView) findViewById(R.id.sevent_list_description))
					.getParent()).removeViewAt(0);
			((RelativeLayout) sevent_ll.getParent()).removeView(sevent_ll);
		}
	}

	public void deleteSevent(View v) {

		LinearLayout therow = (LinearLayout) v.getParent();

		String evsummary = (String) ((TextView) (therow).getChildAt(0))
				.getText();

		DataBaseHandler dbh = new DataBaseHandler(this);
		dbh.deleteEvent(evsummary);
		dbh.close();
		((LinearLayout) therow.getParent()).removeView(therow);

	}

	public void sendMessage(View v) {

		String title = ((EditText) findViewById(R.id.sevent_title)).getText()
				.toString();

		/*
		 * on s'assure quand même que l'utilisateur ne fait pas n'importe quoi
		 */
		if (!title.isEmpty()) {

			DataBaseHandler dbh = new DataBaseHandler(getApplicationContext());
			dbh.addEvent(new Event(sgreg, egreg, 8, title,
					((EditText) findViewById(R.id.sevent_location)).getText()
							.toString()));

			dbh.close();

			finish();

		} else {
			Toast.makeText(getApplicationContext(),
					"Veuillez saisir un intitulé", Toast.LENGTH_SHORT).show();
		}

	}

	public void showDatePickerDialog(View v) {
		(new DatePickerFragment()).show(getFragmentManager(), "datePicker");
	}

	public void showSTimePickerDialog(View v) {
		(new STimePickerFragment()).show(getFragmentManager(), "timePicker");
	}

	public void showETimePickerDialog(View v) {
		(new ETimePickerFragment()).show(getFragmentManager(), "timePicker");
	}

	private static void setStart(int hourOfDay, int minute) {

		sgreg.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
		sgreg.set(GregorianCalendar.MINUTE, minute);

	}

	private static void setDate(int year, int month, int day) {

		sgreg.set(GregorianCalendar.YEAR, year);
		sgreg.set(GregorianCalendar.MONTH, month);
		sgreg.set(GregorianCalendar.DAY_OF_MONTH, day);
		egreg.set(GregorianCalendar.YEAR, year);
		egreg.set(GregorianCalendar.MONTH, month);
		egreg.set(GregorianCalendar.DAY_OF_MONTH, day);
	}

	private static void setEnd(int hourOfDay, int minute) {

		egreg.set(GregorianCalendar.HOUR_OF_DAY, hourOfDay);
		egreg.set(GregorianCalendar.MINUTE, minute);

	}

	public static class STimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int hour = SingleEvent.sgreg.get(GregorianCalendar.HOUR_OF_DAY);
			int minute = SingleEvent.sgreg.get(GregorianCalendar.MINUTE);

			TimePickerDialog tpd = new TimePickerDialog(getActivity(), this,
					hour, minute, DateFormat.is24HourFormat(getActivity()));

			tpd.setTitle(getResources().getString(R.string.set_time_start));

			// Create a new instance of TimePickerDialog and return it
			return tpd;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			SingleEvent.setStart(hourOfDay, minute);
		}
	}

	public static class ETimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			int hour = SingleEvent.egreg.get(GregorianCalendar.HOUR_OF_DAY);
			int minute = SingleEvent.egreg.get(GregorianCalendar.MINUTE);

			TimePickerDialog tpd = new TimePickerDialog(getActivity(), this,
					hour, minute, DateFormat.is24HourFormat(getActivity()));

			tpd.setTitle(getResources().getString(R.string.set_time_start));

			// Create a new instance of TimePickerDialog and return it
			return tpd;
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			SingleEvent.setEnd(hourOfDay, minute);
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker

			int year = sgreg.get(GregorianCalendar.YEAR);
			int month = sgreg.get(GregorianCalendar.MONTH);
			int day = sgreg.get(GregorianCalendar.DAY_OF_MONTH);

			DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,
					year, month, day);

			GregorianCalendar now = new GregorianCalendar();

			dpd.getDatePicker().setMaxDate(
					new GregorianCalendar(now.get(GregorianCalendar.YEAR) + 1,
							5, 30).getTimeInMillis());
			dpd.getDatePicker().setMinDate(
					new GregorianCalendar(now.get(GregorianCalendar.YEAR) - 1,
							8, 1).getTimeInMillis());

			return dpd;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			SingleEvent.setDate(year, month, day);
		}
	}
}