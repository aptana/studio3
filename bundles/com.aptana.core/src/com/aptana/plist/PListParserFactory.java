/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.plist;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ch.randelshofer.quaqua.util.BinaryPListParser;

import com.aptana.plist.xml.XMLPListParser;

public class PListParserFactory
{

	public static Map<String, Object> parse(File file) throws IOException
	{
		if (file == null)
		{
			return null;
		}
		try
		{
			// Try binary format. This should fail quickly with IOException if it's not binary!
			BinaryPListParser parser = new BinaryPListParser();
			return parser.parse(file);
		}
		catch (Exception e)
		{
			// Assume XML now...
			XMLPListParser parser = new XMLPListParser();
			return parser.parse(file);
		}
	}
}
