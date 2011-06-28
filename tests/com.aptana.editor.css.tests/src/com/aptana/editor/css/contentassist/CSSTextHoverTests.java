/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.RGB;

import com.aptana.editor.common.tests.TextViewer;
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.text.CSSTextHover;

/**
 * CSSTextHoverTests
 */
public class CSSTextHoverTests extends TestCase
{
	private CSSTextHover fHover;

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
		super.tearDown();

		fHover = null;
	}

	/**
	 * getTextViewer
	 * 
	 * @param source
	 * @return
	 */
	protected ITextViewer getTextViewer(String source)
	{
		IDocument document = new Document(source);

		return new TextViewer(document);
	}

	protected void assertRegionAndInfoType(String source, int hoverOffset, int regionOffset, int regionLength,
			Class<?> infoType)
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
	public void testElement()
	{
		assertRegionAndInfoType("div { background: green; }", 1, 0, 3, String.class);
	}

	/**
	 * testProperty
	 */
	public void testProperty()
	{
		assertRegionAndInfoType("div { background: green; }", 7, 6, 10, String.class);
	}

	/**
	 * testNamedColor
	 */
	public void testNamedColor()
	{
		assertRegionAndInfoType("div { background: green; }", 19, 18, 5, RGB.class);
	}

	/**
	 * testRGBFunction
	 */
	public void testRGBFunction()
	{
		assertRegionAndInfoType("div { background: rgb(128,128,128); }", 19, 18, 16, RGB.class);
	}

	/**
	 * testHexColor
	 */
	public void testHexColor()
	{
		assertRegionAndInfoType("div { background: #888; }", 19, 18, 4, RGB.class);
	}

	/**
	 * testHexColor2
	 */
	public void testHexColor2()
	{
		assertRegionAndInfoType("div { background: #818283; }", 19, 18, 7, RGB.class);
	}
}
