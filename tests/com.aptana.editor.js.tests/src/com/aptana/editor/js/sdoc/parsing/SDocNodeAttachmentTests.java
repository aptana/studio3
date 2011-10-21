/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;


/**
 * SDocNodeAttachmentTests
 */
public class SDocNodeAttachmentTests extends DocNodeAttachementTestBase
{
	public void testSDocOnFunction()
	{
		String resource = "sdoc/sdocOnFunction.js";
		String xpath = "function[@name='abc']";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	public void testSDocOnSelfInvokingLambda()
	{
		String resource = "sdoc/sdocOnSelfInvokingLambda.js";
		String xpath = "invoke/group/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	public void testSDocOnObjectLiteralProperty()
	{
		String resource = "sdoc/sdocOnObjectLiteralProperty.js";
		String xpath = "var/declaration/object_literal/name_value_pair/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	public void testSDocOnAssignment()
	{
		String resource = "sdoc/sdocOnAssignment.js";
		String xpath = "assign/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	public void testSDocOnDeclaration()
	{
		String resource = "sdoc/sdocOnDeclaration.js";
		String xpath = "var/declaration/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}
}
