/**
 * 
 */
package util.comparators;

import java.util.Comparator;

import model.agent.AgentView;
import model.agent.collection.AgentCollectionView;
import util.enums.AgentStatus;

/**
 * @author Gerben G. Meyer
 * 
 *         Comparator to sort WorldObjects, based on their codes
 */
public class AgentStatusComparator implements Comparator<String> {

	private AgentCollectionView propertyObjectCollectionView;

	public AgentStatusComparator(AgentCollectionView agentsView) {
		this.propertyObjectCollectionView = agentsView;
	}
	
	@Override
	public int compare(String id0, String id1) {
		AgentView po1 = propertyObjectCollectionView.get(id0);
		AgentView po2 = propertyObjectCollectionView.get(id1);

		int statusValue1 = -1;
		String label1 = "";
		try {
			label1 = po1.getLabel();
			statusValue1 = -po1.getStatus().getValue();
			boolean finished = po1.getPropertyValue("Finished").equals(Boolean.toString(true));
			if (finished && po1.getStatus() == AgentStatus.OK) {
				statusValue1 = AgentStatus.UNKNOWN.getValue();
			}
		} catch (Exception e) {
		}

		int statusValue2 = -1;
		String label2 = "";
		try {
			label2 = po2.getLabel();
			statusValue2 = -po2.getStatus().getValue();
			boolean finished = po2.getPropertyValue("Finished").equals(Boolean.toString(true));
			if (finished && po2.getStatus() == AgentStatus.OK) {
				statusValue2 = AgentStatus.UNKNOWN.getValue();
			}
		} catch (Exception e) {
		}
		
		int diff = 0;

		if (statusValue1 > statusValue2) {
			diff = -1;
		}
		if (statusValue1 < statusValue2) {
			diff = 1;
		}
		
		if (diff == 0){
			return label1.compareTo(label2);
		}
		
		
		return diff;
	}
	
}
