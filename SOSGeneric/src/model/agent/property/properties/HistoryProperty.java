package model.agent.property.properties;

import java.util.Vector;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * This Property stores the history of the Agent it belongs to.
 * When a Property is changed, {@link #mutate(Property)} should be called to store the new version. 
 * 
 * @author W.H. Mook
 */
public class HistoryProperty extends Property {

	private final static String seperator = "-hissep-", uberSeperator = "_HISSEP_";
	
	private Vector<String[]> changes = new Vector<String[]>();

	/**
	 * Creates a new HistoryProperty object with a name.
	 * 
	 * @param name the name
	 */
	public HistoryProperty(String name) {
		super(name, PropertyType.HISTORY);
	}
	
	/**
	 * Stores the mutation of a Property.
	 * 
	 * @param p the changed property 
	 */
	public void mutate(Property p) {
		changes.add(new String[] { p.getPropertyType().toString(), p.getName(), p.toString(), TimeProperty.nowString() });
	}

	/**
	 * @return the changes
	 */
	public Vector<String[]> getChanges() {
		return changes;
	}
	
	/**
	 * @return the last change
	 */
	public String[] getLatestChange() {
		return changes.lastElement();
	}

	@Override
	public String getArffAttributeDeclaration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArffData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseString(String str) {
		String[] entrySplit = str.split(HistoryProperty.uberSeperator);
		for (String entry : entrySplit) {
			changes.add(entry.split(HistoryProperty.seperator));
		}
	}

	@Override
	public String toInformativeString() {
		// TODO make useful
		return "meeep";
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		for (String[] change : changes) {
//			StringBuffer changeValues = new StringBuffer();
			for (String s : change) {
				str.append(s).append(HistoryProperty.seperator);
			}
			str.append(HistoryProperty.uberSeperator);
//			str.append(changeValues.deleteCharAt(changeValues.lastIndexOf(HistoryProperty.seperator))).append(HistoryProperty.uberSeperator);
			
		}
		return str.toString();//str.deleteCharAt(str.lastIndexOf(HistoryProperty.uberSeperator)).toString();
	}
}