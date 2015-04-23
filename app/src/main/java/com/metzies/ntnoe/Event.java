package com.metzies.ntnoe;

import java.util.GregorianCalendar;

public class Event {

	private int id;

	private String sdate;
	private String edate;

	private int type;

	private String summary;
	private String location;

	private int week;
	private int day;

	private int year;
	private int month;
	private int dayofmonth;
	private int starthour;
	private int startminute;
	private int endhour;
	private int endminute;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String format(int h) {
		String res;
		if (h <= 9) {
			res = "0" + h;
		} else {
			res = h + "";
		}
		return res;
	}

	public Event(GregorianCalendar sgreg, GregorianCalendar egreg,
			int classtype, String classsummary, String classlocation) {

		String smonth = format(sgreg.get(GregorianCalendar.MONTH) + 1);
		String sday = format(sgreg.get(GregorianCalendar.DAY_OF_MONTH));
		String shour = format(sgreg.get(GregorianCalendar.HOUR_OF_DAY));
		String sminute = format(sgreg.get(GregorianCalendar.MINUTE));

		String emonth = format(egreg.get(GregorianCalendar.MONTH) + 1);
		String eday = format(egreg.get(GregorianCalendar.DAY_OF_MONTH));
		String ehour = format(egreg.get(GregorianCalendar.HOUR_OF_DAY));
		String eminute = format(egreg.get(GregorianCalendar.MINUTE));

		sdate = sgreg.get(GregorianCalendar.YEAR) + "-" + smonth + "-" + sday
				+ " " + shour + ":" + sminute;
		edate = egreg.get(GregorianCalendar.YEAR) + "-" + emonth + "-" + eday
				+ " " + ehour + ":" + eminute;

		type = classtype;
		summary = classsummary;
		location = classlocation;

		week = sgreg.get(GregorianCalendar.WEEK_OF_YEAR);
		day = sgreg.get(GregorianCalendar.DAY_OF_WEEK);

		year = sgreg.get(GregorianCalendar.YEAR);
		month = sgreg.get(GregorianCalendar.MONTH) + 1;
		dayofmonth = sgreg.get(GregorianCalendar.DAY_OF_MONTH);
		starthour = sgreg.get(GregorianCalendar.HOUR_OF_DAY);
		startminute = sgreg.get(GregorianCalendar.MINUTE);
		endhour = egreg.get(GregorianCalendar.HOUR_OF_DAY);
		endminute = egreg.get(GregorianCalendar.MINUTE);
	}

	public Event(String stdate, String endate, String classsummary,
			int classtype, String classlocation, int theweek, int theday) {

		sdate = stdate;
		edate = endate;

		type = classtype;
		summary = classsummary;
		location = classlocation;

		week = theweek;
		day = theday;

		year = Integer.parseInt(stdate.substring(0, 4));
		month = Integer.parseInt(stdate.substring(5, 7));
		dayofmonth = Integer.parseInt(stdate.substring(8, 10));
		starthour = Integer.parseInt(stdate.substring(11, 13));
		startminute = Integer.parseInt(stdate.substring(14, 16));
		endhour = Integer.parseInt(endate.substring(11, 13));
		endminute = Integer.parseInt(endate.substring(14, 16));

	}

	public Event(String stdate, String classsummary, int classtype,
			String classlocation, int theday) {

		sdate = stdate;

		type = classtype;
		summary = classsummary;
		location = classlocation;

		day = theday;
		week = 0;
	}

	public String getSdate() {
		return sdate;
	}

	public void setSdate(GregorianCalendar sgreg) {
		this.sdate = sgreg.get(GregorianCalendar.YEAR) + "-"
				+ (sgreg.get(GregorianCalendar.MONTH) + 1) + "-"
				+ sgreg.get(GregorianCalendar.DAY_OF_MONTH) + " "
				+ sgreg.get(GregorianCalendar.HOUR_OF_DAY) + ":"
				+ sgreg.get(GregorianCalendar.MINUTE);
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(GregorianCalendar egreg) {
		this.edate = egreg.get(GregorianCalendar.YEAR) + "-"
				+ (egreg.get(GregorianCalendar.MONTH) + 1) + "-"
				+ egreg.get(GregorianCalendar.DAY_OF_MONTH) + " "
				+ egreg.get(GregorianCalendar.HOUR_OF_DAY) + ":"
				+ egreg.get(GregorianCalendar.MINUTE);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getWeek() {
		return week;
	}

	public int getDay() {
		return day;
	}

	public GregorianCalendar getSgreg() {
		GregorianCalendar sgreg = new GregorianCalendar();
		sgreg.clear();

		sgreg.set(GregorianCalendar.YEAR, year);
		sgreg.set(GregorianCalendar.MONTH, month-1);
		sgreg.set(GregorianCalendar.DAY_OF_MONTH, dayofmonth);
		sgreg.set(GregorianCalendar.HOUR_OF_DAY, starthour);
		sgreg.set(GregorianCalendar.MINUTE, startminute);

		return sgreg;
	}

	public GregorianCalendar getEgreg() {
		GregorianCalendar egreg = new GregorianCalendar();
		egreg.clear();

		egreg.set(GregorianCalendar.YEAR, year);
		egreg.set(GregorianCalendar.MONTH, month-1);
		egreg.set(GregorianCalendar.DAY_OF_MONTH, dayofmonth);
		egreg.set(GregorianCalendar.HOUR_OF_DAY, endhour);
		egreg.set(GregorianCalendar.MINUTE, endminute);

		return egreg;
	}

}
