/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing;

import org.junit.Test;

import com.aptana.js.core.parsing.DocNodeAttachementTestCase;

/**
 * VSDocNodeAttachmentTests
 */
public class VSDocNodeAttachmentTest extends DocNodeAttachementTestCase
{
	@Test
	public void testVDocOnFunction()
	{
		String resource = "vsdoc/vsdocOnFunction.js";
		String xpath = "function[@name='abc']";
		String description = "This is an vsdoc node";

		assertDescription(resource, xpath, description);
	}

	@Test
	public void testVDocOnNonFunction()
	{
		String resource = "vsdoc/vsdocOnArray.js";
		String xpath = "assign/array_literal";
		String description = "This is an vsdoc node";

		assertDescription(resource, xpath, description);
	}
}
