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

import java.util.HashSet;
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
			return null;
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

		String currentScope = getScopeAtOffset(doc, charOffset);
		// FIXME if we're inside a string or comment, we should limit our search to just this particular partition!
		// Drop out if the char is inside a comment
		if (fgCommentSelector.matches(currentScope))
		{
			return null;
		}

		boolean isForward = fPairs.isStartCharacter(prevChar);
		final String partition = TextUtilities.getContentType(doc, fPartitioning, charOffset, false);
		if (fPairs.isAmbiguous(prevChar))
		{
			// If this is common start tag, look forward, if common end tag look backwards!
			if (partition.equals(CompositePartitionScanner.START_SWITCH_TAG))
			{
				isForward = true;
			}
			else if (partition.equals(CompositePartitionScanner.END_SWITCH_TAG))
			{
				isForward = false;
			}
			else
			{
				// Need to look at partition transition to tell if we're at end or beginning!
				String partitionAhead = TextUtilities.getContentType(doc, fPartitioning, charOffset + 1, false);
				String partitionBehind = TextUtilities.getContentType(doc, fPartitioning, charOffset - 1, false);
				if (partition.equals(partitionBehind) && !partition.equals(partitionAhead))
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
		final int searchStartPosition = isForward ? charOffset + 1 : caretOffset - 2;
		final int adjustedOffset = isForward ? charOffset : caretOffset;

		final DocumentPartitionAccessor partDoc = new DocumentPartitionAccessor(doc, fPartitioning, partition);
		int endOffset = findMatchingPeer(partDoc, prevChar, fPairs.getMatching(prevChar), isForward,
				isForward ? doc.getLength() : -1, searchStartPosition);
		if (endOffset == -1)
			return null;
		final int adjustedEndOffset = isForward ? endOffset + 1 : endOffset;
		if (adjustedEndOffset == adjustedOffset)
			return null;
		return new Region(Math.min(adjustedOffset, adjustedEndOffset), Math.abs(adjustedEndOffset - adjustedOffset));
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

	/**
	 * Searches <code>doc</code> for the specified end character, <code>end</code>.
	 * 
	 * @param doc
	 *            the document to search
	 * @param start
	 *            the opening matching character
	 * @param end
	 *            the end character to search for
	 * @param searchForward
	 *            search forwards or backwards?
	 * @param boundary
	 *            a boundary at which the search should stop
	 * @param startPos
	 *            the start offset
	 * @return the index of the end character if it was found, otherwise -1
	 * @throws BadLocationException
	 *             if the document is accessed with invalid offset or line
	 */
	private int findMatchingPeer(DocumentPartitionAccessor doc, char start, char end, boolean searchForward,
			int boundary, int startPos) throws BadLocationException
	{
		int pos = startPos;
		while (pos != boundary)
		{
			final char c = doc.getChar(pos);
			if (doc.isMatch(pos, end) && !fgCommentSelector.matches(getScopeAtOffset(doc.fDocument, pos)))
			{
				return pos;
			}
			else if (c == start && doc.inPartition(pos))
			{
				pos = findMatchingPeer(doc, start, end, searchForward, boundary,
						doc.getNextPosition(pos, searchForward));
				if (pos == -1)
					return -1;
			}
			pos = doc.getNextPosition(pos, searchForward);
		}
		return -1;
	}

	/**
	 * Utility class that wraps a document and gives access to partitioning information. A document is tied to a
	 * particular partition and, when considering whether or not a position is a valid match, only considers position
	 * within its partition.
	 */
	private static class DocumentPartitionAccessor
	{

		private final IDocument fDocument;
		private final String fPartitioning, fPartition;
		private ITypedRegion fCachedPartition;

		/**
		 * Creates a new partitioned document for the specified document.
		 * 
		 * @param doc
		 *            the document to wrap
		 * @param partitioning
		 *            the partitioning used
		 * @param partition
		 *            the partition managed by this document
		 */
		public DocumentPartitionAccessor(IDocument doc, String partitioning, String partition)
		{
			fDocument = doc;
			fPartitioning = partitioning;
			fPartition = partition;
		}

		/**
		 * Returns the character at the specified position in this document.
		 * 
		 * @param pos
		 *            an offset within this document
		 * @return the character at the offset
		 * @throws BadLocationException
		 *             if the offset is invalid in this document
		 */
		public char getChar(int pos) throws BadLocationException
		{
			return fDocument.getChar(pos);
		}

		/**
		 * Returns true if the character at the specified position is a valid match for the specified end character. To
		 * be a valid match, it must be in the appropriate partition and equal to the end character.
		 * 
		 * @param pos
		 *            an offset within this document
		 * @param end
		 *            the end character to match against
		 * @return true exactly if the position represents a valid match
		 * @throws BadLocationException
		 *             if the offset is invalid in this document
		 */
		public boolean isMatch(int pos, char end) throws BadLocationException
		{
			return getChar(pos) == end && inPartition(pos);
		}

		/**
		 * Returns true if the specified offset is within the partition managed by this document.
		 * 
		 * @param pos
		 *            an offset within this document
		 * @return true if the offset is within this document's partition
		 */
		public boolean inPartition(int pos)
		{
			final ITypedRegion partition = getPartition(pos);
			return samePartitions(partition);
		}

		private boolean samePartitions(ITypedRegion partition)
		{
			return partition != null
					&& (partition.getType().equals(fPartition) || areSwitchPartitions(fPartition, partition.getType()));
		}

		/**
		 * Returns the next position to query in the search. The position is not guaranteed to be in this document's
		 * partition.
		 * 
		 * @param pos
		 *            an offset within the document
		 * @param searchForward
		 *            the direction of the search
		 * @return the next position to query
		 */
		public int getNextPosition(int pos, boolean searchForward)
		{
			final ITypedRegion partition = getPartition(pos);
			if (partition == null || samePartitions(partition))
			{
				return simpleIncrement(pos, searchForward);
			}
			if (searchForward)
			{
				int end = partition.getOffset() + partition.getLength();
				if (pos < end)
					return end;
			}
			else
			{
				int offset = partition.getOffset();
				if (pos > offset)
					return offset - 1;
			}
			return simpleIncrement(pos, searchForward);
		}

		private boolean areSwitchPartitions(String partition1, String partition2)
		{
			return (partition1.equals(CompositePartitionScanner.START_SWITCH_TAG) || partition1
					.equals(CompositePartitionScanner.END_SWITCH_TAG))
					&& (partition2.equals(CompositePartitionScanner.START_SWITCH_TAG) || partition2
							.equals(CompositePartitionScanner.END_SWITCH_TAG));
		}

		private int simpleIncrement(int pos, boolean searchForward)
		{
			return pos + (searchForward ? 1 : -1);
		}

		/**
		 * Returns partition information about the region containing the specified position.
		 * 
		 * @param pos
		 *            a position within this document.
		 * @return positioning information about the region containing the position
		 */
		private ITypedRegion getPartition(int pos)
		{
			if (fCachedPartition == null || !contains(fCachedPartition, pos))
			{
				Assert.isTrue(pos >= 0 && pos <= fDocument.getLength());
				try
				{
					fCachedPartition = TextUtilities.getPartition(fDocument, fPartitioning, pos, false);
				}
				catch (BadLocationException e)
				{
					fCachedPartition = null;
				}
			}
			return fCachedPartition;
		}

		private static boolean contains(IRegion region, int pos)
		{
			int offset = region.getOffset();
			return offset <= pos && pos < offset + region.getLength();
		}

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
			return getAllCharacters().contains(new Character(c));
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
				for (int i = 0; i < fPairs.length; i++)
					set.add(new Character(fPairs[i]));
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
					return true;
				else if (!searchForward && getEndChar(i) == c)
					return true;
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
					return getEndChar(i);
				else if (getEndChar(i) == c)
					return getStartChar(i);
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
