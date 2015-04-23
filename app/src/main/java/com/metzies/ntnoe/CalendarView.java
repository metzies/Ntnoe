package com.metzies.ntnoe;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

public class CalendarView extends Activity {

	public GregorianCalendar month;// calendar instances.

	public CalendarAdapter adapter;// adapter instance

	private static int currentWeek = -1;
	private static int currentMonth;
	private static int year;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);
		getWindow().setBackgroundDrawable(null);

		// disabling the Home As Up arrow on the actionbar for design purposes
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		preflogs = getSharedPreferences("logs", Context.MODE_PRIVATE);

		if (getIntent().getIntExtra("shownmonth", 100) == 100) {

			if (GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) == GregorianCalendar.JULY
					|| GregorianCalendar.getInstance().get(
							GregorianCalendar.MONTH) == GregorianCalendar.AUGUST) {

				month = (GregorianCalendar) GregorianCalendar.getInstance();
				month.set(GregorianCalendar.MONTH, GregorianCalendar.JUNE);
				currentMonth = GregorianCalendar.JUNE + 1;
				year = GregorianCalendar.getInstance().get(
						GregorianCalendar.YEAR);

			} else {

				month = (GregorianCalendar) GregorianCalendar.getInstance();
				currentMonth = GregorianCalendar.getInstance().get(
						GregorianCalendar.MONTH) + 1;
				year = GregorianCalendar.getInstance().get(
						GregorianCalendar.YEAR);

			}
		} else {

			currentMonth = getIntent().getIntExtra("shownmonth", 100);

			month = (GregorianCalendar) GregorianCalendar.getInstance();
			month.set(GregorianCalendar.MONTH, currentMonth - 1);

			year = getIntent()
					.getIntExtra(
							"shownyear",
							GregorianCalendar.getInstance().get(
									GregorianCalendar.YEAR));

		}

		adapter = new CalendarAdapter(this, month);

		((GridView) findViewById(R.id.gridview)).setAdapter(adapter);

		setTitle(makeTitle());

		((GridView) findViewById(R.id.gridview))
				.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {

						((CalendarAdapter) parent.getAdapter()).setSelected(v);
						String selectedGridDate = CalendarAdapter.dayString
								.get(position);
						String[] separatedTime = selectedGridDate.split("-");
						String gridvalueString = separatedTime[2].replaceFirst(
								"^0*", "");// taking last part of date. ie; 2
											// from 2012-12-02.
						int gridvalue = Integer.parseInt(gridvalueString);
						// navigate to next or previous month on clicking
						// offdays.
						if ((gridvalue > 10) && (position < 8)
								&& currentMonth > 8) {
							setPreviousMonth();
							currentMonth--;
							refreshCalendar();
						} else if ((gridvalue < 7) && (position > 28)
								&& currentMonth < 6) {
							setNextMonth();
							currentMonth++;
							refreshCalendar();
						}
						((CalendarAdapter) parent.getAdapter()).setSelected(v);

						showDay(separatedTime);
					}

				});
	}

	protected void setNextMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMaximum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) + 1),
					month.getActualMinimum(GregorianCalendar.MONTH), 1);
		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) + 1);
		}

	}

	private void showDay(String[] separatedTime) {

		GregorianCalendar greg = new GregorianCalendar(
				Integer.parseInt(separatedTime[0]),
				Integer.parseInt(separatedTime[1]) - 1,
				Integer.parseInt(separatedTime[2]));
		currentWeek = greg.get(GregorianCalendar.WEEK_OF_YEAR);
		int currentday = greg.get(GregorianCalendar.DAY_OF_WEEK) - 1;

		DataBaseHandler dbh = new DataBaseHandler(this);
		List<Event> evlist = Manipulator.organize(dbh.getDayEvents(currentWeek,
				currentday));
		dbh.close();

		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(CalendarView.this,
						R.style.AlertDialogCustom));

		String str = "";
		// will be shown in the alertdialog title

		switch (currentday) {
		case 0:
			str = getResources().getString(R.string.sunday);
			break;
		case 1:
			str = getResources().getString(R.string.monday);
			break;
		case 2:
			str = getResources().getString(R.string.tuesday);
			break;
		case 3:
			str = getResources().getString(R.string.wednesday);
			break;
		case 4:
			str = getResources().getString(R.string.thursday);
			break;
		case 5:
			str = getResources().getString(R.string.friday);
			break;
		case 6:
			str = getResources().getString(R.string.saturday);
			break;
		}

		str = str + " " + greg.get(GregorianCalendar.DAY_OF_MONTH) + "/"
				+ (greg.get(GregorianCalendar.MONTH) + 1) + "/"
				+ greg.get(GregorianCalendar.YEAR);

		// chekcing if the day has any classes
		if (evlist.isEmpty()) {
			builder.setMessage(R.string.empty_day).setTitle(str);
		} else {
			// showing the classes if any
			LinearLayout ll = new LinearLayout(CalendarView.this);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.addView(affichageEDT(evlist));
			ll.setPadding(0, 0, 0, 6);

			builder.setTitle(str).setView(ll);

		}

		builder.create().show();
	}

	protected void setPreviousMonth() {
		if (month.get(GregorianCalendar.MONTH) == month
				.getActualMinimum(GregorianCalendar.MONTH)) {
			month.set((month.get(GregorianCalendar.YEAR) - 1),
					month.getActualMaximum(GregorianCalendar.MONTH), 1);

		} else {
			month.set(GregorianCalendar.MONTH,
					month.get(GregorianCalendar.MONTH) - 1);
		}
	}

	public void refreshCalendar() {
		setTitle(makeTitle());
		adapter.refreshDays();
		adapter.notifyDataSetChanged();

	}

	private Menu amenu;
	private SharedPreferences preflogs;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			amenu.performIdentifierAction(R.id.more, 0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public static CharSequence makeTitle() {
		String res;

		switch (currentMonth) {
		case 1:
			res = "Janvier ";
			break;
		case 2:
			res = "Fevrier ";
			break;
		case 3:
			res = "Mars ";
			break;
		case 4:
			res = "Avril ";
			break;
		case 5:
			res = "Mai ";
			break;
		case 6:
			res = "Juin ";
			break;
		case 7:
			res = "Juillet ";
			break;
		case 8:
			res = "Août ";
			break;
		case 9:
			res = "Septembre ";
			break;
		case 10:
			res = "Octobre ";
			break;
		case 11:
			res = "Novembre ";
			break;
		case 12:
			res = "Décembre ";
			break;
		default:
			res = "Marche pas";
			break;
		}

		res += year;

		return (CharSequence) res;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		amenu = menu;

		getMenuInflater().inflate(R.menu.edt, menu);
		menu.findItem(R.id.action_login).setTitle(
				(CharSequence) preflogs.getString("login", "Vous !"));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items

		switch (item.getItemId()) {

		case R.id.action_back:
			if (currentMonth == 1) {
				currentMonth = 12;
				year--;
				setPreviousMonth();
				refreshCalendar();

				return true;
			} else if (currentMonth <= 6 || currentMonth >= 10) {
				setPreviousMonth();
				currentMonth--;
				refreshCalendar();
				return true;
			} else {
				Toast.makeText(this, "C'est le début de l'année",
						Toast.LENGTH_SHORT).show();
				return true;
			}
		case R.id.action_next:
			if (currentMonth == 12) {
				currentMonth = 1;
				year++;
				setNextMonth();
				refreshCalendar();
				return true;
			}
			if (currentMonth <= 5 || currentMonth >= 9) {
				currentMonth++;
				setNextMonth();
				refreshCalendar();
				return true;
			} else {
				Toast.makeText(this, "C'est la fin de l'année",
						Toast.LENGTH_SHORT).show();
				return true;
			}

		case android.R.id.home:
			Intent i = new Intent(this, Edt.class);
			i.putExtra("shownmonth", currentMonth);
			i.putExtra("shownyear", year);
			startActivity(i);
			finish();
			return true;

		case R.id.action_login:
			// indique à qui est l'emploi de temps affiché
			return true;

		case R.id.action_settings_activity:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		case R.id.action_update:

			// on met à jour l'EDT en gardant Créneaux et évènements ponctuels

			AlertDialog.Builder builder = new AlertDialog.Builder(
					new ContextThemeWrapper(this, R.style.AlertDialogCustom));
			builder.setMessage("Voulez-vous mettre à jour l'EDT?")
					.setCancelable(true)
					.setPositiveButton("Oui",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									// SharedPreferences preflogs =
									// getSharedPreferences(
									// "logs", Context.MODE_PRIVATE);

									File dirext = new File(Environment
											.getExternalStorageDirectory()
											+ "/NTNOE/");

									if (dirext.isDirectory()) {
										String[] children = dirext.list();
										for (int i = 0; i < children.length; i++) {
											if (children[i].contains(".ics")) {
												new File(dirext, children[i])
														.delete();
											}
										}
									}

									(new RecupICS(preflogs.getString("login",
											"8"), preflogs.getString(
											"password", "8"),
											CalendarView.this, true)).execute();
								}
							})
					.setNegativeButton("Annuler",
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

	private TextView spaceTV(int i) {
		TextView tvSpace = (new TextView(getApplicationContext()));
		tvSpace.setTextSize(i);
		return tvSpace;
	}

	private TextView lunchPauseTV() {

		TextView tvPause = new TextView(getApplicationContext());
		tvPause.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		tvPause.setTextSize(4);

		tvPause.setBackgroundResource(R.drawable.lunch_break);
		return tvPause;

	}

	public ScrollView affichageEDT(List<Event> evlist) {

		LinearLayout ll = new LinearLayout(this);
		Boolean lunch = false;

		ScrollView scrollView = new ScrollView(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setPadding(16, 16, 16, 16);

		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		if (!evlist.isEmpty()) {

			for (int i = evlist.size() - 1; i > -1; i--) {

				Event cr = evlist.get(i);

				// TODO y'a des dates qui sont en train de foutre la merde

				if (lunch == false
						&& Integer.parseInt(cr.getSdate().substring(11, 13)) > 11) {

					lunch = true;
					ll.addView(lunchPauseTV());
					ll.addView(spaceTV(8));
				}

				TextView tv = new TextView(this);
				tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));

				tv.setText(cr.getSummary() + "\n\n" + cr.getLocation() + "  "
						+ cr.getSdate().substring(11).replace(":", "h") + " | "
						+ cr.getEdate().substring(11).replace(":", "h"));

				tv.setGravity(Gravity.CENTER);
				tv.setTextColor(Color.WHITE);
				tv.setTextSize(18);

				switch (cr.getType()) {
				case 5:
					tv.setBackgroundResource(R.color.schedule_orange);
					break;
				case 9:
					tv.setBackgroundResource(R.color.schedule_blue);
					break;
				case 10:
					tv.setBackgroundResource(R.color.schedule_orange);
					break;
				case 11:
					tv.setBackgroundResource(R.color.schedule_green);
					break;
				case 12:
					tv.setBackgroundResource(R.color.schedule_red);
					break;
				case 13:
					tv.setBackgroundResource(R.color.schedule_brown);
					break;
				case 14:
					tv.setBackgroundResource(R.color.schedule_violet);
					break;
				default:
					tv.setBackgroundResource(R.color.schedule_grey);
					break;
				}

				if (cr.getSgreg().before((Calendar) new GregorianCalendar())
						&& cr.getEgreg().after(
								(Calendar) new GregorianCalendar())) {
					tv.setTextColor(getResources().getColor(R.color.now_grey));

					tv.setTypeface(null, Typeface.BOLD_ITALIC);
				}

				tv.setPadding(16, 16, 16, 16);

				ll.addView(tv);

				ll.addView(spaceTV(8));

			}

			if (lunch == false) {
				ll.addView(lunchPauseTV());
			}

			scrollView.addView(ll);

			return scrollView;

		} else {
			TextView tv = new TextView(this);
			tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			// TODO use proper string here, dont hard code
			tv.setText("Rien ce jour-ci");

			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(Color.rgb(200, 200, 200));
			tv.setTextSize(18);

			ll.addView(tv);
			scrollView.addView(ll);

			return scrollView;
		}

	}
}
