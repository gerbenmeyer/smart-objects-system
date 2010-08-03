package model.agent.property;

import java.util.Vector;

import model.agent.property.properties.TimeProperty;

import util.htmltool.HtmlTool;

/**
 * This class is used to record the history of a Property, with a limit to the number of records.
 * Call the {@link #mutate(Property)} method to record a change.
 * 
 * @author W.H. Mook
 */
public class PropertyHistory {

	/**
	 * The maximum number of records saved in this history.
	 */
	private final int MAXIMUM_HISTORY_RECORDS = 100;
	private Vector<String[]> changes = new Vector<String[]> ();
	private Property property;


	/**
	 * Constructs a new PropertyHistory instance.
	 * 
	 * @param property the property this history belongs to
	 */
	public PropertyHistory(Property property) {
		super ();
		this.property = property;
	}

	/**
	 * Adds a change to the history of the property.
	 */
	public void mutate() {
		if (changes.size () >= MAXIMUM_HISTORY_RECORDS) {
			changes.remove (changes.firstElement ());
		}

		boolean addChange = true;

		if (changes.size () > 0) {
			String prevValue = getLatestChange ()[1];
			if (prevValue.equals (property.toString ())) {
				addChange = false;
			}
		}

		if (addChange) {
			changes.add (new String[] { property.toInformativeString (), property.toString (), TimeProperty.nowString() });
		}
	}

	/**
	 * Gets the latest change of the property.
	 * 
	 * @return the change
	 */
	public String[] getLatestChange() {
		return changes.lastElement ();
	}

	/**
	 * Gets a list with all changes of the property.
	 * 
	 * @return the changes
	 */
	public Vector<String[]> getChanges() {
		return changes;
	}

	/**
	 * Returns the HTML to be used in a view for this history.
	 * 
	 * @return the HTML string
	 */
	public String toHTML() {
		StringBuffer html = new StringBuffer ();
		Vector<String[]> changesClone = new Vector<String[]> (getChanges ());

		int count = 0;

		for (int i = changesClone.size () - 1; i >= 0; i--) {
			String[] change = changesClone.get (i);
			if (count >= 20) {
				html.append ("<div class=\"property\"><div class=\"propertyicon\"></div><div class=\"propertyname\"></div><div class=\"propertyvalue\">And " + (changesClone.size () - count) + " more ...</div></div>\n");
				break;
			}
			String name = change[0];
			String value = change[1];
			String time = change[2];

			if (name.equals ("ID")) {
				continue;
			}
			html.append ("<div class=\"property\"><div class=\"propertyicon\">" + HtmlTool.createImage ("wrench.png", "changed") + "</div><div class=\"propertyname\">" + new TimeProperty ("", time).toInformativeString () + "</div><div class=\"propertyvalue\">" + name + "</div>" + HtmlTool.createImageRight ("info.png", value, 16) + "</div>\n");
			count++;

		}
		return html.toString ();
	}
}