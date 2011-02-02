/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import org.eclipse.core.runtime.Path;

public class DocumentationTests extends InferencingTestsBase
{
	/**
	 * testFunctionReturnType
	 */
	public void testFunctionReturnType()
	{
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/return-type-documentation.js"), "Function:Number");
	}
	
	/**
	 * testFunctionReturnTypes
	 */
	public void testFunctionReturnTypes()
	{		
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/return-types-documentation.js"), "Function:Number,RegExp");
	}
	
	/**
	 * testVarType
	 */
	public void testVarType()
	{		
		this.lastStatementTypeTests(Path.fromPortableString("inferencing/type-documentation.js"), "Node");
	}
}
