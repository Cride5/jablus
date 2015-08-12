package uk.co.crider.jablus.env;

import java.util.Date;
import java.util.Hashtable;

import uk.co.crider.jablus.Constants;
import uk.co.crider.jablus.data.IntegerData;

/** Represents time steps. Time here is abstract and could represent a block, year, decade etc. */
public class Time extends IntegerData implements DriverDynamic {
	
	public static final int DAY   = 0;
	public static final int WEEK  = 1;
	public static final int MONTH = 2;
	public static final int YEAR  = 3;
	
	public static final Hashtable<java.lang.Integer, String> TIME_UNITS = new Hashtable<java.lang.Integer, String>();
	static{
		TIME_UNITS.put(DAY,   Constants.UNITS_DAY);
		TIME_UNITS.put(WEEK,  Constants.UNITS_WEEK);
		TIME_UNITS.put(MONTH, Constants.UNITS_MONTH);
		TIME_UNITS.put(YEAR,  Constants.UNITS_YEAR);
	}
		
	public static final double DAYS_YEAR = 365.25;
	public static final int DAYS_WEEK = 7;
	public static final int WEEK_OFFSET = 4;
	public static final int MONTHS_YEAR = 12;
	public static final int WEEKS_YEAR = 52;
	public static final int[] MONTH_DAYS = new int[]{
		31, // Jan
		28, // Feb
		31, // Mar
		30, // Apr
		31, // May
		30, // Jun
		31, // Jul
		31, // Aug
		30, // Sep
		31, // Oct
		30, // Nov
		31, // Dec
	};
	public static final String[] MONTH_NAMES = new String[]{
		"Jan",
		"Feb",
		"Mar",
		"Apr",
		"May",
		"Jun",
		"Jul",
		"Aug",
		"Sep",
		"Oct",
		"Nov",
		"Dec"
	};
	public static final String[] DAY_NAMES = new String[]{
		"Mon",
		"Tue",
		"Wed",
		"Thu",
		"Fri",
		"Sat",
		"Sun"
	};
	
	private int timeStep;
	
	public Time(){ this(DAY); }
	public Time(int timeStep){ this(timeStep, 0, 0, 0); }
	public Time(int timeStep, int day, int month, int year){
		super(Constants.TEMPORAL_TIME, "day");
		this.timeStep = timeStep;
		setDate(day, month, year);
		// If using weekly time step then adjust date forwards to last sunday of week
		if(timeStep == WEEK)
			addTime(DAY, (DAYS_WEEK - getDayOfWeek() - 1));
	}
	 
	/** Returns the time according to the set timestep,
	 * for example if timestep is YEARS it will return
	 * the year eg 2009 */
	public int getTime(){
		switch(timeStep){
		case DAY : return getDay();
		case WEEK : return getWeek();
		case MONTH : return getMonth();
		case YEAR : return getYear();
		}
		return intValue();
	}
	
	/** Sets the current time using a date */
	public void setDate(int day, int month, int year){
		setValue(0);
		addTime(YEAR, year);
		addTime(MONTH, month);
		addTime(DAY, day);
	}
	/** Sets time's value according to the Time's timestep */
	public void setTime(int time){ setTime(timeStep, time); }
	/** Sets time's value according to the supplied timestep */
	public void setTime(int timeStep, int time){
		setValue(0);
		addTime(timeStep, time);
	}
	/** Adds the given amount of time onto the current time */
	public void addTime(int time){ addTime(timeStep, time); }
	/** Adds the given amount of time onto the current time.
	 * Time added is measured according to the supplied timestep */
	public void addTime(int timeStep, int time){
		switch(timeStep){
		case DAY : setValue(intValue() + time); break;
		case WEEK : setValue(intValue() + time * 7); break;
		case MONTH :
			while(time > 0) setValue(intValue() + MONTH_DAYS[--time]);
			break;
		case YEAR :
			while(time-- > 0) setValue(intValue() + (isLeapYear() ? 366 : 365));
			break;
		}
	}
	
	/** Returns the current day */
	public int getDay(){
		return intValue();
	}
	
	/** Returns the current week */
	public int getWeek(){
		return intValue() / 7;
	}
	
	/** Returns the current block (counting from 0) */
	public int getMonth(){
		return getYear() * MONTH_DAYS.length + getMonthOfYear();
	}
	
	/** Returns the current year */
	public int getYear(){
		return (int)(intValue() / DAYS_YEAR);
	}
	
	/** Returns whether the current year is a leap year */
	public boolean isLeapYear(){
		return getYear() % 4 == 0;
	}
	
	/** Returns the current day of the year (counting from 0) */
	public int getDayOfYear(){
		return  intValue() - (int)Math.ceil(getYear() * DAYS_YEAR);
	}
	
