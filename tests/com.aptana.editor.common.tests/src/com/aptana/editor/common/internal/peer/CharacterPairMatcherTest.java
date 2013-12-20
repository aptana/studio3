/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.internal.peer;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

public class CharacterPairMatcherTest
{
	private static final char[] pairs = new char[] { '(', ')', '{', '}', '[', ']', '`', '`', '\'', '\'', '"', '"', '<',
			'>' };
	private ICharacterPairMatcher matcher;

//	@Override
	@Before
	public void setUp() throws Exception
	{
		matcher = new CharacterPairMatcher(pairs)
		{
			@Override
			protected ITypedRegion getPartition(IDocument doc, int charOffset) throws BadLocationException
			{
				ITypedRegion[] partitions = computePartitioning(doc, charOffset, 1);
				for (ITypedRegion region : partitions)
				{
					if (charOffset >= region.getOffset() && charOffset < (region.getOffset() + region.getLength()))
					{
						return region;
					}
				}
				return null;
			}

			@Override
			protected ITypedRegion[] computePartitioning(IDocument doc, int offset, int length)
					throws BadLocationException
			{
				return new ITypedRegion[] { new TypedRegion(0, doc.getLength(), IDocument.DEFAULT_CONTENT_TYPE) };
			}
		};
//		super.setUp();
	}

//	@Override
	@After
	public void tearDown() throws Exception
	{
		if (matcher != null)
		{
			matcher.dispose();
		}
		matcher = null;
//		super.tearDown();
	}

	@Test
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

	@Test
	public void testPairMatching2()
	{
		String source = "<?xml version=\"1.0\"\n encoding=\"ISO-8859-1\"?>";
		IDocument document = new Document(source);
		assertMatch(document, source, 0);
		assertMatch(document, source, 14, 18);
		assertMatch(document, source, 30, 41);
	}

	@Test
	public void testMatchesCharToRightIfNothingOnLeft()
	{
		String source = "( )";
		IDocument document = new Document(source);
		assertRawMatch(document, 0, 2, 0, 3);
	}

	@Test
	public void testMatchesCharToRightIfNothingOnLeft2()
	{
		String source = "( { [ `ruby command`, 'single quoted string', \"double quoted string\" ] } )";
		IDocument document = new Document(source);
		assertRawMatch(document, 0, source.length() - 1, 0, source.length()); // ()
		assertRawMatch(document, 2, source.length() - 3, 2, source.length() - 4); // {}
		assertRawMatch(document, 4, source.length() - 5, 4, source.length() - 8); // []
		assertRawMatch(document, 6, 19, 6, 14); // ``
		assertRawMatch(document, 22, 43, 22, 22); // ''
		assertRawMatch(document, 46, 67, 46, 22); // ""
	}

	@Test
	public void testDoesntPairMatchInComments()
	{
		final String source = "# ( { [ `ruby command`, 'single quoted string', \"double quoted string\" ] } )";
		IDocument document = new Document(source);
		matcher = new CharacterPairMatcher(pairs)
		{
			protected String getScopeAtOffset(IDocument doc, int charOffset) throws BadLocationException
			{
				return "source.ruby comment.line.hash";
			}

			@Override
			protected ITypedRegion getPartition(IDocument doc, int charOffset) throws BadLocationException
			{
				ITypedRegion[] partitions = computePartitioning(doc, charOffset, 1);
				for (ITypedRegion region : partitions)
				{
					if (charOffset >= region.getOffset() && charOffset < (region.getOffset() + region.getLength()))
					{
						return region;
					}
				}
				return null;
			}

			@Override
			protected ITypedRegion[] computePartitioning(IDocument doc, int offset, int length)
					throws BadLocationException
			{
				return new TypedRegion[] { new TypedRegion(0, source.length(), "__rb_singleline_comment") };
			}
		};
		assertNull(matcher.match(document, 2));
		assertNull(matcher.match(document, 4));
		assertNull(matcher.match(document, 6));
		assertNull(matcher.match(document, 8));
		assertNull(matcher.match(document, 24));
		assertNull(matcher.match(document, 48));
	}

	@Test
	public void testAPSTUD3926()
	{
		// --------------------01234567890123456789012345678
		// --------------------__________1_________2
		final String source = "faux_function(str_pos(next));";
		IDocument document = new Document(source);

		// check after last )
		assertRawMatch(document, 13, 28, 13, 15);
		// check middle () pair
		assertMatch(document, source, 21, 26);
		// check between )), it matches the inside pair
		assertRawMatch(document, 21, 27, 21, 6);
	}

