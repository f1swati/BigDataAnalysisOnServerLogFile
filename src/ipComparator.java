/*
 * This Program was developed by Swati Mittal. 
 * Date - 20-Feb-2016
 * This module implements the PriorityQueue;
 */

import java.util.Comparator;

public class ipComparator implements Comparator<ipAdressCount> {

	@Override
	public int compare(ipAdressCount o1, ipAdressCount o2) {
		if (o1.count < o2.count) {
			return -1;
		} else if (o1.count > o2.count) {
			return 1;
		}
		return 0;
	}

}
