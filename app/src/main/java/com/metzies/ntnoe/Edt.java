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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.metzies.ntnoe.app.R;

public class Edt extends FragmentActivity implements
		ViewPager.OnPageChangeListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private static List<Event> listeCours;
	private static Manipulator man;
	public static int shownweek;
	private static Context mContext;
	private static Menu amenu;
	private static SharedPreferences preflogs;
	private static int year = GregorianCalendar.getInstance().get(
			GregorianCalendar.YEAR);

	private static LinearLayout mon;
	private static LinearLayout tue;
	private static LinearLayout wed;
	private static LinearLayout thu;
	private static LinearLayout fri;

	// TODO changer de semaines en swipant sur un vendredi ou un lundi
	// http://stackoverflow.com/questions/4027553/android-gesturedetector-wont-catch-gestures
	// http://stackoverflow.com/questions/11421368/android-fragment-oncreateview-with-gestures

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("shownweek", shownweek);

		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		// etc.
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		shownweek = savedInstanceState.getInt("shownweek");

		super.onRestoreInstanceState(savedInstanceState);
		// Restore UI state from the savedInstanceState.
		// This bundle has also been passed to onCreate.
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		preflogs = getApplication().getSharedPreferences("logs",
				Context.MODE_PRIVATE);

		if (!preflogs.getBoolean("initialized", false)) {
			startActivity(new Intent(this, Signup.class));
			this.finish();
			onStop();
		}
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.activity_edt);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_week_view);
		}
		getWindow().setBackgroundDrawable(null);

		mon = ((LinearLayout) findViewById(R.id.monday_layout));
		tue = ((LinearLayout) findViewById(R.id.tuesday_layout));
		wed = ((LinearLayout) findViewById(R.id.wednesday_layout));
		thu = ((LinearLayout) findViewById(R.id.thursday_layout));
		fri = ((LinearLayout) findViewById(R.id.friday_layout));

		// actionBar.setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(false);

		/*
		 * Ici on récupère le temps réel, càd dans quel jour de la semaine on
		 * est et dans quelle semaine de l'année on est. Selon la semaine de
		 * l'année, on ira récupérer dans la mémoire du téléphone le fichier
		 * .ics correspondant
		 */
		int dayofweek = (new GregorianCalendar())
				.get(GregorianCalendar.DAY_OF_WEEK);

		if (getIntent().getIntExtra("shownmonth", 100) == 100) {

			try {

				shownweek = savedInstanceState.getInt("shownweek");

			} catch (NullPointerException e) {

				if (GregorianCalendar.getInstance().get(
						GregorianCalendar.WEEK_OF_YEAR) > 26
						&& GregorianCalendar.getInstance().get(
								GregorianCalendar.WEEK_OF_YEAR) < 35) {
					shownweek = 27;
				} else {
					shownweek = (new GregorianCalendar())
							.get(GregorianCalendar.WEEK_OF_YEAR);

					if (shownweek == 52) {
						if (dayofweek == GregorianCalendar.SATURDAY
								|| dayofweek == GregorianCalendar.SUNDAY) {
							shownweek = 1;
							year = year++;
						}
					} else {
						if (dayofweek == GregorianCalendar.SATURDAY
								|| dayofweek == GregorianCalendar.SUNDAY) {
							shownweek++;
						}
					}
				}

			}

		} else {

			GregorianCalendar greg = new GregorianCalendar();
			greg.clear();
			greg.set(GregorianCalendar.MONTH,
					getIntent().getIntExtra("shownmonth", 2));
			greg.set(GregorianCalendar.DAY_OF_MONTH, GregorianCalendar.MONDAY);

			shownweek = greg.get(GregorianCalendar.WEEK_OF_YEAR) - 1;
			year = getIntent().getIntExtra("shownyear", 100);

		}

		/*
		 * Ici on examine si on est en WE. Généralement, si on est WE, on est
		 * plus intéressé par la semaine suivante que la précédente. on rajoute
		 * des conditions de vérification si on est en fin d'année et que c'est
		 * un weekend, ça serait bête de demander la semaine 53.
		 */

		// on initialise notre Manipulator

		man = new Manipulator(this);

		setTitle(Manipulator.mondaydate(shownweek, year, 1));

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

			mContext = getApplicationContext();

			// Create the adapter that will return a fragment for each of
			// the
			// five
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());

			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setOnPageChangeListener(this);
			mViewPager.setAdapter(mSectionsPagerAdapter);

			switch (dayofweek) {

			case GregorianCalendar.MONDAY:
				mViewPager.setCurrentItem(0);
				break;
			case GregorianCalendar.TUESDAY:
				mViewPager.setCurrentItem(1);
				break;
			case GregorianCalendar.WEDNESDAY:
				mViewPager.setCurrentItem(2);
				break;
			case GregorianCalendar.THURSDAY:
				mViewPager.setCurrentItem(3);
				break;
			case GregorianCalendar.FRIDAY:
				mViewPager.setCurrentItem(4);
				break;
			}

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			refreshWeek();
		}

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		setTitle(Manipulator.mondaydate(shownweek, year, position + 1));
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}

	public void refreshWeek() {

		try {
			mon.removeAllViews();
			tue.removeAllViews();
			wed.removeAllViews();
			thu.removeAllViews();
			fri.removeAllViews();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 1; i != 6; i++) {
			fillDay(i);
		}
	}

	public List<Event> getDayEvents(int shownweek, int day) {

		DataBaseHandler dbh = new DataBaseHandler(getApplicationContext());
		List<Event> evlist = dbh.getDayEvents(shownweek, day);
		dbh.close();

		return Manipulator.organize(evlist);
	}

	public void fillDay(final int day) {
		List<Event> evlist = getDayEvents(shownweek, day);

		if (!evlist.isEmpty()) {

			GregorianCalendar rtime = new GregorianCalendar();

			for (int i = evlist.size() - 1; i >= 0; i--) {
				final Event ev = evlist.get(i);
				rtime.clear();
				rtime.set(GregorianCalendar.YEAR,
						Integer.parseInt(ev.getSdate().substring(0, 4)));
				rtime.set(GregorianCalendar.MONTH,
						Integer.parseInt(ev.getSdate().substring(5, 7)));
				rtime.set(GregorianCalendar.DAY_OF_MONTH,
						Integer.parseInt(ev.getSdate().substring(8, 10)));
				rtime.set(GregorianCalendar.HOUR_OF_DAY,
						Integer.parseInt(ev.getSdate().substring(11, 13)));
				rtime.set(GregorianCalendar.MINUTE,
						Integer.parseInt(ev.getSdate().substring(14, 16)));

				TextView tv = new TextView(this);
				tv.setGravity(Gravity.CENTER);
				tv.setTextColor(getResources().getColor(R.color.near_white));
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				tv.setHeight((int) TypedValue.applyDimension(
						TypedValue.COMPLEX_UNIT_SP, 50, getResources()
								.getDisplayMetrics()));

				switch (ev.getType()) {
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

				tv.setText(makeTitle(ev.getSummary()));

				tv.setClickable(true);

				if (ev.getSgreg().before((Calendar) new GregorianCalendar())
						&& ev.getEgreg().after(
								(Calendar) new GregorianCalendar())) {
					tv.setTextColor(mContext.getResources().getColor(
							R.color.now_grey));
					tv.setTypeface(null, Typeface.BOLD_ITALIC);
				}

				tv.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						AlertDialog.Builder builder = new AlertDialog.Builder(
								new ContextThemeWrapper(Edt.this,
										R.style.AlertDialogCustom));

						String str = "";

						GregorianCalendar rtime = new GregorianCalendar();
						rtime.clear();
						rtime.set(GregorianCalendar.YEAR,
								Integer.parseInt(ev.getSdate().substring(0, 4)));
						rtime.set(
								GregorianCalendar.MONTH,
								Integer.parseInt(ev.getSdate().substring(5, 7)) - 1);
						rtime.set(GregorianCalendar.DAY_OF_MONTH, Integer
								.parseInt(ev.getSdate().substring(8, 10)));
						rtime.set(GregorianCalendar.HOUR_OF_DAY, Integer
								.parseInt(ev.getSdate().substring(11, 13)));
						rtime.set(GregorianCalendar.MINUTE, Integer.parseInt(ev
								.getSdate().substring(14, 16)));

						switch (day) {
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
						}

						str = str + " "
								+ rtime.get(GregorianCalendar.DAY_OF_MONTH)
								+ "/"
								+ (rtime.get(GregorianCalendar.MONTH) + 1)
								+ "/" + rtime.get(GregorianCalendar.YEAR);

						builder.setTitle(str);
						builder.setCancelable(true);

						TextView tv = new TextView(Edt.this);

						tv.setTextColor(getResources().getColor(
								R.color.near_white));
						tv.setGravity(Gravity.CENTER);

						tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
						tv.setLayoutParams(new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));

						tv.setText(ev.getSummary() + "\n\n" + ev.getLocation()
								+ "  "
								+ ev.getSdate().substring(11).replace(":", "h")
								+ " | "
								+ ev.getEdate().substring(11).replace(":", "h"));

						switch (ev.getType()) {
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

						builder.setView(tv);

						builder.create().show();
						tv = null;
					}
				});

				switch (day) {
				case 1:
					mon.addView(tv);
					break;
				case 2:
					tue.addView(tv);
					break;
				case 3:
					wed.addView(tv);
					break;
				case 4:
					thu.addView(tv);
					break;
				case 5:
					fri.addView(tv);
					break;
				}

				tv = null;
			}
		} else {
			TextView tv = new TextView(this);
			// TODO don't hardcode this
			tv.setText("Rien");
			tv.setTextColor(Color.rgb(200, 200, 200));
			tv.setGravity(Gravity.CENTER);
			tv.setHeight((int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_SP, 50, getResources()
							.getDisplayMetrics()));

			switch (day) {
			case 1:
				mon.addView(tv);
				break;
			case 2:
				tue.addView(tv);
				break;
			case 3:
				wed.addView(tv);
				break;
			case 4:
				thu.addView(tv);
				break;
			case 5:
				fri.addView(tv);
				break;
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.activity_week_view);
			refreshWeek();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.activity_edt);
		}
	}

	public static CharSequence makeTitle(String full) {

		String result = "";

		if (full.contains("TL")) {
			result = "TL ";
		} else if (full.contains("TD")) {
			result = "TD ";
		} else if (full.contains("xamen")) {
			result = "Exam ";
		} else if (full.contains("oral")) {
			result = "Oral ";
		} else if (full.contains("attrapage")) {
			result = "Rattrapage ";
		}

		if (full.contains("Signaux et systèmes 2")) {
			result += "Sig2 ";
		} else if (full.contains("Signaux et systèmes 1")) {
			result += "Sig1 ";
		} else if (full.contains("rojet de")) {
			result += "Projet ";
		} else if (full.contains("Systèmes logiques")) {
			result += "SLEA ";
		} else if (full.contains("conomie")) {
			result += "Eco ";
		} else if (full.contains("hysique des solides")) {
			result += "PhySol ";
		} else if (full.contains("électrotechnique")) {
			result += "ElectroTech ";
		} else if (full.contains("Anglais")) {
			result += "Anglais ";
		} else if (full.contains("Génie log")) {
			result += "Génilog ";
		} else if (full.contains("ondement de l")) {
			result += "FISDA ";
		} else if (full.contains("tatistiques pour l")) {
			result += "Stats ";
		} else if (full.contains("quantique")) {
			result += "PhyQ ";
		} else if (full.contains("Champs et propa")
				|| full.contains("propagation")) {
			result += "Champs & Propa ";
		} else if (full.contains("Proba") || full.contains("robabilit")) {
			result += "Proba ";
		} else if (full.contains("lectronique anal")) {
			result += "ELAN ";
		} else if (full.contains("odèles de prog")) {
			result += "Prog ";
		} else if (full.contains("acanc")) {
			result += "Vacances !";
		} else if (full.contains("vitesse variab")) {
			result += "CEVV ";
		} else if (full.contains("analyse statis")) {
			result += "RASS ";
		} else if (full.contains("Signal et comm")) {
			result += "Sig & Comm ";
		} else if (full.contains("HF")) {
			result += "HF ";
		} else if (full.contains("optimisatio")) {
			result += "MNO ";
		} else if (full.contains("rchitecture des syst")) {
			result += "Archi ";
		} else if (full.contains("conversion de l")) {
			result += "TCEE ";
		} else if (full.contains("Data Mining")) {
			result += "Data Mining ";
		} else if (full.contains("utomatiqu")) {
			result += "Autom ";
		} else if (full.contains("semi-cond")) {
			result += "Semi-Cond ";
		} else if (full.contains("ystème d'infor")) {
			result += "SI ";
		} else if (full.contains("estion de proje")) {
			result += "GdP ";
		} else if (full.contains("football")) {
			result += "Football ";
		} else if (full.contains("badminto")) {
			result += "Badminton ";
		} else if (full.contains("karate")) {
			result += "Karate ";
		} else if (full.contains("rugby")) {
			result += "Rugby ";
		} else if (full.contains("aviron")) {
			result += "Aviron ";
		} else if (full.contains("escalade")) {
			result += "Escalade ";
		} else if (full.contains("Espagnol")) {
			result += "Espagnol ";
		} else if (full.contains("Allemand")) {
			result += "Allemand ";
		} else if (full.contains("Chinois")) {
			result += "Chinois ";
		} else if (full.contains("Japonais")) {
			result += "Japonais ";
		} else if (full.contains("Arabe")) {
			result += "Arabe ";
		} else if (full.contains("Russe")) {
			result += "Russe ";
		} else {

			if (full.length() < 28) {
				result = full;
			} else {
				result = full.substring(0, 27) + "...";
			}

		}

		return (CharSequence) result;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		preflogs = getSharedPreferences("logs", Context.MODE_PRIVATE);

		// Inflate the menu items for use in the action bar
		getMenuInflater().inflate(R.menu.edt, menu);

		amenu = menu;

		/*
		 * Ici on affiche le nom_pre de l'utilisateur pour qu'on sache l'EDT est
		 * à qui
		 */
		menu.findItem(R.id.action_login).setTitle(
				(CharSequence) preflogs.getString("login", "Vous !"));

		return super.onCreateOptionsMenu(menu);

		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.edt, menu);
		// return true;
	}

	public void onBackPressed() {
		/*
		 * Si on quitte EDT, c'est généralement pas pour aller dans
		 * Attente.java, donc on renvoie au launcher
		 */
		finish();
		onStop();
		startActivity((new Intent(Intent.ACTION_MAIN)).addCategory(
				Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	// the following method should rectify the problems with legacy devices that
	// have a physical menu button
	// http://stackoverflow.com/questions/12277262/opening-submenu-in-action-bar-on-hardware-menu-button-click
	// add Menu amenu attribute to class to get this method to work
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			amenu.performIdentifierAction(R.id.more, 0);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(this, R.style.AlertDialogCustom));
		switch (item.getItemId()) {

		case android.R.id.home:
			GregorianCalendar greg = new GregorianCalendar();
			greg.clear();

			greg.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
			greg.set(GregorianCalendar.WEEK_OF_YEAR, shownweek);
			greg.set(GregorianCalendar.YEAR, year);
			greg.set(GregorianCalendar.HOUR_OF_DAY, 10);

			GregorianCalendar greg2 = new GregorianCalendar();
			greg2.clear();

			greg2.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SATURDAY);
			greg2.set(GregorianCalendar.WEEK_OF_YEAR, shownweek);
			greg2.set(GregorianCalendar.YEAR, year);
			greg2.set(GregorianCalendar.HOUR_OF_DAY, 10);

			if (greg.get(GregorianCalendar.YEAR) != greg2
					.get(GregorianCalendar.YEAR)) {
				year = greg.get(GregorianCalendar.YEAR);
			}

			Intent i = new Intent(this, CalendarView.class);
			i.putExtra("shownmonth", greg.get(GregorianCalendar.MONTH) + 1);
			i.putExtra("shownyear", year);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
			return true;

		case R.id.action_next:
			/*
			 * on repère si on est en fin d'année et on affiche la première
			 * semaine de l'année suivante
			 */

			if (shownweek == 52 || shownweek == 53) {
				shownweek = 1;
				year++;

			} else if (shownweek == 27) {
				Toast.makeText(getApplicationContext(),
						"C'est la fin de l'année", Toast.LENGTH_SHORT).show();
			} else {
				shownweek++;
			}

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

				setTitle(Manipulator.mondaydate(shownweek, year, 1));

				// Create the adapter that will return a fragment for each of
				// the
				// three
				// primary sections of the app.
				mSectionsPagerAdapter = new SectionsPagerAdapter(
						getSupportFragmentManager());

				// Set up the ViewPager with the sections adapter.
				mViewPager = (ViewPager) findViewById(R.id.pager);
				mViewPager.setOnPageChangeListener(this);
				mViewPager.setAdapter(mSectionsPagerAdapter);

				return true;
			} else {
				setTitle(Manipulator.mondaydate(shownweek, year, 1));
				refreshWeek();
				return true;
			}

		case R.id.action_back:
			// idem à action_next
			if (shownweek == 1) {
				shownweek = 52;
				year--;

			} else if (shownweek == 35) {
				Toast.makeText(getApplicationContext(),
						"C'est le début de l'année", Toast.LENGTH_SHORT).show();
			} else {
				shownweek--;
			}

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

				setTitle(Manipulator.mondaydate(shownweek, year, 1));

				// Create the adapter that will return a fragment for each of
				// the
				// three
				// primary sections of the app.
				mSectionsPagerAdapter = new SectionsPagerAdapter(
						getSupportFragmentManager());

				// Set up the ViewPager with the sections adapter.
				mViewPager = (ViewPager) findViewById(R.id.pager);

				mViewPager.setOnPageChangeListener(this);

				mViewPager.setAdapter(mSectionsPagerAdapter);

				return true;
			} else {
				setTitle(Manipulator.mondaydate(shownweek, year, 1));
				refreshWeek();
				return true;
			}

		case R.id.action_login:

			builder.setMessage(
					"Voulez-vous vous déconnecter? Toutes vos informations seront perdues.")
					.setCancelable(true)
					.setPositiveButton("Oui",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									/*
									 * On essaie de tout remettre à 0
									 */
									// SharedPreferences preflogs =
									// getSharedPreferences(
									// "logs",
									// Context.MODE_PRIVATE);

									SharedPreferences preflogs = getSharedPreferences(
											"logs", Context.MODE_PRIVATE);

									Editor editor = preflogs.edit();
									editor.clear();
									editor.commit();

									DataBaseHandler dbh = new DataBaseHandler(
											Edt.this);
									dbh.deleteAll();
									dbh.close();

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
									dirext.delete();

									File dir = new File(
											preflogs.getString(
													"baseDir",
													Environment
															.getDataDirectory()
															+ "/data/com.metzies.ntnoe.app/NTNOE/"));
									if (dir.isDirectory()) {
										String[] children = dir.list();
										for (int i = 0; i < children.length; i++) {
											if (children[i].contains(".ics")) {
												new File(dir, children[i])
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
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.cancel();
								}
							});
			builder.create().show();

			return true;

		case R.id.action_update:

			// on met à jour l'EDT en gardant Créneaux et évènements ponctuels

			builder = new AlertDialog.Builder(new ContextThemeWrapper(this,
					R.style.AlertDialogCustom));
			builder.setMessage("Voulez-vous mettre à jour l'EDT?")
					.setCancelable(true)
					.setPositiveButton("Oui",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

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
											"password", "8"), Edt.this, true))
											.execute();

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

		case R.id.action_settings_activity:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

	public static ScrollView affichageEDT(int j) {

		LinearLayout ll = new LinearLayout(mContext);
		Boolean lunch = false;

		ScrollView scrollView = new ScrollView(mContext);
		listeCours = man.getDailySchedule(shownweek, j);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setPadding(20, 20, 20, 6);

		scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		if (listeCours.size() != 0) {

			for (int i = listeCours.size() - 1; i > -1; i--) {

				Event cr = listeCours.get(i);

				if (lunch == false
						&& Integer.parseInt(cr.getSdate().substring(11, 13)) > 11) {

					lunch = true;
					ll.addView(lunchPauseTV());
					ll.addView(spaceTV(8));
				}

				TextView tv = new TextView(mContext);
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
					tv.setTextColor(mContext.getResources().getColor(
							R.color.now_grey));
					tv.setTypeface(null, Typeface.BOLD_ITALIC);
				}

				tv.setGravity(Gravity.CENTER);

				tv.setPadding(16, 16, 16, 16);

				ll.addView(tv);

				ll.addView(spaceTV(8));

			}

			// end of the for loop

			if (lunch == false) {
				ll.addView(lunchPauseTV());
			}

			scrollView.addView(ll);

			listeCours.clear();
			return scrollView;

		} else {
			TextView tv = new TextView(mContext);
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

	private static TextView spaceTV(int i) {
		TextView tvSpace = (new TextView(mContext));
		tvSpace.setTextSize(i);
		return tvSpace;
	}

	private static TextView lunchPauseTV() {

		TextView tvPause = new TextView(mContext);
		tvPause.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		tvPause.setTextSize(4);

		tvPause.setBackgroundResource(R.drawable.lunch_break);
		return tvPause;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.

			Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 5 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			try {

				// Log.e("Calling day number ",
				// getArguments().getInt(ARG_SECTION_NUMBER) + "");

				return affichageEDT(getArguments().getInt(ARG_SECTION_NUMBER));
			} catch (Exception e) {

				e.printStackTrace();

				return null;
			}
		}

		@Override
		public void onPause() {
			super.onPause();
			onStop();
			onDestroyView();
		}
	}

}
