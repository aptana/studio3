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

public class DocumentationTest extends InferencingTestsBase
{
	/**
	 * testFunctionReturnType
	 */
	@Test
	public void testFunctionReturnType()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/return-type-documentation.js"),
				"Function<Number>");
	}

	/**
	 * testFunctionReturnTypes
	 */
	@Test
	public void testFunctionReturnTypes()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/return-types-documentation.js"),
				"Function<Number,RegExp>");
	}

	/**
	 * testVarType
	 */
	@Test
	public void testVarType()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/type-documentation.js"), "Node");
	}
}