	/** Returns the current day of the block (counting from 0) */
	public int getDayOfMonth(){
		int doy = getDayOfYear();
		int doy_ = doy;
		boolean leap = isLeapYear();
		for(int i = 0; i < MONTH_DAYS.length; i++){
			doy -= MONTH_DAYS[i];
			if(i == 1 && leap) doy--; // 29 days in feb 
			if(doy < 0) return doy_;
			doy_ = doy;
		}
		return 0;
	}
	
	/** Returns the current day of the week (counting from 0) */
	public int getDayOfWeek(){
		return (intValue() + WEEK_OFFSET) % DAYS_WEEK;
	}
	
	/** Returns the current week of the year (counting from 0) */
	public int getWeekOfYear(){
		return getDayOfYear() / DAYS_WEEK;
	}
	
	/** Returns the week day of the first day of the year */
	public int getFirstWeekDayOfYear(){
		return (int)Math.ceil(getYear() * DAYS_YEAR + WEEK_OFFSET) % DAYS_WEEK;
	}

	/** Returns the number of weeks in this year (most often 52, sometimes 53) */
	public int getWeeksThisYear(){
		// Get date of first sunday of the year, add 52-weeks, if still same year then gud
		int firstSun = 6 - getFirstWeekDayOfYear();
		int lastSun = firstSun + WEEKS_YEAR * DAYS_WEEK;
		return lastSun < getDaysThisYear() ? WEEKS_YEAR + 1 : WEEKS_YEAR;
	}
	
	/** Returns the block of the year (counting from 0) */
	public int getMonthOfYear(){
		int doy = getDayOfYear();
		boolean leap = isLeapYear();
		for(int i = 0; i < MONTH_DAYS.length; i++){
			doy -= MONTH_DAYS[i];
			if(i == 1 && leap) doy--; // 29 days in feb 
			if(doy < 0) return i;
		}
		return 0;
	}
	
	/** Returns the number of days this block */
	public int getDaysThisMonth(){
		int m = getMonthOfYear();
		return m == 1 && isLeapYear() ? MONTH_DAYS[m] + 1 : MONTH_DAYS[m];
	}
	
	/** Returns the number of days this year */
	public int getDaysThisYear(){
		return isLeapYear() ? (int)Math.ceil(DAYS_YEAR) : (int)DAYS_YEAR; 
	}
	
	/** Returns the current date as a string */
	public String dateString(){
		int day = getDayOfMonth() + 1;
		return DAY_NAMES[getDayOfWeek()] + (day < 10 ? "  " : " ") + day + " " + MONTH_NAMES[getMonthOfYear()] + " " + getYear();
	}
	
	/** Returns the current data as a Java date object */
	public Date getJavaDate(){
		return new Date((long)((value - 719542.5) * 24.0) * 60l * 60l * 1000l);
	}
	
	/** @inheritDoc */
	public String toString(){
		return dateString();
	}

	// Implementing Dynamic Driver ------------------------

	/** @inheritDoc */
	public void execStep(){
		switch(timeStep){
		case DAY : setValue(intValue() + 1); break;
		case WEEK : setValue(intValue() + 7); break;
		case MONTH : int moy = getMonthOfYear(); setValue(intValue() + MONTH_DAYS[moy] + (isLeapYear() && moy == 1 ? 1 : 0)); break;
		case YEAR : setValue(isLeapYear() ? intValue() + 366 : intValue() + 365); break;
		}
	}

	/** @inheritDoc */
	public void initStep(){
		// Nothing to do here
	}
	
	// Implementing Data ----------------------------------
	
	/** @inheritDoc */
	public Object clone(){ return new Time(id, value, units); }
	/** Constructor for cloning this object */
	public Time(int id, int value, String units){
		super(id, value, units); }
	
	public static void main(String[] args){
		Time t;
		t = new Time(DAY);
		while(t.getYear() <= 1){
			System.out.println("D:" + t.getDay() + "\tDOY:" + t.getDayOfYear() + " \tW:" + t.getWeek() + "\tM:" + t.getMonth() + "\t" + t.dateString());
			t.execStep(); }
		t = new Time(WEEK);
		while(t.getYear() <= 2){
			System.out.println("D:" + t.getDay() + "\tDOY:" + t.getDayOfYear() + " \tW:" + t.getWeek() + "\tM:" + t.getMonth() + "\t" + t.dateString());
			t.execStep(); }
		t = new Time(MONTH);
		while(t.getYear() <= 2){
			System.out.println("D:" + t.getDay() + "\tDOY:" + t.getDayOfYear() + " \tW:" + t.getWeek() + "\tM:" + t.getMonth() + "\t" + t.dateString());
			t.execStep(); }
		t = new Time(YEAR);
		while(t.getYear() <= 5){
			System.out.println("D:" + t.getDay() + "\tDOY:" + t.getDayOfYear() + " \tW:" + t.getWeek() + "\tM:" + t.getMonth() + "\t" + t.dateString());
			t.execStep(); }
		
	}
}
