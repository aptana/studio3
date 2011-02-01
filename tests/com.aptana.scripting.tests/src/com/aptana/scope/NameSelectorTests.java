/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scope;

import junit.framework.TestCase;

public class NameSelectorTests extends TestCase
{
	/**
	 * testNameIsPrefix
	 */
	public void testNameIsPrefix()
	{
		NameSelector name = new NameSelector("source");
		IScopeSelector selector = new ScopeSelector(name);

		assertTrue(selector.matches("source.ruby"));
	}

	/**
	 * testNameIsIdentical
	 */
	public void testNameIsIdentical()
	{
		NameSelector name = new NameSelector("source.ruby");
		IScopeSelector selector = new ScopeSelector(name);

		assertTrue(selector.matches("source.ruby"));
	}

	/**
	 * testNameIsPartial
	 */
	public void testNameIsPartial()
	{
		NameSelector name = new NameSelector("sourc");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches("source.ruby"));
	}

	/**
	 * testNameIsEmpty
	 */
	public void testNameIsEmpty()
	{
		NameSelector name = new NameSelector("");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches("source.ruby"));
	}

	/**
	 * testScopeIsEmpty
	 */
	public void testScopeIsEmpty()
	{
		NameSelector name = new NameSelector("source");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches(""));
	}

	/**
	 * testNameIsNull
	 */
	public void testNameIsNull()
	{
		NameSelector name = new NameSelector(null);
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches("source.ruby"));
	}

	/**
	 * testScopeIsNull
	 */
	public void testScopeIsNull()
	{
		NameSelector name = new NameSelector("source");
		IScopeSelector selector = new ScopeSelector(name);

		assertFalse(selector.matches((String) null));
	}
}
