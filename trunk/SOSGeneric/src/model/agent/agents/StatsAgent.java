package model.agent.agents;

import java.util.HashMap;

import model.agent.Agent;
import model.agent.collection.AgentCollection;
import util.enums.PropertyType;
import util.htmltool.HtmlDetailsContentGenerator;

/**
 * Agent for generating stats.
 * 
 * @author Gerben G. Meyer
 * 
 */
public class StatsAgent extends Agent {

	private final static String LAST_EXECUTION_MILLIS = "LastExecutionMillis";
	private final static String EXECUTION_WAIT_TIME = "ExecutionWaitTime";

	/**
	 * Constructs a new StatsAgent object.
	 * 
	 * @param id
	 *            the identifier for the agent
	 */
	public StatsAgent(String id) {
		super(id);
		if (get(Agent.HIDDEN).isEmpty()) {
			set(PropertyType.BOOLEAN, Agent.HIDDEN, Boolean.toString(true));
		}
		if (get(LAST_EXECUTION_MILLIS).isEmpty()) {
			set(PropertyType.NUMBER, LAST_EXECUTION_MILLIS, "" + System.currentTimeMillis());
		}
		if (get(EXECUTION_WAIT_TIME).isEmpty()) {
			set(PropertyType.NUMBER, EXECUTION_WAIT_TIME, "" + 0.0);
		}
	}

	@Override
	public void act() throws Exception {
		double lastExecutionMillis = Double.parseDouble(get(LAST_EXECUTION_MILLIS));
		double executionWaitTime = (System.currentTimeMillis() - lastExecutionMillis) / 1000.0;
		set(PropertyType.NUMBER, EXECUTION_WAIT_TIME, "" + executionWaitTime);
		set(PropertyType.NUMBER, LAST_EXECUTION_MILLIS, "" + System.currentTimeMillis());
	}

	@Override
	public void generateDetailsContent(HtmlDetailsContentGenerator detailsPane, HashMap<String, String> params) {
		detailsPane.addHeader("Statistics");
		detailsPane.addSubHeader("Index");
		detailsPane.addDataHeader("", "Property");
		detailsPane.addDataRow("#", "Agent types: " + AgentCollection.getInstance().getTypes().size(), "");
		detailsPane.addDataRow("#", "Agent IDs: " + AgentCollection.getInstance().getSize(), "");
		detailsPane.addDataRow("", "Act: " + get(EXECUTION_WAIT_TIME) + " s", "");
	}

}