package com.aptana.editor.xml;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

import com.aptana.editor.xml.tests.XMLTestUtil;

public class TagUtilTest
{
	/**
	 * createDocument
	 * 
	 * @param partitionType
	 * @param source
	 * @return
	 */
	protected IDocument createDocument(String source, boolean stripCursor)
	{
		return XMLTestUtil.createDocument(source, stripCursor);
	}

	@Test
	public void testTagClosed()
	{
		IDocument document = createDocument("<a>Test</a> <b>Item</b>", false); //$NON-NLS-1$
		assertTrue(TagUtil.tagClosed(document, "a"));

		document = createDocument("<a>Test <b>Item</b>", false); //$NON-NLS-1$
		assertFalse(TagUtil.tagClosed(document, "a"));
	}

	@Test
	public void testFindMatchingTag() throws BadLocationException
	{
		String source = "<a>Test</a> <b /> <c><d>ItemM</d></c>"; //$NON-NLS-1$
		IDocument document = createDocument(source, false);

		Collection<String> tagPartitions = new ArrayList<String>();
		tagPartitions.add(XMLSourceConfiguration.TAG);

		// Looking for </a>
		IRegion region = TagUtil.findMatchingTag(document, 1, tagPartitions);
		assertEquals(7, region.getOffset());
		assertEquals(4, region.getLength());

		// Looking for <a>
		region = TagUtil.findMatchingTag(document, 7, tagPartitions);
		assertEquals(0, region.getOffset());
		assertEquals(3, region.getLength());

		// Looking for </b>, but does not exist
		region = TagUtil.findMatchingTag(document, 14, tagPartitions);
		assertNull(region);

		// Looking for </c>
		region = TagUtil.findMatchingTag(document, 19, tagPartitions);
		assertEquals(33, region.getOffset());
		assertEquals(4, region.getLength());

		// Looking for <d>
		region = TagUtil.findMatchingTag(document, 30, tagPartitions);
		assertEquals(21, region.getOffset());
		assertEquals(3, region.getLength());

	}

	@Test
	public void testGetTagName()
	{
		assertEquals("span", TagUtil.getTagName("<span id=\"test\">"));
		assertEquals("span", TagUtil.getTagName("</span>"));
		assertEquals("span", TagUtil.getTagName("span"));
		assertEquals("br", TagUtil.getTagName("<br />"));
	}

	@Test
	public void testIsStartTag()
	{
		assertTrue(TagUtil.isStartTag("<span>"));
		assertFalse(TagUtil.isStartTag("</span>"));
	}

	@Test
	public void testIsEndTag()
	{
		assertTrue(TagUtil.isEndTag("</span>"));
		assertFalse(TagUtil.isEndTag("<span>"));
	}

}
