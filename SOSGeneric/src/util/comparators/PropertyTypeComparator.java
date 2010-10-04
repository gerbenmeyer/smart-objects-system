package util.comparators;

import java.util.Comparator;

import model.agent.property.Property;
import util.enums.PropertyType;

/**
 * Comparator to sort Properties, based on their type.
 * 
 * @author Gerben G. Meyer
 */
public class PropertyTypeComparator implements Comparator<Property> {

	@Override
	public int compare(Property arg0, Property arg1) {
		if (arg0.getPropertyType() == arg1.getPropertyType()) {
			return arg0.getName().compareTo(arg1.getName());
		} else {
			if (arg0.getPropertyType() == PropertyType.STATUS) {
				return -1;
			} else if (arg1.getPropertyType() == PropertyType.STATUS) {
				return 1;
			} else if (arg0.getPropertyType() == PropertyType.DEPENDENCIES) {
				return 1;
			} else if (arg1.getPropertyType() == PropertyType.DEPENDENCIES) {
				return -1;
			} else if (arg0.getPropertyType() == PropertyType.HISTORY) {
				return 1;
			} else if (arg1.getPropertyType() == PropertyType.HISTORY) {
				return -1;
			} else {
				return arg0.getPropertyType().toString().compareTo(arg1.getPropertyType().toString());
			}
		}
	}
}