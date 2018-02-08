/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.junit.Before;
import org.junit.Test;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.hover.TagStripperAndTypeBolder;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.tests.JSEditorBasedTestCase;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.internal.core.index.JSIndexWriter;

public class JSTextHoverTest extends JSEditorBasedTestCase
{
	private JSTextHover hover;

	@Before
	public void setUp() throws Exception
	{
		hover = new JSTextHover();
	}

	@Override
	public void tearDown() throws Exception
	{
		hover = null;
		super.tearDown();
	}

	protected Object assertHoverRegionAndInfo(String resource, String propertyName, String description,
			Region expectedRegion)
	{
		// setup editor and index
		setupProperty(resource, propertyName, description);

		// check hover region
		IRegion hoverRegion = assertHoverRegion(expectedRegion);

		// check hover info
		return assertHoverInfo(hoverRegion);
	}

	/**
	 * @param resource
	 * @param propertyName
	 * @param description
	 */
	private void setupProperty(String resource, String propertyName, String description)
	{
		setupTestContext(resource);
		writeProperty(propertyName, description);
	}

	/**
	 * @param hoverRegion
	 */
	private Object assertHoverInfo(IRegion hoverRegion)
	{
		Object info = hover.getHoverInfo2(getSourceViewer(), hoverRegion);

		assertNotNull("Should have gotten docs on the 'win2' variable we're assigning to!", info);

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

	protected void writeProperty(String name, String description)
	{
		// create property with description
		PropertyElement property = new PropertyElement();
		property.setName(name);
		property.setDescription(description);

		// create containing type and add property to it
		TypeElement window = new TypeElement();
		window.setName("Window");
		window.addProperty(property);

		// write type to index
		JSIndexWriter indexWriter = new JSIndexWriter();
		indexWriter.writeType(getIndex(), window, EditorUtil.getURI(this.editor));

	}

	@Test
	public void test1236()
	{
		assertHoverRegionAndInfo("hover/1236.js", "win2", "These are docs for win2", new Region(4, 4));
	}

	@Test
	public void testTagStrippingAndTypeBolding()
	{
		TagStripperAndTypeBolder formatter = new TagStripperAndTypeBolder();
		formatter.setUseHTML(true);

		String source = "<p>This relates to <Titanium.UI.createWindow></p>";
		String expected = "This relates to <b>Titanium.UI.createWindow</b>";
		String result = formatter.searchAndReplace(source);

		assertEquals(expected, result);
	}

	@Test
	public void testPropertyHoverInfo()
	{
		assertHoverRegionAndInfo("hover/deprecatedProp.js", "win2", "These are docs for win2", new Region(96, 7));
	}

	@Test
	public void testVariableReference()
	{
		assertHoverRegionAndInfo("hover/var_ref.js", "win2", "These are docs for win2", new Region(91, 4));
	}
}
