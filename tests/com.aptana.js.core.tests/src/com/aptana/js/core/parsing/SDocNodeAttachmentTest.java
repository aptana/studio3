/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import org.junit.Test;

/**
 * SDocNodeAttachmentTests
 */
public class SDocNodeAttachmentTest extends DocNodeAttachementTestCase
{
	@Test
	public void testSDocOnFunction()
	{
		String resource = "sdoc/sdocOnFunction.js";
		String xpath = "function[@name='abc']";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	@Test
	public void testSDocOnSelfInvokingLambda()
	{
		String resource = "sdoc/sdocOnSelfInvokingLambda.js";
		String xpath = "invoke/group/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	@Test
	public void testSDocOnObjectLiteralProperty()
	{
		String resource = "sdoc/sdocOnObjectLiteralProperty.js";
		String xpath = "var/declaration/object_literal/name_value_pair/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	/**
	 * APSTUD-4706
	 */
	@Test
	public void testSDocOnObjectLiteralProperty2()
	{
		String resource = "sdoc/sdocOnObjectLiteralProperty2.js";
		String xpath = "assign/invoke/arguments/object_literal/name_value_pair[1]/function";
		String description = "Render the items that will import a sample project.";

		assertDescription(resource, xpath, description);
	}

	@Test
	public void testSDocOnAssignment()
	{
		String resource = "sdoc/sdocOnAssignment.js";
		String xpath = "assign/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}

	@Test
	public void testSDocOnDeclaration()
	{
		String resource = "sdoc/sdocOnDeclaration.js";
		String xpath = "var/declaration/function";
		String description = "This is an sdoc node";

		assertDescription(resource, xpath, description);
	}
}
