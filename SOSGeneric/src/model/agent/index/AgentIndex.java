package model.agent.index;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import main.Settings;
import model.agent.AgentView;
import model.agent.collection.AgentCollectionView;
import util.comparators.AgentStatusComparator;
import util.enums.PropertyType;

public class AgentIndex implements AgentIndexView {

	private Vector<String> agentIDs = new Vector<String>();
	private Vector<String> agentTypes = new Vector<String>();

	private Map<String, Vector<String>> keywordMatrix = Collections
			.synchronizedMap(new HashMap<String, Vector<String>>());

	private final static String whiteSpaceRegex = "[\\.\\,\\!\\-\\(\\)\\/\\\\]";
	private final static String urlRegex = "http:\\/\\/[^\\s\\,]+[^(\\.,!?:;)^\\s]";
	private final static String htmlTagsRegex = "<(.|\n)*?>";

	private Vector<String> blackListStartsWith = new Vector<String>();
	private Vector<String> blackListEquals = new Vector<String>();
	private Vector<String> blackListContains = new Vector<String>();
	
	private AgentCollectionView acv;

	/**
	 * Constructs a new AgentIndex
	 */
	public AgentIndex(AgentCollectionView acv) {
		super();
		this.acv = acv;
		
		blackListStartsWith.add("http:");
		blackListStartsWith.add("https:");

		blackListEquals.add("the");
		blackListEquals.add("and");

		blackListContains.add("0");
		blackListContains.add("1");
		blackListContains.add("2");
		blackListContains.add("3");
		blackListContains.add("4");
		blackListContains.add("5");
		blackListContains.add("6");
		blackListContains.add("7");
		blackListContains.add("8");
		blackListContains.add("9");

	}

	public Vector<String> getAgentIDs() {
		return agentIDs;
	}

	public Vector<String> getAgentTypes() {
		return agentTypes;
	}

	// public String getSearchStringAgentType(String type) {
	// return "type:" + type.toLowerCase();
	// }
	//
	// public String getSearchStringAgentStatus(AgentStatus status) {
	// return "status:" + status.toString().toLowerCase();
	// }

	public Set<String> getKeywords() {
		return keywordMatrix.keySet();
	}

	public Vector<String> searchAgents(String search) {
		search = search.replaceAll(whiteSpaceRegex, " ");
		String[] keywordsSplit = search.split(" ");
		return getAgentsWithKeywords(keywordsSplit);
	}

	/**
	 * Search for agents which contains a list of keywords
	 * 
	 * @param keywords the keywords to be matched
	 * @return a Vector with the identifiers of the agents that match the keywords
	 */
	private Vector<String> getAgentsWithKeywords(String[] keywords) {
		if (keywords.length <= 0) {
			return new Vector<String>();
		}
		Vector<String> result = new Vector<String>();
		Vector<String> subResult = keywordMatrix.get(keywords[0].trim().toLowerCase());
		if (subResult == null) {
			return new Vector<String>();
		}
		result.addAll(subResult);

		if (result == null) {
			return new Vector<String>();
		}
		for (int i = 1; i < keywords.length; i++) {
			subResult = keywordMatrix.get(keywords[i].trim().toLowerCase());
			if (subResult == null) {
				return new Vector<String>();
			}
			result.retainAll(subResult);
		}
		Collections.sort(result, new AgentStatusComparator(acv));
		
		while (result.size() > 10001){
			result.remove(result.size()-1);
		}
		
		return result;
	}

