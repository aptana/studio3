/*******************************************************************************
 * Copyright (c) 2006, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Christian Plesner Hansen (plesner@quenta.org) - initial API and implementation
 *     Chris Williams (Aptana)
 *******************************************************************************/
package com.aptana.editor.common.internal.peer;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.text.rules.CompositePartitionScanner;
import com.aptana.scope.IScopeSelector;
import com.aptana.scope.ScopeSelector;

/**
 * This is a modified version of DefaultCharacterPairMatcher from Eclipse. This version adds some heuristics to try and
 * properly handle character pairs where the start and end chars are the same (like '', "", ``) by looking at the
 * partition transitions on the character. If the partition is the same on the character and before it, we assume that
 * this is the end of the pair. This works so long as the same character pairs do mark partition transitions (like
 * strings).
 * 
 * @author cwilliams
 */
public class CharacterPairMatcher implements ICharacterPairMatcher
{
	private static final IScopeSelector fgCommentSelector = new ScopeSelector("comment"); //$NON-NLS-1$

	private int fAnchor = -1;
	private final CharPairs fPairs;
	private final String fPartitioning;

	/**
	 * Avoid looking up scopes and matching scopes all the time by caching if a given partition type is a comment.
	 */
	private static Map<String, Boolean> partitionIsComment = new HashMap<String, Boolean>();

	/**
	 * Creates a new character pair matcher that matches the specified characters within the specified partitioning. The
	 * specified list of characters must have the form <blockquote>{ <i>start</i>, <i>end</i>, <i>start</i>, <i>end</i>,
	 * ..., <i>start</i>, <i>end</i> }</blockquote> For instance:
	 * 
	 * <pre>
	 * char[] chars = new char[] {'(', ')', '{', '}', '[', ']'};
	 * new SimpleCharacterPairMatcher(chars, ...);
	 * </pre>
	 * 
	 * @param chars
	 *            a list of characters
	 * @param partitioning
	 *            the partitioning to match within
	 */
	public CharacterPairMatcher(char[] chars, String partitioning)
	{
		Assert.isLegal(chars.length % 2 == 0);
		Assert.isNotNull(partitioning);
		fPairs = new CharPairs(chars);
		fPartitioning = partitioning;
	}

	/**
	 * Creates a new character pair matcher that matches characters within the default partitioning. The specified list
	 * of characters must have the form <blockquote>{ <i>start</i>, <i>end</i>, <i>start</i>, <i>end</i>, ...,
	 * <i>start</i>, <i>end</i> }</blockquote> For instance:
	 * 
	 * <pre>
	 * char[] chars = new char[] { '(', ')', '{', '}', '[', ']' };
	 * new SimpleCharacterPairMatcher(chars);
	 * </pre>
	 * 
	 * @param chars
	 *            a list of characters
	 */
	public CharacterPairMatcher(char[] chars)
	{
		this(chars, IDocumentExtension3.DEFAULT_PARTITIONING);
	}

	/* @see ICharacterPairMatcher#getAnchor() */
	public int getAnchor()
	{
		return fAnchor;
	}

	/* @see ICharacterPairMatcher#dispose() */
	public void dispose()
	{
	}

	/* @see ICharacterPairMatcher#clear() */
	public void clear()
	{
		fAnchor = -1;
	}

	/* @see ICharacterPairMatcher#match(IDocument, int) */
	public IRegion match(IDocument doc, int offset)
	{
		if (doc == null || offset < 0 || offset > doc.getLength())
		{
			return null;
		}
		try
		{
			return performMatch(doc, offset);
		}
		catch (BadLocationException ble)
		{
			return null;
		}
	}

