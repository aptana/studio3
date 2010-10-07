/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of Modified BSD License
 * which accompanies this distribution. If redistributing this code,
 * this entire header must remain intact.
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
		try
		{
			// Try binary format. This should fail quickly with IOException if it's not binary!
			BinaryPListParser parser = new BinaryPListParser();
			return parser.parse(file);
		}
		catch (IOException e)
		{
			// Assume XML now...
			XMLPListParser parser = new XMLPListParser();
			return parser.parse(file);
		}
	}
}
