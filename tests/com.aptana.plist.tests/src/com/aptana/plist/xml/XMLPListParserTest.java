/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.plist.xml;

import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.aptana.plist.tests.AbstractPlistParserTestCase;

public class XMLPListParserTest extends AbstractPlistParserTestCase
{

	public void testParseXMLWithInvalidCharacters() throws Exception
	{
		XMLPListParser plistParser = new XMLPListParser();
		Map<String, Object> result = plistParser.parse(getFileInTestFragment(Path
				.fromPortableString("plists/brilliance_dull_2.tmTheme")));

		assertNotNull(result);
		assertEquals(5, result.size());
		assertEquals("Brilliance Dull", result.get("name"));
		assertEquals("Thomas Aylott", result.get("author"));
		assertEquals("by Thomas Aylott subtleGradient.com", result.get("comment"));
		assertEquals("4535004C-927A-401A-A6D5-1C9AC89E24C6", result.get("uuid"));
	}

	@Override
	protected IPath getExampleFilePath()
	{
		return Path.fromPortableString("plists/xml.plist");
	}
}
