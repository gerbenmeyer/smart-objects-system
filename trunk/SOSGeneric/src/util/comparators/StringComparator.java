/**
 * 
 */
package util.comparators;

import java.util.Comparator;

/**
 * @author Gerben G. Meyer
 * 
 *         Comparator to sort WorldObjects, based on their codes
 */
public class StringComparator implements Comparator<String> {

	@Override
	public int compare(String arg0, String arg1) {
		return arg0.compareTo(arg1);
	}
}
