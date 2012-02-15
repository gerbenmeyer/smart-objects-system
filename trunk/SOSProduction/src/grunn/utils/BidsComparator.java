package grunn.utils;

import grunn.world.GRUNNMessage;

import java.util.Comparator;

public class BidsComparator implements Comparator<GRUNNMessage> {

	@Override
	public int compare(GRUNNMessage arg0, GRUNNMessage arg1) {
		if (arg0.getBidUnitPrice() < arg1.getBidUnitPrice()) {
			return 1;
		} else if (arg0.getBidUnitPrice() > arg1.getBidUnitPrice()) {
			return -1;
		} else if (arg0.getBidAmount() < arg1.getBidAmount()) {
			return 1;
		} else if (arg0.getBidAmount() > arg1.getBidAmount()) {
			return -1;
		}
		return 0;
	}
}
