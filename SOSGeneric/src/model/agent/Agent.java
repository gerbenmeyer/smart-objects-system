package model.agent;

import java.util.HashMap;

import main.Settings;
import model.agent.classification.Classifier;
import model.agent.classification.ClassifierCollection;
import model.agent.collection.AgentCollectionView;
import model.agent.property.PropertiesObject;
import util.enums.AgentStatus;
import util.htmltool.HtmlDetailsPaneContentGenerator;
import util.htmltool.HtmlMapContentGenerator;
import util.htmltool.HtmlTool;

/**
 * This abstract class implements the AgentView interface,
 * and must be extended by every agent implementation.
 *  
 * @author Gijs B. Roest
 */
public abstract class Agent extends PropertiesObject implements
		AgentView {

	private AgentCollectionView agentCollectionView;

	/**
	 * Constructs a new Agent object.
	 * 
	 * @param id the identifier for the agent
	 * @param agentCollectionView the collectionView for (read) access to other agents
	 */
	public Agent(String id, AgentCollectionView agentCollectionView) {
		super(id);
		this.agentCollectionView = agentCollectionView;
	}

	/**
	 * Executes the agent. For external use.
	 * 
	 * @throws InterruptedException
	 */
	final public void execute() throws InterruptedException {
		try {
			act();
		} catch (InterruptedException ie) {
			throw ie;
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * Returns the view this agent has of all other agents.
	 * 
	 * @return the view
	 */
	public AgentCollectionView getAgentCollectionView() {
		return agentCollectionView;
	}

	/**
	 * Executes the agent. For internal use only.
	 * 
	 * @throws Exception
	 */
	protected abstract void act() throws Exception;

	/**
	 * Learn the agent a new status.
	 * 
	 * @param status the status to be teached
	 */
	public void teachStatus(AgentStatus status) {
		try {
			Classifier r = ClassifierCollection.getInstance().getRelation(
					this.getType(), getArffAttributesString());
			r.addInstance(getArffInstanceString(), status);
		} catch (Exception e) {
		}
	}

	/**
	 * Get the current learned status.
	 * 
	 * @return the status
	 */
	public AgentStatus getLearnedStatus() {
		AgentStatus status = AgentStatus.UNKNOWN;
		if (Settings.getProperty(Settings.AGENT_PROBLEM_DETECTION_ENABLED).equals(Boolean.toString(true))) {
			try {
				Classifier r = ClassifierCollection.getInstance().getRelation(
						this.getType(), getArffAttributesString());
				status = r.getStatus(getArffInstanceString());
			} catch (Exception e) {
			}
		}
		return status;
	}

	/**
	 * This method is executed just before the agent is removed from its collection.
	 */
	public abstract void lastWish();

	/**
	 * True if this agent is garbage and may be disposed.
	 * 
	 * @return a boolean
	 */
	public abstract boolean isGarbage();

	public String getIcon() {
		return getType().toLowerCase() + "_icon.png";
	}

	public String getMapMarkerImage() {
		return getType().toLowerCase() + ".png";
	}

	public abstract void generateMapContent(HtmlMapContentGenerator mapContent,
			HashMap<String, String> params);

	public abstract void generateDetailsPaneContent(
			HtmlDetailsPaneContentGenerator detailsPane,
			HashMap<String, String> params);

	/**
	 * This function generates the code required for training agents as part of
	 * the mapContent. Has to be used in combination with
	 * generateDetailsPaneTrainingCode
	 * 
	 * @param mapContent
	 * @param params
	 */
	public void generateMapContentTrainingCode(
			HtmlMapContentGenerator mapContent, HashMap<String, String> params) {
		if (!Settings.getProperty(Settings.AGENT_PROBLEM_LEARNING_ENABLED)
				.equals(Boolean.toString(true))) {
			return;
		}
		for (String key : params.keySet()) {
			if (key.equals("learnstatus")) {
				try {
					AgentStatus newStatus = AgentStatus
							.valueOf(params.get(key));
					teachStatus(newStatus);
					mapContent.addCustomScript("alert('New status learned!');");
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * This function generates the code required for training agents as part of
	 * the detailsPane. Has to be used in combination with
	 * generateMapContentTrainingCode
	 * 
	 * @param detailsPane
	 * @param params
	 */
	public void generateDetailsPaneTrainingCode(
			HtmlDetailsPaneContentGenerator detailsPane,
			HashMap<String, String> params) {
		if (!Settings.getProperty(Settings.AGENT_PROBLEM_LEARNING_ENABLED)
				.equals(Boolean.toString(true))) {
			return;
		}

		detailsPane.addSubHeader("Training");
		detailsPane.addDataHeader("", "Training", "Status");
		String url = getID() + ".html?learnstatus=";
		String trainingCode = "";
		trainingCode += HtmlTool.createLink(url + AgentStatus.OK.toString(),
				HtmlTool.createImage("ok.png", "ok", 16), "hidden_frame");
		trainingCode += " ";
		trainingCode += HtmlTool.createLink(url
				+ AgentStatus.WARNING.toString(), HtmlTool.createImage(
				"warning.png", "warning", 16), "hidden_frame");
		trainingCode += " ";
		trainingCode += HtmlTool.createLink(url + AgentStatus.ERROR.toString(),
				HtmlTool.createImage("error.png", "error", 16), "hidden_frame");
		detailsPane.addDataRow("info.png", "Provide status", trainingCode, "");
	}
}