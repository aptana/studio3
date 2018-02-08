/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileWriter;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aptana.css.core.index.CSSIndexQueryHelper;
import com.aptana.css.core.index.CSSMetadataLoader;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.css.text.CSSTextHover;
import com.aptana.editor.epl.tests.EditorTestHelper;
import com.aptana.ui.util.UIUtils;

/**
 * CSSTextHoverTests
 */
public class CSSTextHoverTests
{
	private CSSTextHover fHover;
	private AbstractThemeableEditor editor;
	private File file;
	private Object hoverObject;
	private CSSIndexQueryHelper queryHelper;

	@Before
	public void setUp() throws Exception
	{
		CSSMetadataLoader loader = new CSSMetadataLoader();
		loader.schedule();
		loader.join();

		queryHelper = new CSSIndexQueryHelper();

		fHover = new CSSTextHover()
		{
			@Override
			public String getHeader(Object element, IEditorPart editorPart, IRegion hoverRegion)
			{
				hoverObject = element;
				return super.getHeader(element, editorPart, hoverRegion);
			}
		};
	}

	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (editor != null)
			{
				EditorTestHelper.closeEditor(editor);
				editor = null;
			}
			if (file != null)
			{
				if (!file.delete())
				{
					file.deleteOnExit();
				}
				file = null;
			}
		}
		finally
		{
			queryHelper = null;
			hoverObject = null;
			file = null;
			editor = null;
			fHover = null;
		}
	}

	/**
	 * getTextViewer
	 * 
	 * @param source
	 * @return
	 */
	protected ITextViewer getTextViewer(String source) throws Exception
	{
		file = File.createTempFile("test_css_hover", ".css");
		FileWriter writer = new FileWriter(file);
		writer.write(source);
		writer.close();
		IFileStore fileStore = EFS.getStore(file.toURI());

		FileStoreEditorInput input = new FileStoreEditorInput(fileStore);
		editor = (AbstractThemeableEditor) IDE.openEditor(UIUtils.getActivePage(), input, "com.aptana.editor.css");

		return editor.getISourceViewer();
	}

	protected void assertHover(String source, int hoverOffset, int regionOffset, int regionLength, Object hover)
			throws Exception
	{
		ITextViewer textViewer = getTextViewer(source);

		IRegion hoverRegion = fHover.getHoverRegion(textViewer, hoverOffset);
		assertNotNull("Hover region was null", hoverRegion);
		assertEquals("Hover offset doesn't match", regionOffset, hoverRegion.getOffset());
		assertEquals("Hover length doesn't match", regionLength, hoverRegion.getLength());

		// Ask for hover so we can grab the underlying object...
		fHover.getHoverInfo2(textViewer, hoverRegion);

		assertNotNull("Info was null", hoverObject);
		assertEquals("hover doesn't match", hover, hoverObject);
	}

	protected void assertNoHover(String source, int hoverOffset) throws Exception
	{
		ITextViewer textViewer = getTextViewer(source);

		IRegion hoverRegion = fHover.getHoverRegion(textViewer, hoverOffset);
		assertNull("Got a hover region when we didn't expect one", hoverRegion);
	}

	@Test
	public void testElement() throws Exception
	{
		// CSSElementSelectorHover in HTML plugin handles these
		assertNoHover("div { background: green; }", 1);
	}

	@Test
	public void testProperty() throws Exception
	{
		assertHover("div { background: green; }", 7, 6, 10, queryHelper.getProperty("background"));
	}

	@Test
	public void testNamedColor() throws Exception
	{
		assertHover("div { background: green; }", 19, 18, 5, new RGB(0, 128, 0));
	}

	@Test
	public void testRGBFunction() throws Exception
	{
		assertHover("div { background: rgb(128,128,128); }", 19, 18, 16, new RGB(128, 128, 128));
	}

	@Test
	public void testHexColor() throws Exception
	{
		assertHover("div { background: #888; }", 19, 18, 4, new RGB(136, 136, 136));
	}

	@Test
	public void testHexColor2() throws Exception
	{
		assertHover("div { background: #818283; }", 19, 18, 7, new RGB(129, 130, 131));
	}

	@Test
	public void testPseudoElementAfter() throws Exception
	{
		assertHover("q:after { content: \"#\"; }", 4, 1, 6, queryHelper.getPseudoElement("after"));
	}

	@Test
	public void testPseudoElementAfterWithDoubleColon() throws Exception
	{
		assertHover("q::after { content: \"#\"; }", 5, 1, 7, queryHelper.getPseudoElement("after"));
	}

	@Test
	public void testPseudoClassVisited() throws Exception
	{
		assertHover("a:visited { color: blue; }", 5, 1, 8, queryHelper.getPseudoClass("visited"));
	}

	@Test
	public void testPseudoClassNthChild2nPlus1() throws Exception
	{
		assertHover("tr:nth-child(2n+1) { color: blue; }", 5, 3, 9, queryHelper.getPseudoClass("nth-child"));
	}

	@Test
	public void testPseudoClassNthChildEven() throws Exception
	{
		assertHover("tr:nth-child(even) { color: blue; }", 5, 3, 9, queryHelper.getPseudoClass("nth-child"));
	}

	@Test
	public void testPseudoClassNthChildOdd() throws Exception
	{
		assertHover("tr:nth-child(odd) { color: blue; }", 5, 3, 9, queryHelper.getPseudoClass("nth-child"));
	}
}
