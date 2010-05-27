package com.aptana.core.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.aptana.core.util.CollectionsUtil;

public class CollectionsUtilTest extends TestCase
{

	public void testRemoveDuplicates() throws Exception
	{
		Integer[] array = { 0, 1, 1, 2, 3, 3, 3 };
		List<Integer> list = new ArrayList<Integer>();
		for (Integer element : array)
		{
			list.add(element);
		}
		CollectionsUtil.removeDuplicates(list);
		for (int i = 0; i < list.size(); ++i)
		{
			assertEquals(i, list.get(i).intValue());
		}
	}
}
