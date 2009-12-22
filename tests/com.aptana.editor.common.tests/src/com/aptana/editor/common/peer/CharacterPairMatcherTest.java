package com.aptana.editor.common.peer;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

public class CharacterPairMatcherTest extends TestCase
{
	private ICharacterPairMatcher matcher;

	@Override
	protected void setUp() throws Exception
	{
		char[] pairs = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'', '\'', '"', '"' };
		matcher = new CharacterPairMatcher(pairs);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception
	{
		if (matcher != null)
		{
			matcher.dispose();
		}
		matcher = null;
		super.tearDown();
	}

	public void testPairMatching()
	{
		String source = "( { [ `ruby command`, 'single quoted string', \"double quoted string\" ] } )";
		IDocument document = new Document(source);
		assertMatch(document, source, 0); // ()
		assertMatch(document, source, 2); // {}
		assertMatch(document, source, 4); // []
		assertMatch(document, source, 6, 19); // ``
		assertMatch(document, source, 22, 43); // ''
		assertMatch(document, source, 46, 67); // ""
	}

	public void testPairMatching2()
	{
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>";
		IDocument document = new Document(source);
		assertMatch(document, source, 14, 18);
		assertMatch(document, source, 30, 41);
	}

	private void assertMatch(IDocument document, String source, int i)
	{
		int j = source.length() - i - 1;
		assertMatch(document, source, i, j);
	}

	private void assertMatch(IDocument document, String source, int i, int j)
	{
		int length = (j - i) + 1;
		// left
		IRegion region = matcher.match(document, i + 1);
		assertNotNull(region);
		assertEquals("offset", i, region.getOffset());
		assertEquals("length", length, region.getLength());
		assertEquals(ICharacterPairMatcher.LEFT, matcher.getAnchor());
		// right
		region = matcher.match(document, j + 1);
		assertNotNull(region);
		assertEquals("offset", i, region.getOffset());
		assertEquals("length", length, region.getLength());
		assertEquals(ICharacterPairMatcher.RIGHT, matcher.getAnchor());
	}
}
