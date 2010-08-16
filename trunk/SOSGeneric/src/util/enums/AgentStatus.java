package util.enums;

/**
 * The possible statuses of an Agent.
 * Also contains some small logic for prioritizing statuses.
 * 
 * @author Gerben G. Meyer
 */
public enum AgentStatus {
	//The statuses and their values.
	UNKNOWN(0), OK(-1), WARNING(-2), ERROR(-3);
	
	private int value;
	
	/**
	 * Constructs an AgentStatus enum with a value.
	 * 
	 * @param value the value
	 */
	private AgentStatus(int value) {
		this.value = value;
	}
	
	/**
	 * Returns the value of this AgentStatus.
	 * 
	 * @return the value
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * Returns the lesser AgentStatus of two AgentStatuses.
	 * 
	 * @param s1 an AgentStatus
	 * @param s2 another AgentStatus
	 * @return the lesser AgentStatus
	 */
	public static AgentStatus min(AgentStatus s1, AgentStatus s2){
		if (s1.getValue() < s2.getValue()){
			return s1;
		} else {
			return s2;
		}
	}

	/**
	 * Returns the greater AgentStatus of two AgentStatuses.
	 * 
	 * @param s1 an AgentStatus
	 * @param s2 another AgentStatus
	 * @return the greater AgentStatus
	 */
	public static AgentStatus max(AgentStatus s1, AgentStatus s2){
		if (s1.getValue() > s2.getValue()){
			return s1;
		} else {
			return s2;
		}
	}
}