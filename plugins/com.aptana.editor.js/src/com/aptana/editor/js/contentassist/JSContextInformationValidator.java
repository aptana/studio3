/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.contentassist;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.aptana.editor.common.contentassist.ILexemeProvider;
import com.aptana.editor.js.text.JSFlexLexemeProvider;
import com.aptana.js.core.parsing.JSFlexScanner;
import com.aptana.js.core.parsing.JSTokenType;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.theme.ColorManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public class JSContextInformationValidator implements IContextInformationValidator, IContextInformationPresenter
{
	private class DelimiterCounter
	{
		public final int commaCount;
		public final int parenCount;
		public final int curlyCount;

		DelimiterCounter(int offset)
		{
			int commaCount = 0;
			int parenCount = 0;
			int curlyCount = 0;
			int bracketCount = 0;

			// grab lexemes
			IDocument document = _viewer.getDocument();
			ILexemeProvider<JSTokenType> lexemeProvider = new JSFlexLexemeProvider(document, offset, _startingOffset,
					new JSFlexScanner());

			// get starting index based on the initial offset provided to this validator
			int index = lexemeProvider.getLexemeFloorIndex(_startingOffset);

			while (0 <= index && index < lexemeProvider.size())
			{
				Lexeme<JSTokenType> lexeme = lexemeProvider.getLexeme(index);

				if (lexeme.getStartingOffset() < offset)
				{
					switch (lexeme.getType())
					{
						case COMMA:
							if (bracketCount == 0 && curlyCount == 0 && parenCount == 1)
							{
								commaCount++;
							}
							break;

						case RBRACKET:
							bracketCount--;
							break;

						case RCURLY:
							curlyCount--;
							break;

						case RPAREN:
							parenCount--;
							break;

						case LBRACKET:
							bracketCount++;
							break;

						case LCURLY:
							curlyCount++;
							break;

						case LPAREN:
							parenCount++;
							break;

						default:
							break;
					}

					index++;
				}
				else
				{
					break;
				}
			}

			// save results
			this.commaCount = commaCount;
			this.parenCount = parenCount;
			this.curlyCount = curlyCount;
		}

		public int getArgumentIndex()
		{
			return (this.parenCount != 0) ? this.commaCount : -1;
		}
	}

	private IContextInformation _contextInformation;
	private ITextViewer _viewer;
	private int _startingOffset;

	/**
	 * createBoldStyle
	 * 
	 * @param startingOffset
	 * @param length
	 * @return
	 */
	protected StyleRange createBoldStyle(int startingOffset, int length)
	{
		return createStyle(startingOffset, length, SWT.BOLD);
	}

	/**
	 * createNormalStyle
	 * 
	 * @param startingOffset
	 * @param length
	 * @return
	 */
	protected StyleRange createNormalStyle(int startingOffset, int length)
	{
		return createStyle(startingOffset, length, SWT.NORMAL);
	}

	/**
	 * createStyle
	 * 
	 * @param startingOffset
	 * @param length
	 * @param style
	 * @return
	 */
	protected StyleRange createStyle(int startingOffset, int length, int style)
	{
		return new StyleRange(startingOffset, length, this.getForeground(), null, style);
	}

	/**
	 * getArgumentIndex
	 * 
	 * @param offset
	 * @return
	 */
	protected int getArgumentIndex(int offset)
	{
		DelimiterCounter counter = new DelimiterCounter(offset);

		return counter.getArgumentIndex();
	}

	/**
	 * getColorManager
	 * 
	 * @return
	 */
	protected ColorManager getColorManager()
	{
		return ThemePlugin.getDefault().getColorManager();
	}

	/**
	 * getContextInformation
	 * 
	 * @return
	 */
	protected IContextInformation getContextInformation()
	{
		return _contextInformation;
	}

	/**
	 * getCurrentTheme
	 * 
	 * @return
	 */
	protected Theme getCurrentTheme()
	{
		return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
	 * org.eclipse.jface.text.TextPresentation)
	 */
	protected Color getForeground()
	{
		return getColorManager().getColor(getCurrentTheme().getForeground());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.text.contentassist.IContextInformationValidator#install(org.eclipse.jface.text.contentassist
	 * .IContextInformation, org.eclipse.jface.text.ITextViewer, int)
	 */
	public void install(IContextInformation info, ITextViewer viewer, int offset)
	{
		this._contextInformation = info;
		this._viewer = viewer;

		if (info instanceof IContextInformationExtension)
		{
			this._startingOffset = ((IContextInformationExtension) info).getContextInformationPosition();
		}
		else
		{
			this._startingOffset = offset;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#isContextInformationValid(int)
	 */
	public boolean isContextInformationValid(int offset)
	{
		boolean result = false;

		if (offset > this._startingOffset)
		{
			DelimiterCounter counter = new DelimiterCounter(offset);

			if (counter.curlyCount == 0)
			{
				result = (this.getArgumentIndex(offset) >= 0);
			}
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
	 * org.eclipse.jface.text.TextPresentation)
	 */
	public boolean updatePresentation(int offset, TextPresentation presentation)
	{
		// grab presentation text and split into separate lines
		String info = this._contextInformation.getInformationDisplayString();
		String[] lines = info.split(JSContextInformation.DESCRIPTION_DELIMITER);

		// determine which argument we are within
		int argIndex = this.getArgumentIndex(offset);

		// reset the presentation
		presentation.clear();

		// bold the function name
		int startingPosition = info.indexOf('(');

		presentation.addStyleRange(createBoldStyle(0, startingPosition));

		if (0 <= argIndex && argIndex < lines.length - 1)
		{
			// bold the current argument name
			int endingPosition = info.indexOf(',');
			int closingParenPosition = info.indexOf(')');

			if (endingPosition == -1)
			{
				endingPosition = closingParenPosition;
			}
			else if (closingParenPosition != -1)
			{
				endingPosition = Math.min(endingPosition, closingParenPosition);
			}

			for (int i = 0; i < argIndex; i++)
			{
				startingPosition = endingPosition;
				endingPosition = info.indexOf(',', startingPosition + 1);

				if (endingPosition == -1)
				{
					endingPosition = info.indexOf(')', startingPosition);
				}

				if (endingPosition == -1)
				{
					break;
				}
			}

			if (endingPosition != -1)
			{
				// advance over '(' or ','
				startingPosition++;

				presentation.addStyleRange(createBoldStyle(startingPosition, endingPosition - startingPosition));
			}

			// bold the argument description line
			int runningLength = lines[0].length() + 1; // 1 for delimiter

			for (int i = 1; i < lines.length; i++)
			{
				String line = lines[i];
				int length = line.length();

				if (i - 1 == argIndex)
				{
					presentation.addStyleRange(createBoldStyle(runningLength, length));
				}
				else
				{
					int colonIndex = line.indexOf(':');

					if (colonIndex != -1)
					{
						presentation.addStyleRange(createNormalStyle(runningLength, colonIndex));
					}
				}

				runningLength += length + 1; // 1 for delimiter
			}
		}

		return true;
	}
}
