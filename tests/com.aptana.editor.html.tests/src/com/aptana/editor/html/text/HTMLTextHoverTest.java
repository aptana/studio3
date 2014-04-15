/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.html.HTMLMetadataLoader;
import com.aptana.editor.html.contentassist.HTMLIndexQueryHelper;
import com.aptana.editor.html.contentassist.model.BaseElement;
import com.aptana.editor.html.tests.HTMLEditorBasedTests;

@SuppressWarnings("rawtypes")
public class HTMLTextHoverTest extends HTMLEditorBasedTests
{
	private HTMLTextHover hover;
	private Object fHeaderElement;
	private HTMLIndexQueryHelper fQueryHelper;

	@Before
	public void setUp() throws Exception
	{
		HTMLMetadataLoader loader = new HTMLMetadataLoader();
		loader.schedule();
		loader.join();

		hover = new HTMLTextHover()
		{

			@Override
			public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
			{
				fHeaderElement = element;
				return super.getHeader(element, editorPart, hoverRegion);
			}
		};
		fQueryHelper = new HTMLIndexQueryHelper();
	}

	@Override
	public void tearDown() throws Exception
	{
		hover = null;
		fHeaderElement = null;
		fQueryHelper = null;
		super.tearDown();
	}

	@Test
	public void testHTMLElement()
	{
		assertHoverRegionAndInfo("hover/element.html", getElement("html"), new Region(17, 4));
	}

	@Test
	public void testH1AttributeClass()
	{
		assertHoverRegionAndInfo("hover/attribute.html", getAttribute("h1", "class"), new Region(86, 6));
	}

	@Test
	public void testAtSpaceInsideElementStartTag()
	{
		assertNoHover("hover/inside_element_space.html");
	}

	@Test
	public void testNoHoverOverAttributeValue()
	{
		assertNoHover("hover/attribute_value.html");
	}

	protected BaseElement getElement(String name)
	{
		return fQueryHelper.getElement(name);
	}

	protected BaseElement getAttribute(String elementName, String attributeName)
	{
		return fQueryHelper.getAttribute(elementName, attributeName);
	}

	protected void assertNoHover(String resource)
	{
		// setup editor and index
		setupTestContext(resource);

		// check hover region
		IRegion hoverRegion = getHoverRegion(this.cursorOffsets.get(0));

		assertNull("Expected no hover, but got a region returned", hoverRegion);
	}

	protected Object assertHoverRegionAndInfo(String resource, BaseElement hoverElement, Region expectedRegion)
	{
		// setup editor and index
		setupTestContext(resource);

		// check hover region
		IRegion hoverRegion = assertHoverRegion(expectedRegion);

		// check hover info
		return assertHoverInfo(hoverElement, hoverRegion);
	}

	/**
	 * @param hoverRegion
	 */
	private Object assertHoverInfo(BaseElement hoverElement, IRegion hoverRegion)
	{
		Object info = hover.getHoverInfo2(getSourceViewer(), hoverRegion);

		assertNotNull("Should have gotten hover info!", info);
		assertEquals("Metadata element doesn't match", hoverElement, fHeaderElement);

		return info;
	}

	/**
	 * @param expectedRegion
	 * @return
	 */
	private IRegion assertHoverRegion(Region expectedRegion)
	{
		IRegion hoverRegion = getHoverRegion(this.cursorOffsets.get(0));

		if (expectedRegion != null)
		{
			assertNotNull(hoverRegion);
			assertEquals("Incorrect hover region returned", expectedRegion, hoverRegion);
		}

		return hoverRegion;
	}

	protected IRegion getHoverRegion(int offset)
	{
		return hover.getHoverRegion(getSourceViewer(), offset);
	}
}
