package com.aptana.core.util;

import java.util.LinkedHashSet;
import java.util.List;

public class CollectionsUtil {
	/**
	 * Given a list of elements of type <T>, remove the duplicates from the list in place
	 * 
	 * @param <T>
	 * @param list
	 */
	public static <T> void removeDuplicates(final List<T> list)
	{
	    final LinkedHashSet<T> set;

	    set = new LinkedHashSet<T>(list); 
	    list.clear(); 
	    list.addAll(set);
	}
}
