/**
 * 
 */
package util.comparators;

import java.util.Comparator;

/**
 * Comparator to sort Agents, based on their type.
 * 
 * @author Gerben G. Meyer
 */
public class AgentTypeComparator implements Comparator<String> {

	@Override
	public int compare(String arg0, String arg1) {
		if (arg0.equals("all")){
			return -1;
		}
		if (arg1.equals("all")){
			return 1;
		}
		return arg0.compareTo(arg1);
	}
}