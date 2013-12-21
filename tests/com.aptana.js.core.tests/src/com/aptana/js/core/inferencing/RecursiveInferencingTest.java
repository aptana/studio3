/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

public class RecursiveInferencingTest extends InferencingTestsBase
{
	/**
	 * testIdentifierCycle
	 */
	@Test
	public void testIdentifierCycle()
	{
		this.varTypeTests("var a = b, b = a;", "a");
	}

	/**
	 * testIdentifierCycle2
	 */
	@Test
	public void testIdentifierCycle2()
	{
		this.varTypeTests("var a = b, b = a;", "b");
	}

	/**
	 * testIdentifierCycle3
	 */
	@Test
	public void testIdentifierCycle3()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "a");
	}

	/**
	 * testIdentifierCycle4
	 */
	@Test
	public void testIdentifierCycle4()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "b");
	}

	/**
	 * testIdentifierCycle5
	 */
	@Test
	public void testIdentifierCycle5()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "c");
	}

	/**
	 * testInvocationCycle
	 */
	@Test
	public void testInvocationCycle()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/invocation-cycle-1.js"), "Function");
	}

	/**
	 * testInvocationCycle2
	 */
	@Test
	public void testInvocationCycle2()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/invocation-cycle-2.js"), "Number");
	}

	/**
	 * testInvocationCycle3
	 */
	@Test
	public void testInvocationCycle3()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/invocation-cycle-3.js"), "Number");
	}

	/**
	 * testObjectPropertyReturnsSelf
	 * 
	 * @throws Exception
	 */
	@Test
	public void testObjectPropertyReturnsSelf() throws Exception
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/chaining.js"), "Utils.create.utilsSelf");
	}

	/*
	 * APSTUD-4864 JS Type Inferencing fails to check local scope for variable type before checking globals
	 */
	@Test
	public void testReturnSelfVarDefinedInLocalScopeBeforeConsultingGlobals() throws Exception
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/chaining-self.js"), "Utils.create.self");
	}
}
