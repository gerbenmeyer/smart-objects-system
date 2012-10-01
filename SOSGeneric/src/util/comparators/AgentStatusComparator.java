/**
 * 
 */
package util.comparators;

import java.util.Comparator;

import model.agent.Agent;
import model.agent.AgentViewable;
import model.agent.collection.AgentCollectionViewable;
import util.enums.AgentStatus;

/**
 * Comparator to sort Agents, based on their statuses.
 * 
 * @author Gerben G. Meyer
 */
public class AgentStatusComparator implements Comparator<String> {

	private AgentCollectionViewable agentCollectionView;

	/**
	 * Constructs a new AgentStatusComparator. An AgentCollectionView is needed to retrieve the agent status.
	 * 
	 * @param agentsView the view
	 */
	public AgentStatusComparator(AgentCollectionViewable agentsView) {
		this.agentCollectionView = agentsView;
	}
	
	@Override
	public int compare(String id0, String id1) {
		AgentViewable av1 = agentCollectionView.get(id0);
		AgentViewable av2 = agentCollectionView.get(id1);

		int statusValue1 = -1;
		String label1 = "";
		try {
			label1 = av1.get(Agent.LABEL);
			AgentStatus status1 = AgentStatus.valueOf(av1.getStatus().toString());
			statusValue1 = -status1.getValue();
			boolean finished = av1.getBool("Finished");
			if (finished && status1 == AgentStatus.OK) {
				statusValue1 = AgentStatus.UNKNOWN.getValue();
			}
		} catch (Exception e) {
		}

		int statusValue2 = -1;
		String label2 = "";
		try {
			label2 = av2.get(Agent.LABEL);
			AgentStatus status2 = AgentStatus.valueOf(av2.getStatus().toString());
			statusValue2 = -status2.getValue();
			boolean finished = av2.getBool("Finished");
			if (finished && status2 == AgentStatus.OK) {
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