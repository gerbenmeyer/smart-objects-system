package model.agent.property.properties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import model.agent.AgentViewable;
import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * A Property implementation holding a time.
 * 
 * @author Gerben G. Meyer
 */
public class TimeProperty extends Property {

	private GregorianCalendar dateTime = new GregorianCalendar();

	/**
	 * Time pattern 1.
	 */
	public final static String formatPattern1 = "dd-MM-yy HH:mm";
	/**
	 * Time pattern 2.
	 */
	public final static String formatPattern2 = "MM/dd/yy HH:mm";

	/**
	 * Constructs a named TimeProperty.
	 * 
	 * @param name
	 *            the name
	 */
	public TimeProperty(String name) {
		super(name, PropertyType.TIME);
	}

	/**
	 * Constructs a named TimeProperty with a value.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the value in the format of {@link #formatPattern1} or
	 *            {@link #formatPattern2}
	 */
	public TimeProperty(String name, String value) {
		this(name);
		parseString(value);
	}

	/**
	 * Constructs a named TimeProperty with a value using a GregorianCalendar.
	 * 
	 * @param name
	 *            the name
	 * @param value
	 *            the calendar
	 */
	public TimeProperty(String name, GregorianCalendar value) {
		this(name);
		setDateTime(value);
	}

	/**
	 * Constructs an unnamed TimeProperty using the time of construction as
	 * time.
	 */
	public TimeProperty() {
		this("");
		dateTime = new GregorianCalendar();
	}

	/**
	 * Gets the time of this TimeProperty.
	 * 
	 * @return the calendar
	 */
	public GregorianCalendar getDateTime() {
		return dateTime;
	}

	/**
	 * Sets the time of this TimeProperty using a GregorianCalendar.
	 * 
	 * @param dateTime
	 */
	public void setDateTime(GregorianCalendar dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Sets the time of this TimeProperty using values for each field.
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @param dayOfMonth
	 *            the day of the month
	 * @param hourOfDay
	 *            the hour
	 * @param minute
	 *            the minute
	 */
	public void setDateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
		this.dateTime = new GregorianCalendar(year, month - 1, dayOfMonth, hourOfDay, minute);
	}

	/**
	 * Returns a formatted string with the current time.
	 * 
	 * @return the time
	 */
	public static String nowString() {
		return new SimpleDateFormat(formatPattern1).format(new GregorianCalendar().getTime());
	}

	@Override
	public String toString() {
		return new SimpleDateFormat(formatPattern1).format(this.dateTime.getTime());
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
				// e.printStackTrace();
			}
		}
	}

	public static String parseHint() {
		return formatPattern1;
	}

	public String getIcon() {
		return "datetime.png";
	}

	@Override
	public String getArffAttributeDeclaration() {
		return "@ATTRIBUTE " + getName() + " NUMERIC";
	}

	@Override
	public String getArffData(AgentViewable av) {
		long date = this.dateTime.getTimeInMillis();
		long now = new GregorianCalendar().getTimeInMillis();
		double diffHours = (date - now) / (60.0 * 60.0 * 1000.0);
		return Double.toString(diffHours);
	}

	@Override
	public String toInformativeString() {
		return toString();

		// long date = this.dateTime.getTimeInMillis();
		// long now = new GregorianCalendar().getTimeInMillis();
		// long diffMinutes = (date - now) / (60 * 1000);
		//
		// long diffHours = diffMinutes / 60;
		// long diffDays = diffHours / 24;
		//
		// String text = "";
		// if (diffMinutes == 0) {
		// text = "now";
		// } else {
		// if (Math.abs(diffDays) > 1) {
		// text = Math.abs(diffDays) + "d";
		// } else if (Math.abs(diffHours) > 1) {
		// text = Math.abs(diffHours) + "h";
		// } else {
		// text = Math.abs(diffMinutes) + "m";
		// }
		//
		// }
		//
		// if (diffMinutes > 0) {
		// text += " ahead";
		// }
		//
		// if (diffMinutes < 0) {
		// text += " ago";
		// }
		//
		// return text;
	}
}