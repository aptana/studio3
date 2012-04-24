/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.ISourceViewer;
import org.junit.Test;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.hover.DocumentationBrowserInformationControlInput;
import com.aptana.editor.common.util.EditorUtil;
import com.aptana.editor.js.JSSourceEditor;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.editor.js.tests.JSEditorBasedTests;

public class JSTextHoverTest extends JSEditorBasedTests
{
	private JSTextHover hover;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		hover = new JSTextHover();
	}

	@Override
	protected void tearDown() throws Exception
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

	protected JSSourceEditor getJSEditor()
	{
		return (JSSourceEditor) this.editor;
	}

	protected ISourceViewer getSourceViewer()
	{
		return getJSEditor().getISourceViewer();
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
		indexWriter.writeType(getIndex(), window, EditorUtil.getURI(getJSEditor()));

	}

	public void test1236()
	{
		assertHoverRegionAndInfo("hover/1236.js", "win2", "These are docs for win2", new Region(4, 4));
	}

	public void testTagStrippingAndTypeBolding()
	{
		// grab hover info
		Object info = assertHoverRegionAndInfo("hover/1236.js", "win2",
				"<p>This relates to <Titanium.UI.createWindow></p>", null);

		// grab hover HTML content
		assertTrue(info instanceof DocumentationBrowserInformationControlInput);
		DocumentationBrowserInformationControlInput input = (DocumentationBrowserInformationControlInput) info;
		String html = input.getHtml();
		int count = 0;

		// HACK: retry grabbing HTML, with delay, up to 5 times
		while (count < 5 && StringUtil.isEmpty(html))
		{
			IRegion hoverRegion = getHoverRegion(this.cursorOffsets.get(0));
			info = hover.getHoverInfo2(getSourceViewer(), hoverRegion);
			input = (DocumentationBrowserInformationControlInput) info;

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
			}

			html = input.getHtml();
			count++;
		}

		// test that HTML ends with our cleaned-up description
		Pattern p = Pattern.compile("<body[^>]*>(.*?)</body>");
		Matcher m = p.matcher(html);

		if (m.find())
		{
			String text = m.group(1);

			assertTrue("Could not find documentation string in hover info: '" + html + "'",
					text.endsWith("This relates to <b>Titanium.UI.createWindow</b>"));
		}
		else
		{
			fail("Could not find documentation string in hover info: '" + html + "'");
		}
	}

	@Test
	public void testVariableReference()
	{
		assertHoverRegionAndInfo("hover/var_ref.js", "win2", "These are docs for win2", new Region(91, 4));
	}
}
