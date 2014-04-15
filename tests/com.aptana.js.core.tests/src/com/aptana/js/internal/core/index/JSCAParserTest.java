/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.index;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.model.TypeElement;

public class JSCAParserTest
{

	private IJSCAParser parser;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		parser = new JSCAParser();
	}

	@After
	public void tearDown() throws Exception
	{
		parser = null;
//		super.tearDown();
	}

	@Test
	public void testTISTUD5079() throws Exception
	{
		URL url = FileLocator.find(JSCorePlugin.getDefault().getBundle(),
				Path.fromPortableString("indexing/arbitrary_property.jsca"), null);

		// Ensure we can parse a JSCA fle that has an arbitrary property we don't recognize (we just ignore it).
		IJSCAModel model = parser.parse(url.openStream());

		List<TypeElement> types = model.getTypes();
		assertEquals(1, types.size());
	}

}