	/*
	 * Performs the actual work of matching for #match(IDocument, int).
	 */
	private IRegion performMatch(IDocument doc, int caretOffset) throws BadLocationException
	{
		int charOffset = Math.max(caretOffset - 1, 0);
		char prevChar = doc.getChar(charOffset);
		if (!fPairs.contains(prevChar))
		{
			// Now try to right of caret
			charOffset = caretOffset;
			caretOffset += 1;
			if (charOffset >= doc.getLength())
			{
				return null;
			}
			prevChar = doc.getChar(charOffset);
			if (!fPairs.contains(prevChar))
			{
				return null;
			}
		}

		ITypedRegion partition = getPartition(doc, charOffset);
		// FIXME if we're inside a string or comment, we should limit our search to just this particular partition!
		// Drop out if the char is inside a comment
		if (isComment(doc, partition))
		{
			return null;
		}

		boolean isForward = fPairs.isStartCharacter(prevChar);
		String contentType = partition.getType();
		if (fPairs.isAmbiguous(prevChar))
		{
			// If this is common start tag, look forward, if common end tag look backwards!
			if (CompositePartitionScanner.START_SWITCH_TAG.equals(contentType))
			{
				isForward = true;
			}
			else if (CompositePartitionScanner.END_SWITCH_TAG.equals(contentType))
			{
				isForward = false;
			}
			else
			{
				// Need to look at partition transition to tell if we're at end or beginning!
				String partitionAhead = TextUtilities.getContentType(doc, fPartitioning, charOffset + 1, false);
				String partitionBehind = TextUtilities.getContentType(doc, fPartitioning, charOffset - 1, false);
				if (contentType.equals(partitionBehind) && !contentType.equals(partitionAhead))
				{
					// End because we're transitioning out of a partition on this character
					isForward = false;
				}
				else if (isUnclosedPair(prevChar, doc, charOffset))
				{
					isForward = false;
				}
			}
		}
		fAnchor = isForward ? ICharacterPairMatcher.LEFT : ICharacterPairMatcher.RIGHT;
		int searchStartPosition = isForward ? charOffset + 1 : charOffset - 1;
		char endChar = fPairs.getMatching(prevChar);

		int endOffset = -1;
		if (isForward)
		{
			endOffset = searchForward(doc, searchStartPosition, prevChar, endChar, contentType);
		}
		else
		{
			endOffset = searchBackwards(doc, searchStartPosition, prevChar, endChar, contentType);
		}

		if (endOffset == -1)
		{
			return null;
		}
		final int adjustedOffset = isForward ? charOffset : caretOffset;
		final int adjustedEndOffset = isForward ? endOffset + 1 : endOffset;
		if (adjustedEndOffset == adjustedOffset)
		{
			return null;
		}
		return new Region(Math.min(adjustedOffset, adjustedEndOffset), Math.abs(adjustedEndOffset - adjustedOffset));
	}

	protected ITypedRegion getPartition(IDocument doc, int charOffset) throws BadLocationException
	{
		return TextUtilities.getPartition(doc, fPartitioning, charOffset, false);
	}

	private int searchBackwards(IDocument doc, int searchStartPosition, char startChar, char endChar,
			String partitionType) throws BadLocationException
	{
		int stack = 0;
		ITypedRegion[] partitions = computePartitioning(doc, 0, searchStartPosition + 1);
		// reverse the partitions
		partitions = reverse(partitions);
		for (ITypedRegion p : partitions)
		{
			// skip other partitions that don't match our source partition
			if (skipPartition(p.getType(), partitionType))
			{
				continue;
			}
			int partitionOffset = p.getOffset();
			int partitionEnd = partitionOffset + p.getLength() - 1;
			int startOffset = Math.min(searchStartPosition, partitionEnd);
			int length = startOffset - partitionOffset;
			String contents = doc.get(partitionOffset, length + 1);
			// Now search backwards through the partition for the end char
			for (int i = length; i >= 0; i--)
			{
				char c = contents.charAt(i);
				if (c == endChar) // found end char!
				{
					if (stack == 0)
					{
						return partitionOffset + i;
					}
					else
					{
						stack--;
					}
				}
				else if (c == startChar)
				{
					stack++;
				}
			}
		}
		return -1;
	}

	private ITypedRegion[] reverse(ITypedRegion[] array)
	{
		List<ITypedRegion> list = Arrays.asList(array);
		Collections.reverse(list);
		return list.toArray(new ITypedRegion[list.size()]);
	}
	
	
	protected ITypedRegion[] computePartitioning(IDocument doc, int offset, int length) throws BadLocationException
	{
		return doc.computePartitioning(offset, length);
	}

	private int searchForward(IDocument doc, int searchStartPosition, char startChar, char endChar,
			String startPartition) throws BadLocationException
	{
		int stack = 0;
		ITypedRegion[] partitions = computePartitioning(doc, searchStartPosition, doc.getLength() - searchStartPosition);
		for (ITypedRegion p : partitions)
		{
			// skip other partitions that don't match our source partition
			if (skipPartition(p.getType(), startPartition))
			{
				continue;
			}
			// Now search through the partition for the end char
			int partitionLength = p.getLength();
			int partitionEnd = p.getOffset() + partitionLength;
			int startOffset = Math.max(searchStartPosition, p.getOffset());
			int length = partitionEnd - startOffset;
			String partitionContents = doc.get(startOffset, length);
			for (int i = 0; i < length; i++)
			{
				char c = partitionContents.charAt(i);
				if (c == endChar)
				{
					if (stack == 0)
					{
						// it's a match
						return i + startOffset;
					}
					else
					{
						// need to close nested pair
						stack--;
					}
				}
				else if (c == startChar)
				{
					// open nested pair
					stack++;
				}
			}
		}
		return -1;
	}

	private boolean areSwitchPartitions(String partition1, String partition2)
	{
		return (CompositePartitionScanner.START_SWITCH_TAG.equals(partition1) || CompositePartitionScanner.END_SWITCH_TAG
				.equals(partition1))
				&& (CompositePartitionScanner.START_SWITCH_TAG.equals(partition2) || CompositePartitionScanner.END_SWITCH_TAG
						.equals(partition2));
	}

