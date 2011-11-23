/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import java.io.File;
import java.io.FileWriter;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.text.CSSTextHover;
import com.aptana.ui.util.UIUtils;

/**
 * CSSTextHoverTests
 */
public class CSSTextHoverTests extends TestCase
{
	private CSSTextHover fHover;
	private AbstractThemeableEditor editor;
	private File file;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		fHover = new CSSTextHover();
	}

	/**
	 * @throws InterruptedException
	 */
	private void waitForMetadata()
	{
		CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();

		// try to read metadata up to 10 times before giving up
		for (int count = 0; count < 10; count++)
		{
			ElementElement element = queryHelper.getElement("a");

			// if we got something, then metadata is loaded, so exit loop
			if (element != null)
			{
				break;
			}

			// else wait for 1/2 a second and try again
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			if (editor != null)
			{
				if (editor != null)
				{
					if (Display.getCurrent() != null)
					{
						editor.getSite().getPage().closeEditor(editor, false);
					}
					else
					{
						editor.close(false);
					}
				}
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
			file = null;
			editor = null;
			fHover = null;
			super.tearDown();
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

	protected void assertRegionAndInfoType(String source, int hoverOffset, int regionOffset, int regionLength,
			Class<?> infoType) throws Exception
	{
		ITextViewer textViewer = getTextViewer(source);

		waitForMetadata();

		IRegion hoverRegion = fHover.getHoverRegion(textViewer, hoverOffset);
		assertEquals(regionOffset, hoverRegion.getOffset());
		assertEquals(regionLength, hoverRegion.getLength());

		Object info = fHover.getHoverInfo2(textViewer, hoverRegion);
		assertTrue("info was not " + infoType.getName(), infoType.isAssignableFrom(info.getClass()));
	}

	/**
	 * testElement
	 */
	public void testElement() throws Exception
	{
		assertRegionAndInfoType("div { background: green; }", 1, 0, 3, String.class);
	}

	/**
	 * testProperty
	 */
	public void testProperty() throws Exception
	{
		assertRegionAndInfoType("div { background: green; }", 7, 6, 10, String.class);
	}

	/**
	 * testNamedColor
	 */
	public void testNamedColor() throws Exception
	{
		assertRegionAndInfoType("div { background: green; }", 19, 18, 5, RGB.class);
	}

	/**
	 * testRGBFunction
	 */
	public void testRGBFunction() throws Exception
	{
		assertRegionAndInfoType("div { background: rgb(128,128,128); }", 19, 18, 16, RGB.class);
	}

	/**
	 * testHexColor
	 */
	public void testHexColor() throws Exception
	{
		assertRegionAndInfoType("div { background: #888; }", 19, 18, 4, RGB.class);
	}

	/**
	 * testHexColor2
	 */
	public void testHexColor2() throws Exception
	{
		assertRegionAndInfoType("div { background: #818283; }", 19, 18, 7, RGB.class);
	}
}
