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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.rules.Token;

import com.aptana.parsing.lexer.IRange;
import com.aptana.parsing.lexer.ITypePredicate;
import com.aptana.parsing.lexer.Lexeme;

public abstract class LexemeProvider<T extends ITypePredicate> implements Iterable<Lexeme<T>>
{
	private static final Pattern WHITESPACE = Pattern.compile("\\s+", Pattern.MULTILINE); //$NON-NLS-1$

	private List<Lexeme<T>> _lexemes;

	/**
	 * Convert the partition that contains the given offset into a list of lexemes.
	 * 
	 * @param document
	 * @param offset
	 * @param scanner
	 */
	public LexemeProvider(IDocument document, int offset, ITokenScanner scanner)
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
	public LexemeProvider(IDocument document, int offset, int includeOffset, ITokenScanner scanner)
	{
		int start = offset;
		int end = offset;

		try
		{
			ITypedRegion partition = document.getPartition(offset);

			start = partition.getOffset();
			end = start + partition.getLength();

			start = Math.min(start, includeOffset);
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
	public LexemeProvider(IDocument document, IRange range, ITokenScanner scanner)
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
		this._lexemes.add(lexeme);
	}

	/**
	 * createLexemeList
	 */
	private void createLexemeList(IDocument document, int offset, int length, ITokenScanner scanner)
	{
		this._lexemes = new ArrayList<Lexeme<T>>();

		try
		{
			scanner.setRange(document, offset, length);

			// prime scanner
			IToken token = scanner.nextToken();

			while (token != Token.EOF)
			{
				Object data = token.getData();

				// grab the lexeme particulars
				int tokenOffset = scanner.getTokenOffset();
				int tokenLength = scanner.getTokenLength();
				int endingOffset = tokenOffset + tokenLength;
				String text = document.get(tokenOffset, tokenLength);
				T type = null;
				// skip tokens with null data (typically whitespace)
				if (data != null)
				{
					type = this.getTypeFromData(data);
					Lexeme<T> lexeme = new Lexeme<T>(type, tokenOffset, endingOffset - 1, text);
					// add it to our list
					this.addLexeme(lexeme);
				}

				// NOTE: the following is useful during development to capture any
				// scopes that weren't converted to enumerations
				if (Platform.inDevelopmentMode())
				{
					if (data != null)
					{
						if (type == null || type.isDefined() == false)
						{
							System.out.println("Possible missed token type for text: [" + data + "]~" + text + "~"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
					else
					{
						Matcher m = WHITESPACE.matcher(text);

						if (m.matches() == false)
						{
							System.out.println("Possible missed token type for text: ~" + text + "~"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}

				// advance
				token = scanner.nextToken();
			}
		}
		catch (BadLocationException e)
		{
		}
		catch (IllegalArgumentException e)
		{
		}
	}

	/**
	 * Gets the lexeme at the specified offset. If it is a whitespace character it will return the next (higher) lexeme
	 * if one exists. If not found it will return null.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately following the offset if none exists at
	 *         the given offset
	 */
	public Lexeme<T> getCeilingLexeme(int offset)
	{
		int index = this.getLexemeCeilingIndex(offset);

		return this.getLexeme(index);
	}

	/**
	 * Returns the first lexeme in the list. If there is no lexeme at that position (i.e. empty list), returns null
	 * 
	 * @return The first lexeme, or null
	 */
	public Lexeme<T> getFirstLexeme()
	{
		return this.getLexeme(0);
	}

	/**
	 * Gets the lexeme at the specified offset. If it is a whitespace character it will return the previous (lower)
	 * lexeme if one exists. If not found it will return null.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately preceding the offset if none exists at
	 *         the given offset
	 */
	public Lexeme<T> getFloorLexeme(int offset)
	{
		int index = this.getLexemeFloorIndex(offset);

		return this.getLexeme(index);
	}

	/**
	 * Returns the last lexeme in the list. If there is no lexeme at that position (i.e. empty list), returns null
	 * 
	 * @return The last lexeme, or null
	 */
	public Lexeme<T> getLastLexeme()
	{
		return this.getLexeme(this.size() - 1);
	}

	/**
	 * Get a lexeme at the specified index. This method will return null if the index is not within the range of this
	 * lexeme list
	 * 
	 * @param index
	 *            The index to retrieve
	 * @return The lexeme at the specified index
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

	/**
	 * Get the index of the lexeme at the specified offset. If it is a whitespace character it will return the next
	 * (higher) lexeme if one exists. If not found it will return -1.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately following the offset if none exists at
	 *         the given offset
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

	/**
	 * Get the index of the lexeme at the specified offset. If it is a whitespace character it will return the previous
	 * (lower) lexeme if one exists. If not found it will return -1.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately preceding the offset if none exists at
	 *         the given offset
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

	/**
	 * Get the index of the lexeme at the specified offset
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset. Returns null if no lexeme is at the given offset.
	 */
	public Lexeme<T> getLexemeFromOffset(int offset)
	{
		int index = this.getLexemeIndex(offset);

		return this.getLexeme(index);
	}

	/**
	 * Get the index of the lexeme at the specified offset
	 * 
	 * @param offset
	 * @return Returns the index of the lexeme at the given offset. A negative value will be returned if there is no
	 *         lexeme at the given offset
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

	/**
	 * getTypeFromData
	 * 
	 * @param data
	 * @return
	 */
	protected abstract T getTypeFromData(Object data);

	/**
	 * iterator
	 */
	public Iterator<Lexeme<T>> iterator()
	{
		return this._lexemes.iterator();
	}

	/**
	 * Return the size of this list
	 * 
	 * @return The list size
	 */
	public int size()
	{
		return this._lexemes.size();
	}
}