	private boolean skipPartition(String toCheck, String originalPartition)
	{
		if (toCheck == null)
		{
			return true;
		}
		// don't skip same partition
		if (toCheck.equals(originalPartition))
		{
			return false;
		}
		// If they're both language switch partitions, don't skip.
		return !areSwitchPartitions(toCheck, originalPartition);
	}

	protected String getScopeAtOffset(IDocument doc, int charOffset) throws BadLocationException
	{
		return CommonEditorPlugin.getDefault().getDocumentScopeManager().getScopeAtOffset(doc, charOffset);
	}

	private boolean isUnclosedPair(char c, IDocument document, int offset) throws BadLocationException
	{
		// TODO Refactor and combine this copy-pasted code from PeerCharacterCloser
		int beginning = 0;
		// Don't check from very beginning of the document! Be smarter/quicker and check from beginning of
		// partition if we can
		if (document instanceof IDocumentExtension3)
		{
			try
			{
				IDocumentExtension3 ext = (IDocumentExtension3) document;
				ITypedRegion region = ext.getPartition(IDocumentExtension3.DEFAULT_PARTITIONING, offset, false);
				beginning = region.getOffset();
			}
			catch (BadPartitioningException e)
			{
				// ignore
			}
		}
		// Now check leading source and see if we're an unclosed pair.
		String previous = document.get(beginning, offset - beginning);
		boolean open = false;
		int index = -1;
		while ((index = previous.indexOf(c, index + 1)) != -1)
		{
			open = !open;
		}
		return open;
	}

	protected boolean isComment(IDocument doc, ITypedRegion partition) throws BadLocationException
	{
		if (partitionIsComment.containsKey(partition.getType()))
		{
			return partitionIsComment.get(partition.getType());
		}
		String scope = getScopeAtOffset(doc, partition.getOffset());
		boolean isComment = fgCommentSelector.matches(scope);
		partitionIsComment.put(partition.getType(), isComment);
		return isComment;
	}

	/**
	 * Utility class that encapsulates access to matching character pairs.
	 */
	private static class CharPairs
	{

		private final char[] fPairs;

		public CharPairs(char[] pairs)
		{
			fPairs = pairs;
		}

		/**
		 * Returns true if the specified character pair occurs in one of the character pairs.
		 * 
		 * @param c
		 *            a character
		 * @return true exactly if the character occurs in one of the pairs
		 */
		public boolean contains(char c)
		{
			return getAllCharacters().contains(Character.valueOf(c));
		}

		private Set<Character> fCharsCache = null;

		/**
		 * @return A set containing all characters occurring in character pairs.
		 */
		private Set<Character> getAllCharacters()
		{
			if (fCharsCache == null)
			{
				Set<Character> set = new HashSet<Character>();
				for (char pair : fPairs)
				{
					set.add(Character.valueOf(pair));
				}
				fCharsCache = set;
			}
			return fCharsCache;
		}

		/**
		 * Returns true if the specified character opens a character pair when scanning in the specified direction.
		 * 
		 * @param c
		 *            a character
		 * @param searchForward
		 *            the direction of the search
		 * @return whether or not the character opens a character pair
		 */
		public boolean isOpeningCharacter(char c, boolean searchForward)
		{
			for (int i = 0; i < fPairs.length; i += 2)
			{
				if (searchForward && getStartChar(i) == c)
				{
					return true;
				}
				else if (!searchForward && getEndChar(i) == c)
				{
					return true;
				}
			}
			return false;
		}

		/**
		 * Returns true of the specified character is a start character.
		 * 
		 * @param c
		 *            a character
		 * @return true exactly if the character is a start character
		 */
		public boolean isStartCharacter(char c)
		{
			return this.isOpeningCharacter(c, true);
		}

		/**
		 * Determine if the pair's start and end char are the same!
		 * 
		 * @param c
		 * @return
		 */
		public boolean isAmbiguous(char c)
		{
			if (!isStartCharacter(c))
				return false;

			for (int i = 1; i < fPairs.length; i += 2)
			{
				if (fPairs[i] == c)
					return true;
			}
			return false;
		}

		/**
		 * Returns the matching character for the specified character.
		 * 
		 * @param c
		 *            a character occurring in a character pair
		 * @return the matching character
		 */
		public char getMatching(char c)
		{
			for (int i = 0; i < fPairs.length; i += 2)
			{
				if (getStartChar(i) == c)
				{
					return getEndChar(i);
				}
				else if (getEndChar(i) == c)
				{
					return getStartChar(i);
				}
			}
			Assert.isTrue(false);
			return '\0';
		}

		private char getStartChar(int i)
		{
			return fPairs[i];
		}

		private char getEndChar(int i)
		{
			return fPairs[i + 1];
		}

	}
}
