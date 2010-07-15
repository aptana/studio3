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
	 * Convert the partition that contains the given offset into a list of
	 * lexemes.
	 * 
	 * @param document
	 * @param offset
	 */
	public LexemeProvider(IDocument document, int offset, ITokenScanner scanner)
	{
		int length = 0;
		
		try
		{
			ITypedRegion partition = document.getPartition(offset);
			
			offset = partition.getOffset();
			length = partition.getLength();
		}
		catch (BadLocationException e)
		{
		}
		
		this.createLexemeList(document, offset, length, scanner);
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
	 * Add the specified lexeme to the lexeme provider's list. Subclasses can
	 * use override this method to filter which type of lexemes should be added
	 * to the list
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
	 * getCeilingLexeme
	 * 
	 * @param offset
	 * @return
	 */
	public Lexeme<T> getCeilingLexeme(int offset)
	{
		int index = this.getLexemeCeilingIndex(offset);

		return this.getLexeme(index);
	}

	/**
	 * getFirstLexeme
	 * 
	 * @return
	 */
	public Lexeme<T> getFirstLexeme()
	{
		return this.getLexeme(0);
	}
	
	/**
	 * getFloorLexeme
	 * 
	 * @param offset
	 * @return
	 */
	public Lexeme<T> getFloorLexeme(int offset)
	{
		int index = this.getLexemeFloorIndex(offset);

		return this.getLexeme(index);
	}
	
	/**
	 * getLastLexeme
	 * 
	 * @return
	 */
	public Lexeme<T> getLastLexeme()
	{
		return this.getLexeme(this.size() - 1);
	}

	/**
	 * getLexeme
	 * 
	 * @param index
	 * @return
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
	 * getLexemeCeilingIndex
	 * 
	 * @param offset
	 * @return
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
	 * getLexemeFloorIndex
	 * 
	 * @param offset
	 * @return
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
	 * getLexemeFromOffset
	 * 
	 * @param offset
	 * @return
	 */
	public Lexeme<T> getLexemeFromOffset(int offset)
	{
		int index = this.getLexemeIndex(offset);

		return this.getLexeme(index);
	}

	/**
	 * getLexemeIndex
	 * 
	 * @param offset
	 * @return
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
	 * size
	 * 
	 * @return
	 */
	public int size()
	{
		return this._lexemes.size();
	}
}
