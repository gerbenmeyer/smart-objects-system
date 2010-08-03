package util;

public class Capitalize {
	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String capitalizeLine(String line) {
		String[] words = line.split("\\s");
		String result = "";
		for (String s : words) {
			result += capitalize(s) + " ";
		}
		return result;
	}
}