	@Test
	public void testAPSTUD3926_2()
	{
		// --------------------0123456789012345678901 2345
		// --------------------__________1_________2______
		final String source = "print(String(\"message\"));";
		IDocument document = new Document(source);
		matcher = new CharacterPairMatcher(pairs)
		{

			private ITypedRegion[] partitions = new TypedRegion[] {
					new TypedRegion(0, 13, IDocument.DEFAULT_CONTENT_TYPE),
					new TypedRegion(13, 9, "__js_string_double"),
					new TypedRegion(22, 3, IDocument.DEFAULT_CONTENT_TYPE) };

			@Override
			protected ITypedRegion getPartition(IDocument doc, int charOffset) throws BadLocationException
			{
				for (ITypedRegion region : partitions)
				{
					if (charOffset >= region.getOffset() && charOffset < (region.getOffset() + region.getLength()))
					{
						return region;
					}
				}
				return null;
			}

			@Override
			protected ITypedRegion[] computePartitioning(IDocument doc, int offset, int length)
					throws BadLocationException
			{
				List<ITypedRegion> matching = new ArrayList<ITypedRegion>();
				for (ITypedRegion region : partitions)
				{
					if (region.getOffset() >= (offset + length))
					{
						continue;
					}
					if (region.getOffset() >= offset)
					{
						matching.add(region);
					}
				}
				return matching.toArray(new ITypedRegion[matching.size()]);
			}
		};

		// check after last )
		assertMatchFromRight(document, 24, 5, 19);
	}

	@Test
	public void testSkipsPairsInComments()
	{
		String source = "(\n# )\n)";
		IDocument document = new Document(source);
		matcher = new CharacterPairMatcher(pairs)
		{
			protected String getScopeAtOffset(IDocument doc, int charOffset) throws BadLocationException
			{
				if (charOffset >= 2 && charOffset <= 5)
					return "source.ruby comment.line.hash";
				return "source.ruby";
			}

			@Override
			protected ITypedRegion getPartition(IDocument doc, int charOffset) throws BadLocationException
			{
				ITypedRegion[] partitions = computePartitioning(doc, charOffset, 1);
				for (ITypedRegion region : partitions)
				{
					if (charOffset >= region.getOffset() && charOffset < (region.getOffset() + region.getLength()))
					{
						return region;
					}
				}
				return null;
			}

			@Override
			protected ITypedRegion[] computePartitioning(IDocument doc, int offset, int length)
					throws BadLocationException
			{
				return new TypedRegion[] { new TypedRegion(0, 2, "__rb__dftl_partition_content_type"),
						new TypedRegion(2, 4, "__rb_singleline_comment"),
						new TypedRegion(6, 1, "__rb__dftl_partition_content_type") };
			}
		};
		IRegion region = matcher.match(document, 0);
		assertNotNull(region);
		assertEquals("offset", 0, region.getOffset());
		assertEquals("length", 7, region.getLength());
		assertEquals(ICharacterPairMatcher.LEFT, matcher.getAnchor());
		assertNull(matcher.match(document, 4));
	}

	/**
	 * Assumes a symmetrical document where the offset given is the offset from the doc start to the left side of the
	 * pair, and doc.length - 1 - offset is the right side of the pair.
	 * 
	 * @param document
	 * @param source
	 * @param offset
	 */
	private void assertMatch(IDocument document, String source, int offset)
	{
		int j = source.length() - offset - 1;
		assertMatch(document, source, offset, j);
	}

	private void assertMatch(IDocument document, String source, int leftPairOffset, int rightPairOffset)
	{
		int length = (rightPairOffset - leftPairOffset) + 1;
		assertRawMatch(document, leftPairOffset, rightPairOffset, leftPairOffset, length);
	}

	private void assertRawMatch(IDocument document, int leftOffsetToMatch, int rightOffsetToMatch, int offset,
			int length)
	{
		assertMatchFromLeft(document, leftOffsetToMatch, offset, length);
		assertMatchFromRight(document, rightOffsetToMatch, offset, length);
	}

	protected void assertMatchFromLeft(IDocument document, int leftOffsetToMatch, int offset, int length)
	{
		IRegion region = matcher.match(document, leftOffsetToMatch);
		assertNotNull("Failed to match forwards from left side of pair", region);
		assertEquals("offset", offset, region.getOffset());
		assertEquals("length", length, region.getLength());
		assertEquals(ICharacterPairMatcher.LEFT, matcher.getAnchor());
	}

	protected void assertMatchFromRight(IDocument document, int rightOffsetToMatch, int offset, int length)
	{
		IRegion region = matcher.match(document, rightOffsetToMatch);
		assertNotNull("Failed to match backwards from right side of pair", region);
		assertEquals("offset of left side of pair (from right) doesn't match", offset, region.getOffset());
		assertEquals("length matching backwards doesn't match", length, region.getLength());
		assertEquals(ICharacterPairMatcher.RIGHT, matcher.getAnchor());
	}
}
