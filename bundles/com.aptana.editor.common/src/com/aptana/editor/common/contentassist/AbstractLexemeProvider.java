/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.Lexeme;

/**
 * AbstractLexemeProvider
 */
public abstract class AbstractLexemeProvider<T, U> implements ILexemeProvider<T>
{
	private List<Lexeme<T>> _lexemes = new ArrayList<Lexeme<T>>();

	/**
	 * Convert the partition that contains the given offset into a list of lexemes.
	 * 
	 * @param document
	 * @param offset
	 * @param scanner
	 */
	protected AbstractLexemeProvider(IDocument document, int offset, U scanner)
	{
		this(document, offset, offset, scanner);
	}

	/**
	 * Convert the partition that contains the given offset into a list of lexemes. If the includeOffset is not within
	 * the partition found at offset, then the range is extended to include it
	 * 
	 * @param document
	 * @param offset
	 * @param includeOffset
	 * @param scanner
	 */
	protected AbstractLexemeProvider(IDocument document, int offset, int includeOffset, U scanner)
	{
		int start = offset;
		int end = offset;

		try
		{
			ITypedRegion partition = document.getPartition(offset);

			start = partition.getOffset();
			end = start + partition.getLength();

			start = Math.max(0, Math.min(start, includeOffset));
			end = Math.min(Math.max(end, includeOffset), document.getLength());
		}
		catch (BadLocationException e)
		{
		}

		this.createLexemeList(document, start, end - start, scanner);
	}

	/**
	 * Convert the specified range of text into a list of lexemes
	 * 
	 * @param document
	 * @param offset
	 * @param length
	 * @param scanner
	 */
	protected AbstractLexemeProvider(IDocument document, IRange range, U scanner)
	{
		this.createLexemeList(document, range.getStartingOffset(), range.getLength(), scanner);
	}

	/**
	 * Add the specified lexeme to the lexeme provider's list. Subclasses can use override this method to filter which
	 * type of lexemes should be added to the list
	 * 
	 * @param lexeme
	 */
	protected void addLexeme(Lexeme<T> lexeme)
	{
		_lexemes.add(lexeme);
	}

	protected abstract void createLexemeList(IDocument document, int offset, int length, U scanner);

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getCeilingLexeme(int)
	 */
	public Lexeme<T> getCeilingLexeme(int offset)
	{
		int index = this.getLexemeCeilingIndex(offset);

		return this.getLexeme(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getFirstLexeme()
	 */
	public Lexeme<T> getFirstLexeme()
	{
		return this.getLexeme(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getFloorLexeme(int)
	 */
	public Lexeme<T> getFloorLexeme(int offset)
	{
		int index = this.getLexemeFloorIndex(offset);

		return this.getLexeme(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getLastLexeme()
	 */
	public Lexeme<T> getLastLexeme()
	{
		return this.getLexeme(this.size() - 1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getLexeme(int)
	 */
	public Lexeme<T> getLexeme(int index)
	{
		Lexeme<T> result = null;

		if (0 <= index && index < this._lexemes.size())
		{
			result = this._lexemes.get(index);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getLexemeCeilingIndex(int)
	 */
	public int getLexemeCeilingIndex(int offset)
	{
		int length = this._lexemes.size();
		int result = -1;

		if (length > 0)
		{
			// find index in our collection
			result = this.getLexemeIndex(offset);

			// see if we're in between lexemes
			if (result < 0)
			{
				// we are in between lexemes, so find the lexeme index to our right
				result = -(result + 1);

				// make sure we're in a valid range
				if (result >= length)
				{
					// we're past the end of our list, so return -1
					result = -1;
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getLexemeFloorIndex(int)
	 */
	public int getLexemeFloorIndex(int offset)
	{
		int result = -1;

		if (this._lexemes.size() > 0)
		{
			// find index in our collection
			result = this.getLexemeIndex(offset);

			// see if we're in between lexemes
			if (result < 0)
			{
				// we are in between lexemes, so find the lexeme index to our left
				result = -(result + 1) - 1;

				// make sure we're in a valid range
				if (result < 0)
				{
					// we're before the start of our list, so return -1
					result = -1;
				}
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getLexemeFromOffset(int)
	 */
	public Lexeme<T> getLexemeFromOffset(int offset)
	{
		int index = this.getLexemeIndex(offset);

		return this.getLexeme(index);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#getLexemeIndex(int)
	 */
	public int getLexemeIndex(int offset)
	{
		int low = 0;
		int high = this._lexemes.size() - 1;

		while (low <= high)
		{
			int mid = (low + high) >>> 1;
			Lexeme<T> candidate = this._lexemes.get(mid);

			if (offset < candidate.getStartingOffset())
			{
				high = mid - 1;
			}
			else if (candidate.getEndingOffset() < offset)
			{
				low = mid + 1;
			}
			else
			{
				return mid;
			}
		}

		return -(low + 1);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Lexeme<T>> iterator()
	{
		return this._lexemes.iterator();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.common.contentassist.ILexemeProvider#size()
	 */
	public int size()
	{
		return this._lexemes.size();
	}
}
