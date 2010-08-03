package model.agent.index;

import java.util.Comparator;

/**
 * A comperator for comparing keywords to be used whith an AgentIndex.
 * 
 * @author G.G. Meyer
 */
public class KeywordComparator implements Comparator<String> {

	private AgentIndexView index;
	
	/**
	 * Constructs a new KeywordComparator
	 * 
	 * @param index an AgentIndexView
	 */
	public KeywordComparator(AgentIndexView index){
		this.index = index;
	}
	
	@Override
	public int compare(String o1, String o2) {
		int size1 = index.searchAgents(o1).size();
		int size2 = index.searchAgents(o2).size();
		return size2 - size1;
	}
}