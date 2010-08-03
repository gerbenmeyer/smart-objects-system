package model.agent.utility;

import java.io.StringReader;

import main.Settings;
import util.enums.AgentStatus;
import util.xmltool.XMLTool;
import weka.classifiers.trees.LMT;
import weka.core.Instance;
import weka.core.Instances;

public class Relation {

	private String name;

	private String dataLocation;
	private String attributesLocation;
	private String arffLocation;
	private String modelLocation;

	LMT decisionAlgorithm;

	private int numInstances = 0;

	boolean hasNewInstance = true;
	
	private static double requiredConfidenceFactor = 4.0;

	public Relation(String name, String attributes) {
		this.name = name;
		attributes += "\n@ATTRIBUTE Status {" + AgentStatus.UNKNOWN.toString() + ","
				+ AgentStatus.OK.toString() + "," + AgentStatus.WARNING.toString() + ","
				+ AgentStatus.ERROR.toString() + "}";

		String hash = Integer.toString(attributes.hashCode());

		String path = Settings.getProperty(Settings.AGENTS_DATA_DIR);
		this.dataLocation = path + name.toLowerCase() + "_" + hash + ".data";
		this.attributesLocation = path + name.toLowerCase() + "_" + hash + ".attr";
		this.arffLocation = path + name.toLowerCase() + "_" + hash + ".arff";
		this.modelLocation = path + name.toLowerCase() + "_" + hash + ".txt";

		XMLTool.xmlToFile(attributesLocation, attributes);
		numInstances = XMLTool.xmlFromFile(dataLocation).split("\n").length;
	}

	public void addInstance(String newInstance, AgentStatus status) {
		synchronized (dataLocation) {
			String current = XMLTool.xmlFromFile(dataLocation, 199);
			newInstance += "," + status.toString();
			if (!current.isEmpty()) {
				newInstance += "\n";
			}
			current = newInstance + current;
			XMLTool.xmlToFile(dataLocation, current);
			XMLTool.xmlToFile(arffLocation, toArff(true));
			hasNewInstance = true;
			numInstances++;
		}
	}

	/**
	 * 
	 * @param attributes
	 * @return
	 */
	public AgentStatus getStatus(String instanceString) {
		AgentStatus result = AgentStatus.UNKNOWN;

		if (numInstances <= 10) {
			return result;
		}

		synchronized (dataLocation) {

			try {

				Instances blaat = new Instances(new StringReader(this.toArff(false) + "\n" + instanceString + ","
						+ result.toString()));
				blaat.setClassIndex(blaat.numAttributes() - 1);

				Instance instance = blaat.instance(0);

				if (hasNewInstance) {

					decisionAlgorithm = new LMT();
//					decisionAlgorithm.setHiddenLayers("o");
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

				for ( int i = 0 ; i < distribution.length ; i ++ ) {
					if (distribution[i] > bestPolicyValue) {
						bestPolicyValue = distribution[i];
					}
				}

				for ( int i = 0 ; i < distribution.length ; i ++ ) {
					if (distribution[i] > secondBestPolicyValue && distribution[i] < bestPolicyValue) {
						secondBestPolicyValue = distribution[i];
					}
				}

				double confidenceFactor = bestPolicyValue / secondBestPolicyValue;
				
				if (confidenceFactor >= requiredConfidenceFactor){
					result = AgentStatus.valueOf(blaat.attribute(blaat.numAttributes() - 1).value(
						classifyResult));
				} 

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public String toArff(boolean includeData) {
		String result = "";
		synchronized (dataLocation) {

			result += "@RELATION " + name + "\n";
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