	/**
	 * Adds an agent to the AgentIndex and processes it.
	 * 
	 * @param agent the agent to be added
	 */
	public void add(AgentView agent) {
		String agentID = agent.getID();
		if (agentID.isEmpty()) {
			return;
		}
		if (!agentIDs.contains(agentID)) {
			agentIDs.add(agentID);
		}
		if (agent.isHidden()) {
			return;
		}

		String agentType = agent.getType().trim().toLowerCase();
		String agentStatus = agent.getStatus().toString().trim().toLowerCase();

		String searchType = "type:" + agentType;
		String searchStatus = "status:" + agentStatus;

		if (Settings.getProperty(Settings.SHOW_ALL_OBJECTS).equals(Boolean.toString(true))) {
			// add type to vector
			if (!agentType.isEmpty()) {
				if (!agentTypes.contains("all")) {
					agentTypes.add("all");
				}
			}
			String allType = "type:all";
			// add ID by type to matrix.
			if (!keywordMatrix.containsKey(allType)) {
				keywordMatrix.put(allType, new Vector<String>());
			}
			if (!keywordMatrix.get(allType).contains(agentID)) {
				keywordMatrix.get(allType).add(agentID);
			}
		}

		// add type to vector
		if (!agent.getType().isEmpty()) {
			if (!agentTypes.contains(agentType)) {
				agentTypes.add(agentType);
			}
		}
		// add ID by type to matrix.
		if (!keywordMatrix.containsKey(searchType)) {
			keywordMatrix.put(searchType, new Vector<String>());
		}
		if (!keywordMatrix.get(searchType).contains(agentID)) {
			keywordMatrix.get(searchType).add(agentID);
		}

		// add ID by state to matrix.
		if (!keywordMatrix.containsKey(searchStatus)) {
			keywordMatrix.put(searchStatus, new Vector<String>());
		}
		if (!keywordMatrix.get(searchStatus).contains(agentID)) {
			keywordMatrix.get(searchStatus).add(agentID);
		}

		// add ID by keyword to matrix.
		for (String propKey : agent.getPropertiesKeySet()) {
			if (agent.getPropertyType(propKey) != PropertyType.TEXT) {
				continue;
			}
			if (propKey.equals("ID") || propKey.equals("Type") || propKey.equals("DisplayCode")) {
				continue;
			}

			String search = agent.getPropertyValue(propKey);
			search = search.replaceAll(htmlTagsRegex, " ");
			search = search.replaceAll(urlRegex, " ");
			search = search.replaceAll(whiteSpaceRegex, " ");

			String[] words = search.split(" ");
			for (String word : words) {

				String smallWord = word.toLowerCase().trim();
				if (smallWord.startsWith("type:") || smallWord.startsWith("status:") || smallWord.length() <= 2) {
					continue;
				}

				boolean valid = true;
				for (String black : blackListStartsWith) {
					if (smallWord.startsWith(black)) {
						valid = false;
					}
				}
				for (String black : blackListContains) {
					if (smallWord.contains(black)) {
						valid = false;
					}
				}
				if (blackListEquals.contains(smallWord)) {
					valid = false;
				}
				if (!valid) {
					continue;
				}
				if (!keywordMatrix.containsKey(smallWord)) {
					keywordMatrix.put(smallWord, new Vector<String>());

				}
				if (!keywordMatrix.get(smallWord).contains(agentID)) {
					keywordMatrix.get(smallWord).add(agentID);
				}
			}
		}

		// System.out.println("AgentIDs: " + agentIDs.size() + "; AgentTypes: "
		// + agentTypes.size() + "; Keywords: "
		// + keywordMatrix.size());
	}

	/**
	 * Removes an agent form the AgentIndex.
	 * Also removes all of its keywords.
	 * 
	 * @param agent the agent to be removed
	 */
	public void remove(AgentView agent) {
		String agentID = agent.getID();
		if (agentID.isEmpty()) {
			return;
		}
		while (agentIDs.contains(agentID)) {
			agentIDs.remove(agentID);
		}
		Vector<String> keywordsToRemove = new Vector<String>();
		Vector<String> keywords = new Vector<String>(keywordMatrix.keySet());
		for (String keyword : keywords) {
			Vector<String> keywordIndex = keywordMatrix.get(keyword);
			if (keywordIndex == null) {
				continue;
			}
			while (keywordIndex.contains(agentID)) {
				keywordIndex.remove(agentID);
			}
			if (keywordIndex.isEmpty()) {
				keywordsToRemove.add(keyword);
			}
		}
		for (String keyword : keywordsToRemove) {
			keywordMatrix.remove(keyword);
		}
		// System.out.println("AgentIDs: " + agentIDs.size() + "; AgentTypes: "
		// + agentTypes.size() + "; Keywords: "
		// + keywordMatrix.size());
	}

	/**
	 * Updates an agent's keywords
	 * 
	 * @param agent the agent to be updated 
	 */
	public void update(AgentView agent) {
		remove(agent);
		add(agent);
	}
}