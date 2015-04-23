/*
 * Ce fichier fait partie du projet NTNOE sur Android.
 * 
 * Les développeurs mettent ce projet à disposition de tous sous les conditions suivantes:
 * 
 * Vous avez le droit d'examiner et tester le code.
 * 
 * Vous avez le droit de copier et modifier le code à condition de citer clairement et publiquement vos sources.
 * 
 * Vous n'avez pas le droit de désigner ce code (modifié ou non) comentièrement le votre.
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;

public class Manipulator {
	private Context mContext;

	// le premier manipulator crée un calendrier et l'initialise
	public Manipulator(Context c) {
		mContext = c;
	}

	/*
	 * renvoie la date du lundi de la semaine. le string renvoyé sera affiché
	 * dans le titre d'EDT
	 */
	public static String mondaydate(int shownweek, int year, int day) {
		// TODO
		Calendar greg = GregorianCalendar.getInstance();
		greg.clear();
		greg.set(GregorianCalendar.YEAR, year);
		greg.set(GregorianCalendar.WEEK_OF_YEAR, shownweek);

		switch (day) {
		case 1:
			greg.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
			break;
		case 2:
			greg.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.TUESDAY);
			break;
		case 3:
			greg.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.WEDNESDAY);
			break;
		case 4:
			greg.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.THURSDAY);
			break;
		case 5:
			greg.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.FRIDAY);
			break;
		}

		return greg.get(GregorianCalendar.DAY_OF_MONTH) + "/"
				+ (greg.get(GregorianCalendar.MONTH) + 1) + "/"
				+ greg.get(GregorianCalendar.YEAR);
	}

	/*
	 * méthode qui réordonne les vecteurs selon l'ordre chronologique, pour
	 * l'affichage appelée dans getdailyschedule()
	 */
	public static List<Event> organize(List<Event> listeCours) {
		// dans liste cours, les horaires ressemblent à 14H00 - 16H00

		for (int i = 0; i != listeCours.size(); i++) {
			Event current = listeCours.get(i);
			int runner = i;

			while (runner > 0
					&& Integer.parseInt(current.getSdate().substring(11, 13)) < Integer
							.parseInt(listeCours.get(runner - 1).getSdate()
									.substring(11, 13))) {
				listeCours.set(runner, listeCours.get(runner - 1));
				runner--;
			}
			listeCours.set(runner, current);
		}

		int k = listeCours.size() - 1;
		for (int i = 0; i < listeCours.size() / 2; i++) {
			Event temp = listeCours.get(k);
			listeCours.set(k, listeCours.get(i));
			listeCours.set(i, temp);
			k--;
		}

		return listeCours;
	}

	public List<Integer> getWeeksWithLangs(int sequence) {

		switch (sequence) {
		case 5:
			sequence = 1;
			break;
		case 6:
			sequence = 2;
			break;
		case 7:
			sequence = 3;
			break;
		case 8:
			sequence = 4;
			break;
		default:
			break;
		}

		int counter = 0;

		SharedPreferences preflogs = mContext.getSharedPreferences("logs",
				Context.MODE_PRIVATE);
		List<Integer> weekswithlangs = new ArrayList<Integer>();

		for (int i = 34; i != 53; i++) {

			int k = preflogs.getInt("lw" + i, 0);

			if (k != 0) {

				if (counter >= (sequence - 1) * 7 && counter < sequence * 7) {
					weekswithlangs.add(k);

				}
				counter++;

			}
		}
		for (int i = 1; i != 27; i++) {

			int k = preflogs.getInt("lw" + i, 0);

			if (k != 0) {

				if (counter >= (sequence - 1) * 7 && counter < sequence * 7) {
					weekswithlangs.add(k);

				}
				counter++;

			}
		}

		return weekswithlangs;

	}

	// fill the DB with the original ics' contents (modulo les cours de langues)
	public void dbTransfer(String path) {

		try {

			FileInputStream fin = new FileInputStream(path);

			ComponentList original = (new CalendarBuilder()).build(fin)
					.getComponents(Component.VEVENT);

			fin.close();

			// ComponentList original =
			// this.calendar.getComponents(Component.VEVENT);

			int currentyear = GregorianCalendar.getInstance().get(
					GregorianCalendar.YEAR);

			int currentmonth = GregorianCalendar.getInstance().get(
					GregorianCalendar.MONTH) + 1;

			VEvent current;
			int evyear;
			int evmonth;
			GregorianCalendar sdate;
			GregorianCalendar edate;

			DataBaseHandler dbh = new DataBaseHandler(mContext);

			SQLiteDatabase db = dbh.getWritableDatabase();

			db.beginTransaction();

			SharedPreferences preflogs = mContext.getSharedPreferences("logs",
					Context.MODE_PRIVATE);
			Editor ed = preflogs.edit();

			for (int i = 0; i != original.size(); i++) {
				current = (VEvent) original.get(i);

				sdate = new GregorianCalendar();
				sdate.setTime(current.getStartDate().getDate());

				edate = new GregorianCalendar();
				edate.setTime(current.getEndDate().getDate());

				evyear = sdate.get(GregorianCalendar.YEAR);
				evmonth = sdate.get(GregorianCalendar.MONTH) + 1;

				if ((currentyear == evyear && (evmonth >= 9
						&& currentmonth >= 9 || evmonth <= 8
						&& currentmonth <= 8))

						|| (evyear == currentyear - 1 && evmonth >= 9 && currentmonth <= 8)
						|| (evyear == currentyear + 1 && evmonth <= 8 && currentmonth >= 9)) {

					if (!current.getProperty(Property.SUMMARY).getValue()
							.contains((CharSequence) "vivantes")) {

						db.insert("events", null, DataBaseHandler
								.getValues(new Event(sdate, edate, Integer
										.parseInt(current.getDescription()
												.getValue().toString()),
										current.getSummary().getValue()
												.toString(), current
												.getLocation().getValue()
												.toString())));

					} else {
						ed.putInt(
								"lw"
										+ sdate.get(GregorianCalendar.WEEK_OF_YEAR),
								sdate.get(GregorianCalendar.WEEK_OF_YEAR));
					}
				}
			}

			ed.commit();
			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
			dbh.close();

		} catch (FileNotFoundException e) {

			// TODO something here

			/*
			 * Eventer ev = new Eventer("Rien cette semaine!", 1, 1);
			 * ComponentList week = new ComponentList(); week.add((VEvent) new
			 * VEvent()); ev.addEvent(week); updateCalendar(week);
			 */
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}

	}

	public void dbTransferProf(String path) {

		try {

			FileInputStream fin = new FileInputStream(path);
			ComponentList original = (new CalendarBuilder()).build(fin)
					.getComponents(Component.VEVENT);
			fin.close();
			// ComponentList original =
			// this.calendar.getComponents(Component.VEVENT);

			int currentyear = GregorianCalendar.getInstance().get(
					GregorianCalendar.YEAR);

			VEvent current;
			int evyear;
			int evmonth;
			GregorianCalendar sdate;
			GregorianCalendar edate;

			DataBaseHandler dbh = new DataBaseHandler(mContext);

			// TODO dont delete all, delete all the non language classes

			SQLiteDatabase db = dbh.getWritableDatabase();

			db.beginTransaction();

			for (int i = 0; i != original.size(); i++) {
				current = (VEvent) original.get(i);

				sdate = new GregorianCalendar();
				sdate.setTime(current.getStartDate().getDate());

				edate = new GregorianCalendar();
				edate.setTime(current.getEndDate().getDate());

				evyear = sdate.get(GregorianCalendar.YEAR);
				evmonth = sdate.get(GregorianCalendar.MONTH);

				if ((currentyear == evyear && (evmonth > 7 || evmonth < 6))
						|| (evyear == currentyear - 1 && evmonth > 7)
						|| (evyear == currentyear + 1 && evmonth < 6)) {

					db.insert("events", null,
							DataBaseHandler
									.getValues(new Event(sdate, edate, Integer
											.parseInt(current.getDescription()
													.getValue().toString()),
											current.getSummary().getValue()
													.toString(), current
													.getLocation().getValue()
													.toString())));

				}
			}

			db.setTransactionSuccessful();
			db.endTransaction();
			db.close();
			dbh.close();

		} catch (FileNotFoundException e) {

			// TODO something here

			/*
			 * Eventer ev = new Eventer("Rien cette semaine!", 1, 1);
			 * ComponentList week = new ComponentList(); week.add((VEvent) new
			 * VEvent()); ev.addEvent(week); updateCalendar(week);
			 */
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}

	}

	public void dbLangAdd(List<Event> evVec) {
		DataBaseHandler dbh = new DataBaseHandler(mContext);

		if (!evVec.isEmpty()) {
			for (Event ev : evVec) {

				// transform desc_ev into evs

				// adding the desc_evs for the posterity
				dbh.addEvent(ev);

				int thesequence = Integer.parseInt(ev.getLocation());

				String thesummary = ev.getSummary();

				GregorianCalendar sdate = new GregorianCalendar();
				GregorianCalendar edate = new GregorianCalendar();
				sdate.clear();
				edate.clear();

				String theclass = ev.getSdate();

				if (theclass.contains("Lu")) {
					sdate.set(GregorianCalendar.DAY_OF_WEEK,
							GregorianCalendar.MONDAY);
				} else if (theclass.contains("Ma")) {
					sdate.set(GregorianCalendar.DAY_OF_WEEK,
							GregorianCalendar.TUESDAY);
				} else if (theclass.contains("Me")) {
					sdate.set(GregorianCalendar.DAY_OF_WEEK,
							GregorianCalendar.WEDNESDAY);
				} else if (theclass.contains("Je")) {
					sdate.set(GregorianCalendar.DAY_OF_WEEK,
							GregorianCalendar.THURSDAY);
				} else if (theclass.contains("Ve")) {
					sdate.set(GregorianCalendar.DAY_OF_WEEK,
							GregorianCalendar.FRIDAY);
				}

				int thetype = -5;

				if (ev.getType() == 0) {

					if (theclass.contains("13h30")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 13);
						sdate.set(GregorianCalendar.MINUTE, 30);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 15);
						edate.set(GregorianCalendar.MINUTE, 0);

					} else if (theclass.contains("15h15")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 15);
						sdate.set(GregorianCalendar.MINUTE, 15);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 16);
						edate.set(GregorianCalendar.MINUTE, 45);

					} else if (theclass.contains("8h30")
							&& !theclass.contains("18h30")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 8);
						sdate.set(GregorianCalendar.MINUTE, 30);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 10);
						edate.set(GregorianCalendar.MINUTE, 0);

					} else if (theclass.contains("10h30")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 10);
						sdate.set(GregorianCalendar.MINUTE, 30);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 12);
						edate.set(GregorianCalendar.MINUTE, 0);

					} else if (theclass.contains("16h45")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 16);
						sdate.set(GregorianCalendar.MINUTE, 45);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 18);
						edate.set(GregorianCalendar.MINUTE, 15);

					} else if (theclass.contains("18h30")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 18);
						sdate.set(GregorianCalendar.MINUTE, 30);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 20);
						edate.set(GregorianCalendar.MINUTE, 0);

					}

					thetype = 5;

				} else if (ev.getType() == 1) {

					if (theclass.contains("18h00")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 18);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 19);

					} else if (theclass.contains("19h00")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 19);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 20);

					} else if (theclass.contains("20h00")) {

						sdate.set(GregorianCalendar.HOUR_OF_DAY, 20);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 21);

					} else if (theclass.contains("21h00")) {
						sdate.set(GregorianCalendar.HOUR_OF_DAY, 21);
						edate.set(GregorianCalendar.HOUR_OF_DAY, 22);

					}

					thetype = 14;

				}

				// now create 7 events depending ont the sequence

				int realmonth = GregorianCalendar.getInstance().get(
						GregorianCalendar.MONTH) + 1;
				int realyear = GregorianCalendar.getInstance().get(
						GregorianCalendar.YEAR);

				List<Integer> weekswithlangs = getWeeksWithLangs(thesequence);

				for (int k : weekswithlangs) {
					if (realmonth >= 9 && realmonth <= 12) {
						if (k > 34 && k < 53) {
							edate.set(GregorianCalendar.YEAR, realyear);
							sdate.set(GregorianCalendar.YEAR, realyear);
							edate.set(GregorianCalendar.WEEK_OF_YEAR, k);
							sdate.set(GregorianCalendar.WEEK_OF_YEAR, k);

							dbh.addEvent(new Event(sdate, edate, thetype,
									thesummary, ""));
						} else if (k >= 1 && k < 27) {
							edate.set(GregorianCalendar.YEAR, realyear + 1);
							sdate.set(GregorianCalendar.YEAR, realyear + 1);
							edate.set(GregorianCalendar.WEEK_OF_YEAR, k);
							sdate.set(GregorianCalendar.WEEK_OF_YEAR, k);

							dbh.addEvent(new Event(sdate, edate, thetype,
									thesummary, ""));
						}
					} else if (realmonth <= 8 && realmonth >= 1) {
						if (k > 34 && k < 53) {
							edate.set(GregorianCalendar.YEAR, realyear - 1);
							sdate.set(GregorianCalendar.YEAR, realyear - 1);
							edate.set(GregorianCalendar.WEEK_OF_YEAR, k);
							sdate.set(GregorianCalendar.WEEK_OF_YEAR, k);

							dbh.addEvent(new Event(sdate, edate, thetype,
									thesummary, ""));
						} else if (k >= 1 && k < 27) {
							edate.set(GregorianCalendar.YEAR, realyear);
							sdate.set(GregorianCalendar.YEAR, realyear);
							edate.set(GregorianCalendar.WEEK_OF_YEAR, k);
							sdate.set(GregorianCalendar.WEEK_OF_YEAR, k);

							dbh.addEvent(new Event(sdate, edate, thetype,
									thesummary, ""));
						}
					}
				}

			}
			/*
			 * end of for loop
			 */

		}

		dbh.close();

	}

	public List<Event> getDailySchedule(int week, int day) {
		DataBaseHandler dbh = new DataBaseHandler(mContext);
		List<Event> evlist = dbh.getDayEvents(week, day);
		dbh.close();
		return organize(evlist);
	}

}
