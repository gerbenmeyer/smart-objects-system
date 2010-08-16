package model.agent.classification;

import java.io.StringReader;

import main.Settings;
import util.enums.AgentStatus;
import util.xmltool.XMLTool;
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

	private String dataLocation;
	private String attributesLocation;
	private String arffLocation;
	private String modelLocation;

	private LMT decisionAlgorithm;

	private int numInstances = 0;

	private boolean hasNewInstance = true;

	private static double requiredConfidenceFactor = 4.0;

	/**
	 * Creates a new classifier.
	 * 
	 * @param agentType
	 *            the agent type which will use this classifier
	 * @param arffAttributes
	 *            ARFF representation of the attributes of the agents using this
	 *            classifier
	 */
	public Classifier(String agentType, String arffAttributes) {
		this.agentType = agentType;
		arffAttributes += "\n@ATTRIBUTE Status {" + AgentStatus.UNKNOWN.toString() + "," + AgentStatus.OK.toString()
				+ "," + AgentStatus.WARNING.toString() + "," + AgentStatus.ERROR.toString() + "}";

		String hash = Integer.toString(arffAttributes.hashCode());

		String path = Settings.getProperty(Settings.AGENTS_DATA_DIR);
		this.dataLocation = path + agentType.toLowerCase() + "_" + hash + ".data";
		this.attributesLocation = path + agentType.toLowerCase() + "_" + hash + ".attr";
		this.arffLocation = path + agentType.toLowerCase() + "_" + hash + ".arff";
		this.modelLocation = path + agentType.toLowerCase() + "_" + hash + ".txt";

		XMLTool.xmlToFile(attributesLocation, arffAttributes);
		numInstances = XMLTool.xmlFromFile(dataLocation).split("\n").length;
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
	public void addInstance(String arffInstance, AgentStatus status) {
		synchronized (dataLocation) {
			String current = XMLTool.xmlFromFile(dataLocation, 199);
			arffInstance += "," + status.toString();
			if (!current.isEmpty()) {
				arffInstance += "\n";
			}
			current = arffInstance + current;
			XMLTool.xmlToFile(dataLocation, current);
			XMLTool.xmlToFile(arffLocation, toArff(true));
			hasNewInstance = true;
			numInstances++;
		}
	}

	/**
	 * Returns the status most appropriate status based on the provided
	 * instance.
	 * 
	 * @param arffInstance
	 *            ARFF representation of the to be classified instance
	 * @return The most appropriate status
	 */
	public AgentStatus getStatus(String arffInstance) {
		AgentStatus result = AgentStatus.UNKNOWN;

		if (numInstances <= 10) {
			return result;
		}

		synchronized (dataLocation) {

			try {

				Instances blaat = new Instances(new StringReader(this.toArff(false) + "\n" + arffInstance + ","
						+ result.toString()));
				blaat.setClassIndex(blaat.numAttributes() - 1);

				Instance instance = blaat.instance(0);

				if (hasNewInstance) {

					decisionAlgorithm = new LMT();
					// decisionAlgorithm.setHiddenLayers("o");
					Instances instances = new Instances(new StringReader(this.toArff(true)));
					instances.setClassIndex(instances.numAttributes() - 1);
					decisionAlgorithm.buildClassifier(instances);
					XMLTool.xmlToFile(modelLocation, decisionAlgorithm.toString());

					hasNewInstance = false;

				}

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
		String result = "";
		synchronized (dataLocation) {

			result += "@RELATION " + agentType + "\n";
			result += "\n";

			result += XMLTool.xmlFromFile(attributesLocation) + "\n";

			result += "\n";
			result += "@DATA\n";
			if (includeData) {
				result += XMLTool.xmlFromFile(dataLocation);
			}

		}
		return result;
	}

}
