package model.agent.utility;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RelationCollection {

	private static RelationCollection relations = null;

	private Map<String, Relation> map;

	private RelationCollection() {
		map = Collections.synchronizedMap(new HashMap<String, Relation>());
	}

	public static RelationCollection getInstance() {
		if (relations == null) {
			relations = new RelationCollection();
		}

		return relations;
	}
	
	public Relation getRelation(String name, String attributes){
		String key = name+"_"+Integer.toString(attributes.hashCode());
		
		if (!map.containsKey(key)){
			map.put(key, new Relation(name,attributes));
		}
		return map.get(key);		
	}
}
