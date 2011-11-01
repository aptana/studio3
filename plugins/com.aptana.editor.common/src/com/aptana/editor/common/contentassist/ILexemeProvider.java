/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.contentassist;

import com.aptana.parsing.lexer.Lexeme;

/**
 * ILexemeProvider
 */
public interface ILexemeProvider<T> extends Iterable<Lexeme<T>>
{
	/**
	 * Gets the lexeme at the specified offset. If it is a whitespace character it will return the next (higher) lexeme
	 * if one exists. If not found it will return null.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately following the offset if none exists at
	 *         the given offset
	 */
	Lexeme<T> getCeilingLexeme(int offset);

	/**
	 * Returns the first lexeme in the list. If there is no lexeme at that position (i.e. empty list), returns null
	 * 
	 * @return The first lexeme, or null
	 */
	Lexeme<T> getFirstLexeme();

	/**
	 * Gets the lexeme at the specified offset. If it is a whitespace character it will return the previous (lower)
	 * lexeme if one exists. If not found it will return null.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately preceding the offset if none exists at
	 *         the given offset
	 */
	Lexeme<T> getFloorLexeme(int offset);

	/**
	 * Returns the last lexeme in the list. If there is no lexeme at that position (i.e. empty list), returns null
	 * 
	 * @return The last lexeme, or null
	 */
	Lexeme<T> getLastLexeme();

	/**
	 * Get a lexeme at the specified index. This method will return null if the index is not within the range of this
	 * lexeme list
	 * 
	 * @param index
	 *            The index to retrieve
	 * @return The lexeme at the specified index
	 */
	Lexeme<T> getLexeme(int index);

	/**
	 * Get the index of the lexeme at the specified offset. If it is a whitespace character it will return the next
	 * (higher) lexeme if one exists. If not found it will return -1.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately following the offset if none exists at
	 *         the given offset
	 */
	int getLexemeCeilingIndex(int offset);

	/**
	 * Get the index of the lexeme at the specified offset. If it is a whitespace character it will return the previous
	 * (lower) lexeme if one exists. If not found it will return -1.
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset or the lexeme immediately preceding the offset if none exists at
	 *         the given offset
	 */
	int getLexemeFloorIndex(int offset);

	/**
	 * Get the index of the lexeme at the specified offset
	 * 
	 * @param offset
	 * @return Returns the lexeme at the given offset. Returns null if no lexeme is at the given offset.
	 */
	Lexeme<T> getLexemeFromOffset(int offset);

	/**
	 * Get the index of the lexeme at the specified offset
	 * 
	 * @param offset
	 * @return Returns the index of the lexeme at the given offset. A negative value will be returned if there is no
	 *         lexeme at the given offset
	 */
	int getLexemeIndex(int offset);

	/**
	 * Return the size of this list
	 * 
	 * @return The list size
	 */
	int size();
}
