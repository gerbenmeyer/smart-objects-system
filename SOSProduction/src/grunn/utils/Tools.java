package grunn.utils;

public abstract class Tools {

	public static String FormatNumber(int value){
		return FormatNumber(value, 4);
	}
	
	public static String FormatNumber(int value, int length){
		String result = ""+Math.round(value);
		while (result.length() < length){
			result = " "+result;
		}
		return result;
	}
	
	public static String FormatCurrency(int value){
		String result = "$ "+Math.round(value);
		while (result.length() < 6){
			result = " "+result;
		}
		return result;
	}
	
}
