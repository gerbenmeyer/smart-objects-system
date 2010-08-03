package util.enums;

public enum AgentStatus {
	UNKNOWN(0), OK(-1), WARNING(-2), ERROR(-3);
	
	private int value;
	private AgentStatus(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
	
	public static AgentStatus min(AgentStatus s1, AgentStatus s2){
		if (s1.getValue() < s2.getValue()){
			return s1;
		} else {
			return s2;
		}
	}
	
	public static AgentStatus max(AgentStatus s1, AgentStatus s2){
		if (s1.getValue() > s2.getValue()){
			return s1;
		} else {
			return s2;
		}
	}
}
