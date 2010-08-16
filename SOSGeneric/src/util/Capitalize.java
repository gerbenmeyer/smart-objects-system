package util;

/**
 * This class provides static methods to capitalize text.
 * 
 * @author Gerben G. Meyer
 */
public class Capitalize {
	
	/**
	 * Captitalizes the first character of a given text.
	 * 
	 * @param s the text
	 * @return the result
	 */
	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	/**
	 * Capitalizes the first character of each word in a text.
	 * 
	 * @param line the text
	 * @return the result
	 */
	public static String capitalizeLine(String line) {
		String[] words = line.split("\\s");
		String result = "";
		for (String s : words) {
			result += capitalize(s) + " ";
		}
		return result;
	}
}