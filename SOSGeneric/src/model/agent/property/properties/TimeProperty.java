package model.agent.property.properties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * 
 * @author Gerben G. Meyer
 * 
 */
public class TimeProperty extends Property {

	private GregorianCalendar dateTime = new GregorianCalendar();

	public final static String formatPattern1 = "dd-MM-yy HH:mm";
	public final static String formatPattern2 = "MM/dd/yy HH:mm";

	public TimeProperty(String name) {
		super(name, PropertyType.TIME);
	}

	public TimeProperty(String name, String value) {
		this(name);
		parseString(value);
	}
	
	public TimeProperty(String name, GregorianCalendar value){
		this(name);
		setDateTime(value);
	}

	public TimeProperty() {
		this("");
		dateTime = new GregorianCalendar();
	}

	/**
	 * @return the date
	 */
	public GregorianCalendar getDateTime() {
		return dateTime;
	}

	/**
	 * 
	 * @param dateTime
	 */
	public void setDateTime(GregorianCalendar dateTime) {
		this.dateTime = dateTime;
		mutateHistory();
	}

	/**
	 * @param dateTime
	 *            the date to set
	 */
	public void setDateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		this.dateTime = new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute);
		mutateHistory();
	}

	@Override
	/*
	 * *
	 */
	public String toString() {
		return new SimpleDateFormat(formatPattern1).format(this.dateTime.getTime());
	}
	
	public static String nowString(){
		return new SimpleDateFormat(formatPattern1).format(new GregorianCalendar().getTime());
	}

	@Override
	public void parseString(String str) {
		this.dateTime = new GregorianCalendar();
		try {
			this.dateTime.setTime(new SimpleDateFormat(formatPattern1).parse(str));
		} catch (ParseException pe) {
			try {
				this.dateTime.setTime(new SimpleDateFormat(formatPattern2).parse(str));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		mutateHistory();
	}

	public static String parseHint() {
		return formatPattern1;
	}

	public String getIcon(){
		return "datetime.png";
	}

	@Override
	public String arffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String arffData() {
		long date = this.dateTime.getTimeInMillis();
		long now = new GregorianCalendar().getTimeInMillis();
		double diffHours = (date - now) / (60.0 * 60.0 * 1000.0);
		return Double.toString(diffHours);
	}

	@Override
	public String toInformativeString() {
		return toString();
		
//		long date = this.dateTime.getTimeInMillis();
//		long now = new GregorianCalendar().getTimeInMillis();
//		long diffMinutes = (date - now) / (60 * 1000);
//
//		long diffHours = diffMinutes / 60;
//		long diffDays = diffHours / 24;
//
//		String text = "";
//		if (diffMinutes == 0) {
//			text = "now";
//		} else {
//			if (Math.abs(diffDays) > 1) {
//				text = Math.abs(diffDays) + "d";
//			} else if (Math.abs(diffHours) > 1) {
//				text = Math.abs(diffHours) + "h";
//			} else {
//				text = Math.abs(diffMinutes) + "m";
//			}
//
//		}
//
//		if (diffMinutes > 0) {
//			text += " ahead";
//		}
//
//		if (diffMinutes < 0) {
//			text += " ago";
//		}
//
//		return text;
	}

}
