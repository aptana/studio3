/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

public class NameSelectorTests
{
	/**
	 * testNameIsPrefix
	 */
	@Test
	public void testNameIsPrefix()
	{
		NameSelector name = new NameSelector("source");
		IScopeSelector selector = new ScopeSelector(name);

		assertTrue(selector.matches("source.ruby"));
	}

	/**
	 * testNameIsIdentical
	 */
	@Test
	public void testNameIsIdentical()
	{
		NameSelector name = new NameSelector("source.ruby");
		IScopeSelector selector = new ScopeSelector(name);

		assertTrue(selector.matches("source.ruby"));
	}

	/**
	 * testNameIsPartial
	 */
	@Test
	public void testNameIsPartial()
	{
		NameSelector name = new NameSelector("sourc");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches("source.ruby"));
	}

	/**
	 * testNameIsEmpty
	 */
	@Test
	public void testNameIsEmpty()
	{
		NameSelector name = new NameSelector("");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches("source.ruby"));
	}

	/**
	 * testScopeIsEmpty
	 */
	@Test
	public void testScopeIsEmpty()
	{
		NameSelector name = new NameSelector("source");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches(""));
	}

	/**
	 * testNameIsNull
	 */
	@Test
	public void testNameIsNull()
	{
		NameSelector name = new NameSelector(null);
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches("source.ruby"));
	}

	/**
	 * testScopeIsNull
	 */
	@Test
	public void testScopeIsNull()
	{
		NameSelector name = new NameSelector("source");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches((String) null));
	}
}
