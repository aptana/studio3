/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.model;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.IMap;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;

public class DTDTransformerTest
{
	private DTDTransformer transformer;

//	@Override
	@Before
	public void setUp() throws Exception
	{
//		super.setUp();

		transformer = new DTDTransformer();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		transformer = null;
//		super.tearDown();
	}

	protected String getContent(String path)
	{
		try
		{
			InputStream stream = FileLocator.openStream(Platform.getBundle("com.aptana.xml.core.tests"),
					Path.fromPortableString(path), false);
			return IOUtil.read(stream);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
			// never reached...
			return StringUtil.EMPTY;
		}
	}

	private void transform(String path) throws DTDTransformException
	{
		transformer.transform(getContent(path));
	}

	protected void assertAttributes(String element, String... attributes) throws DTDTransformException
	{
		// find target element
		ElementElement targetElement = null;

		for (ElementElement e : transformer.getElements())
		{
			if (e.getName().equals(element))
			{
				targetElement = e;
				break;
			}
		}

		assertNotNull(targetElement);
		// assertEquals(attributes.length, targetElement.getAttributes().size());

		// generate set of attribute name
		Set<String> names = new HashSet<String>();

		for (String name : targetElement.getAttributes())
		{
			names.add(name);
		}

		// assert
		for (String name : attributes)
		{
			assertTrue("Did not find attribute: " + name, names.contains(name));
		}
	}

	protected void assertElements(String... elements) throws DTDTransformException
	{
		// gather element names for easy lookup
		List<ElementElement> elementObjects = transformer.getElements();
		assertNotNull("Was unable to determine elements", elementObjects);
		Set<String> names = new HashSet<String>(CollectionsUtil.map(elementObjects, new IMap<ElementElement, String>()
		{
			public String map(ElementElement item)
			{
				return item.getName();
			}
		}));

		// assert the element name list
		for (String name : elements)
		{
			assertTrue("Did not find element: " + name, names.contains(name));
		}
	}

	@Test
	public void testSingleElement() throws DTDTransformException
	{
		transform("DTD/singleElement.dtd");
		assertElements("svg");
	}

	@Test
	public void testMultipleElements() throws DTDTransformException
	{
		transform("DTD/multipleElements.dtd");
		assertElements("svg", "circle", "ellipse", "rectangle", "path");
	}

	@Test
	public void testSingleAttribute() throws DTDTransformException
	{
		transform("DTD/elementAttribute.dtd");
		assertAttributes("svg", "x");
	}

	@Test
	public void testMultipleAttributes() throws DTDTransformException
	{
		transform("DTD/multipleElementAttributes.dtd");
		assertAttributes("svg", "x", "y", "width", "height");
	}

	@Test
	public void testSVGDTD() throws DTDTransformException
	{
		transform("DTD/svg11-flat.dtd");
		assertElements("svg");
	}
}
