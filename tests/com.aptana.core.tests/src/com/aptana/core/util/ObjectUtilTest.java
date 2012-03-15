/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import junit.framework.TestCase;

/**
 * ObjectUtilTest
 */
public class ObjectUtilTest extends TestCase
{
	public void testAreNotEqual1()
	{
		assertFalse(ObjectUtil.areNotEqual(null, null));
	}

	public void testAreNotEqual2()
	{
		assertTrue(ObjectUtil.areNotEqual(null, "test"));
	}

	public void testAreNotEqual3()
	{
		assertTrue(ObjectUtil.areNotEqual("test", null));
	}

	public void testAreNotEqual4()
	{
		assertTrue(ObjectUtil.areNotEqual("test", "tes"));
	}

	public void testAreNotEqual5()
	{
		assertFalse(ObjectUtil.areNotEqual("test", "test"));

	}

	public void testAreEqual1()
	{
		assertTrue(ObjectUtil.areEqual(null, null));
	}

	public void testAreEqual2()
	{
		assertFalse(ObjectUtil.areEqual(null, "test"));
	}

	public void testAreEqual3()
	{
		assertFalse(ObjectUtil.areEqual("test", null));
	}

	public void testAreEqual4()
	{
		assertTrue(ObjectUtil.areEqual("test", "test"));
	}

	public void testAreEqual5()
	{
		assertFalse(ObjectUtil.areEqual("test", "tes"));
	}
}
