package com.ugnavigate.utils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SortingUtils {

	public static <T> List<T> sortBy(List<T> items, Comparator<T> comparator) {
		if (items == null || comparator == null) return items;
		return items.stream().sorted(comparator).collect(Collectors.toList());
	}
}
