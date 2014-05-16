/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

/**
 * ObjectUtilTest
 */
public class ObjectUtilTest
{
	@Test
	public void testAreNotEqual1()
	{
		assertFalse(ObjectUtil.areNotEqual(null, null));
	}

	@Test
	public void testAreNotEqual2()
	{
		assertTrue(ObjectUtil.areNotEqual(null, "test"));
	}

	@Test
	public void testAreNotEqual3()
	{
		assertTrue(ObjectUtil.areNotEqual("test", null));
	}

	@Test
	public void testAreNotEqual4()
	{
		assertTrue(ObjectUtil.areNotEqual("test", "tes"));
	}

	@Test
	public void testAreNotEqual5()
	{
		assertFalse(ObjectUtil.areNotEqual("test", "test"));

	}

	@Test
	public void testAreEqual1()
	{
		assertTrue(ObjectUtil.areEqual(null, null));
	}

	@Test
	public void testAreEqual2()
	{
		assertFalse(ObjectUtil.areEqual(null, "test"));
	}

	@Test
	public void testAreEqual3()
	{
		assertFalse(ObjectUtil.areEqual("test", null));
	}

	@Test
	public void testAreEqual4()
	{
		assertTrue(ObjectUtil.areEqual("test", "test"));
	}

	@Test
	public void testAreEqual5()
	{
		assertFalse(ObjectUtil.areEqual("test", "tes"));
	}
}
