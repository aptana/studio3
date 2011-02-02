/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import org.eclipse.core.runtime.Path;

public class RecursiveInferencingTests extends InferencingTestsBase
{
	/**
	 * testIdentifierCycle
	 */
	public void testIdentifierCycle()
	{
		this.varTypeTests("var a = b, b = a;", "a");
	}
	
	/**
	 * testIdentifierCycle2
	 */
	public void testIdentifierCycle2()
	{
		this.varTypeTests("var a = b, b = a;", "b");
	}

	/**
	 * testIdentifierCycle3
	 */
	public void testIdentifierCycle3()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "a");
	}
	
	/**
	 * testIdentifierCycle4
	 */
	public void testIdentifierCycle4()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "b");
	}
	
	/**
	 * testIdentifierCycle5
	 */
	public void testIdentifierCycle5()
	{
		this.varTypeTests("var a = b, b = c, c = a;", "c");
	}

	/**
	 * testInvocationCycle
	 */
	public void testInvocationCycle()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/invocation-cycle-1.js"), "Function");
	}

	/**
	 * testInvocationCycle2
	 */
	public void testInvocationCycle2()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/invocation-cycle-2.js"), "Number");
	}

	/**
	 * testInvocationCycle3
	 */
	public void testInvocationCycle3()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/invocation-cycle-3.js"), "Number");
	}

	/**
	 * testObjectPropertyReturnsSelf
	 */
	public void testObjectPropertyReturnsSelf()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/chaining.js"), "Utils.create.self");
	}
}
