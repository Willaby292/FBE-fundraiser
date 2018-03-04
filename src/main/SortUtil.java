package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SortUtil {

	public static enum Type {
		ALPHABETICAL("Alphabetical", new AlphabeticalComparator()),
		AMOUNT("Largest Donation", new AmountComparator()),
		CHRONOLOGICAL("Chronological", new ChronologicalComparator());

		private final Comparator<Donation>	comparator;
		private final String				displayName;

		Type(String displayName, Comparator<Donation> comparator) {
			this.displayName = displayName;
			this.comparator = comparator;
		}

		public String displayName() {
			return displayName;
		}

	}

	public static List<Donation> getAsSortedList(Map<String, Set<Donation>> donationsMap, Type type, boolean sumTotal) {
		Map<String, Set<Donation>> map = new HashMap<String, Set<Donation>>(donationsMap);
		List<Donation> sorted = new ArrayList<Donation>();

		if (sumTotal) {
			for (String name : map.keySet()) {
				Double total = map.get(name).stream().mapToDouble(Donation::getAmount).sum();
				Donation allDonations = new Donation(name, total);
				sorted.add(allDonations);
			}

		} else {
			for (String name : map.keySet()) {
				for (Donation donation : map.get(name)) {
					sorted.add(donation);
				}

			}
		}
		sorted.sort(type.comparator);
		return sorted;
	}

	private static class AlphabeticalComparator implements Comparator<Donation> {
		@Override
		public int compare(Donation d1, Donation d2) {
			int nameCompare = d1.getName().compareTo(d2.getName());
			if (nameCompare == 0) {
				int amountCompare = d1.getAmount().compareTo(d2.getAmount());
				if (amountCompare == 0) {
					return d1.getTime().compareTo(d2.getTime());
				} else {
					return amountCompare;
				}
			}
			return nameCompare;
		}
	}

	private static class ChronologicalComparator implements Comparator<Donation> {
		@Override
		public int compare(Donation d1, Donation d2) {
			return d1.getTime().compareTo(d2.getTime());
		}
	}

	private static class AmountComparator implements Comparator<Donation> {
		@Override
		public int compare(Donation d1, Donation d2) {
			int amountCompare = d2.getAmount().compareTo(d1.getAmount());
			if (amountCompare == 0) {
				int timeCompare = d1.getTime().compareTo(d2.getTime());
				if (timeCompare == 0) {
					return d1.getName().compareTo(d2.getName());
				} else {
					return timeCompare;
				}
			}
			return amountCompare;
		}

	}

}