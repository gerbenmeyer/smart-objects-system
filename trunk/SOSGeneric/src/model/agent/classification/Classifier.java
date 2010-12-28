package model.agent.classification;

import java.io.StringReader;

import util.enums.AgentStatus;
import weka.classifiers.trees.LMT;
import weka.core.Instance;
import weka.core.Instances;

/**
 * 
 * This class is used by agents to classify their current status. Trained
 * instances will be saved into an XML file, and reloaded when the same
 * classifier (i.e. of the same agentType and the same attributes) is
 * instantiated again.
 * 
 * @author Gerben G. Meyer
 * 
 */
public class Classifier {

	private String agentType;
	private LMT decisionAlgorithm;

	private static double requiredConfidenceFactor = 4.0;
	
	private String arffData;
	private String arffAttributes;

	/**
	 * Creates a new classifier.
	 * 
	 * @param agentType
	 *            the agent type which will use this classifier
	 * @param arffAttributes
	 *            ARFF representation of the attributes of the agents using this
	 *            classifier
	 * @param arffData existing data
	 */
	public Classifier(String agentType, String arffAttributes, String arffData) {
		this(agentType, arffAttributes, arffData, new LMT());
	}
	
	/**
	 * Creates a new Classifier with an existing LMT decision algorithm.
	 * 
	 * @param agentType
	 *            the agent type which will use this classifier
	 * @param arffAttributes
	 *            ARFF representation of the attributes of the agents using this
	 *            classifier
	 * @param arffData existing data
	 * @param decisionAlgorithm the decision algorithm
	 */
	public Classifier(String agentType, String arffAttributes, String arffData, LMT decisionAlgorithm) {
		this.agentType = agentType;
		this.arffData = arffData;
		this.arffAttributes = arffAttributes;
		this.decisionAlgorithm = decisionAlgorithm;
	}

	/**
	 * Adds a new instance to the set of training instances used by the
	 * classifier.
	 * 
	 * @param arffInstance
	 *            ARFF representation of the new instance
	 * @param status
	 *            The trained status
	 */
	public synchronized void addTrainingInstance(String arffInstance, AgentStatus status) {
		this.arffData = arffInstance + "," + status.toString() +"\n"+ this.arffData;

		if (getArffData().replaceAll("[^\\n]", "").length() > 10) {
			try {
				Instances instances = new Instances(new StringReader(this.toArff(true)));
				instances.setClassIndex(instances.numAttributes() - 1);
				decisionAlgorithm.buildClassifier(instances);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ClassifierCollection.getInstance().put(this);
	}

	/**
	 * Returns the status most appropriate status based on the provided
	 * instance.
	 * 
	 * @param arffInstance
	 *            ARFF representation of the to be classified instance
	 * @return The most appropriate status
	 */
	public AgentStatus classifyStatus(String arffInstance) {
		AgentStatus result = AgentStatus.UNKNOWN;

		if (getArffData().replaceAll("[^\\n]", "").length() <= 10) {
			return result;
		}
		try {
			Instances blaat = new Instances(new StringReader(this.toArff(false) + "\n" + arffInstance + ","
					+ result.toString()));
			blaat.setClassIndex(blaat.numAttributes() - 1);

			Instance instance = blaat.instance(0);

			int classifyResult = (int) decisionAlgorithm.classifyInstance(instance);
			double[] distribution = decisionAlgorithm.distributionForInstance(instance);

			double bestPolicyValue = 0.0;
			double secondBestPolicyValue = 0.0;

			for (int i = 0; i < distribution.length; i++) {
				if (distribution[i] > bestPolicyValue) {
					bestPolicyValue = distribution[i];
				}
			}

			for (int i = 0; i < distribution.length; i++) {
				if (distribution[i] > secondBestPolicyValue && distribution[i] < bestPolicyValue) {
					secondBestPolicyValue = distribution[i];
				}
			}

			double confidenceFactor = bestPolicyValue / secondBestPolicyValue;

			if (confidenceFactor >= requiredConfidenceFactor) {
				result = AgentStatus.valueOf(blaat.attribute(blaat.numAttributes() - 1).value(classifyResult));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Returns an ARFF string representing the used training data with all used
	 * attributes, and optionally, with all training data.
	 * 
	 * @param includeData
	 *            Boolean determining whether to include all training data or
	 *            not
	 * @return the ARFF string
	 */
	public String toArff(boolean includeData) {
		String result = "@RELATION " + agentType + "\n"
		+ "\n"
		+ getArffAttributes() + "\n"
		+ "\n"
		+ "@DATA\n";
		if (includeData) {
			result += getArffData();
		}
		return result;
	}
	
	/**
	 * @return the agentType
	 */
	public String getAgentType() {
		return agentType;
	}

	/**
	 * @return the arffData
	 */
	public String getArffData() {
		return arffData;
	}

	/**
	 * @return the arffAttributes
	 */
	public String getArffAttributes() {
		return arffAttributes;
	}
	
	/**
	 * @return the decisionAlgorithm
	 */
	public LMT getDecisionAlgorithm() {
		return decisionAlgorithm;
	}
}