package com.aptana.editor.coffee;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;

import com.aptana.editor.common.tests.TextViewer;

public class CoffeeDoubleClickStrategyTest
{

	private CoffeeDoubleClickStrategy strategy;

	@Before
	public void setUp() throws Exception
	{
//		super.setUp();
		strategy = new CoffeeDoubleClickStrategy();
	}

	@After
	public void tearDown() throws Exception
	{
		strategy = null;
//		super.tearDown();
	}

	@Test
	public void testSimpleIdentifier() throws Exception
	{
		String src = "singers = {Jagger: \"Rock\", Elvis: \"Roll\"}";
		IDocument document = new Document(src);
		ITextViewer textViewer = new TextViewer(document);

		textViewer.setSelectedRange(0, 0);
		strategy.doubleClicked(textViewer);
		assertEquals(0, textViewer.getSelectedRange().x);
		assertEquals(7, textViewer.getSelectedRange().y);
	}

	@Test
	public void testHashKey() throws Exception
	{
		String src = "singers = {Jagger: \"Rock\", Elvis: \"Roll\"}";
		IDocument document = new Document(src);
		ITextViewer textViewer = new TextViewer(document);

		// TODO Do we want it to include the trailing colon too?

		textViewer.setSelectedRange(12, 1);
		strategy.doubleClicked(textViewer);
		assertEquals(11, textViewer.getSelectedRange().x);
		assertEquals(6, textViewer.getSelectedRange().y);
	}

	@Test
	public void testDoesntPickUpParens() throws Exception
	{
		String src = "countdown = (num for num in [10..1])";
		IDocument document = new Document(src);
		ITextViewer textViewer = new TextViewer(document);

		// ('n'um
		textViewer.setSelectedRange(13, 0);
		strategy.doubleClicked(textViewer);
		assertEquals(13, textViewer.getSelectedRange().x);
		assertEquals(3, textViewer.getSelectedRange().y);

		// ['1'0
		textViewer.setSelectedRange(29, 0);
		strategy.doubleClicked(textViewer);
		assertEquals(29, textViewer.getSelectedRange().x);
		assertEquals(2, textViewer.getSelectedRange().y);
	}

}
