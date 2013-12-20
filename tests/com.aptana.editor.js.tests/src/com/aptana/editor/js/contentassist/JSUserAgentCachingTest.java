/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ListCrossProduct;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.UserAgentElement;

/**
 * JSUserAgentCachingTests
 */
public class JSUserAgentCachingTest
{
	protected ListCrossProduct<String> createCrossProduct(List<String>... lists)
	{
		ListCrossProduct<String> result = new ListCrossProduct<String>();

		for (List<String> list : lists)
		{
			result.addList(list);
		}

		return result;
	}

	protected UserAgentElement getUserAgent(List<String> list)
	{
		String product = list.get(0);
		String version = list.get(1);
		String os = list.get(2);
		String osVersion = list.get(3);
		String description = list.get(4);

		return UserAgentElement.createUserAgentElement(product, version, os, osVersion, description);
	}

	@Test
	public void testNewUserAgent()
	{
		UserAgentElement.clearCache();
		UserAgentElement uaNoncached = new UserAgentElement();
		uaNoncached.setPlatform("IE");
		UserAgentElement uaCached = UserAgentElement.createUserAgentElement("IE");

		assertNotSame(uaNoncached, uaCached);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCachedUserAgent()
	{
		UserAgentElement.clearCache();
		List<String> products = CollectionsUtil.newList("IE");
		List<String> versions = CollectionsUtil.newList("", "1.0");
		List<String> oses = CollectionsUtil.newList("", "Mac OS X");
		List<String> osVersions = CollectionsUtil.newList("", "10.7");
		List<String> description = CollectionsUtil.newList("", "Simple description");

		ListCrossProduct<String> cp1 = createCrossProduct(products, versions, oses, osVersions, description);
		ListCrossProduct<String> cp2 = createCrossProduct(products, versions, oses, osVersions, description);

		for (List<String> list1 : cp1)
		{
			UserAgentElement ua1 = getUserAgent(list1);

			for (List<String> list2 : cp2)
			{
				UserAgentElement ua2 = getUserAgent(list2);

				if (list1.equals(list2))
				{
					assertSame(ua1, ua2);
				}
				else
				{
					assertNotSame(ua1, ua2);
				}
			}
		}
	}

	@Test
	public void testHasAllUserAgents()
	{
		UserAgentElement.clearCache();
		PropertyElement property = new PropertyElement();
		List<UserAgentElement> uaListBefore = property.getUserAgents();

		assertNotNull(uaListBefore);
		assertTrue(uaListBefore.isEmpty());

		property.setHasAllUserAgents();
		List<UserAgentElement> uaListAfter = property.getUserAgents();

		assertTrue(property.hasAllUserAgents());
		assertNotNull(uaListAfter);
		assertFalse(uaListAfter.isEmpty());
	}

	@Test
	public void testAddExistingUserAgentToAllUserAgents()
	{
		UserAgentElement.clearCache();
		PropertyElement property = new PropertyElement();

		property.setHasAllUserAgents();
		property.addUserAgent(UserAgentElement.createUserAgentElement("IE"));

		assertTrue(property.hasAllUserAgents());
	}

	@Test
	public void testAddNewUserAgentToAllUserAgents()
	{
		UserAgentElement.clearCache();
		PropertyElement property = new PropertyElement();

		// set to use all ua's
		property.setHasAllUserAgents();

		// grab current list
		Set<UserAgentElement> allUAs = new HashSet<UserAgentElement>(property.getUserAgents());
		assertFalse(allUAs.isEmpty());

		// add a user agent not in the "all ua's" list
		property.addUserAgent(UserAgentElement.createUserAgentElement("MyCrazyBrowser"));

		// grab the new list of ua's
		Set<UserAgentElement> augmentedUAs = new HashSet<UserAgentElement>(property.getUserAgents());

		// perform a diff (aug - all)
		augmentedUAs.removeAll(allUAs);

		// make sure all ua's flag is off now
		assertFalse(property.hasAllUserAgents());

		// we should have only our new custom browser
		assertEquals(1, augmentedUAs.size());
		UserAgentElement ua = augmentedUAs.iterator().next();
		assertEquals("MyCrazyBrowser", ua.getPlatform());
	}
}
