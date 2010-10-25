package com.aptana.editor.erb.html;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

import com.aptana.editor.common.internal.peer.CharacterPairMatcher;

public class CharacterPairMatcherTest extends TestCase
{

	private static final char[] pairs = new char[] { '%', '%', '<', '>', '(', ')', '{', '}', '[', ']', '`', '`', '\'',
			'\'', '"', '"' };
	private ICharacterPairMatcher matcher;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		matcher = new CharacterPairMatcher(pairs);
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

	public void testERB() throws Exception
	{
		String source = "<% @var %>";
		IDocument document = new Document(source);
		RHTMLDocumentProvider provider = new RHTMLDocumentProvider()
		{
			@Override
			public IDocument getDocument(Object element)
			{
				return (IDocument) element;
			}
		};
		provider.connect(document);
		assertMatch(document, 0, 9);
		assertMatch(document, 1, 8);
	}

	private void assertMatch(IDocument document, int i, int j)
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
