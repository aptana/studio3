/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;

import com.aptana.editor.common.contentassist.InformationControl;
import com.aptana.editor.common.contentassist.LexemeProvider;
import com.aptana.editor.css.CSSColors;
import com.aptana.editor.css.CSSScopeScanner;
import com.aptana.editor.css.contentassist.CSSIndexQueryHelper;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.theme.ColorManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public class CSSTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
{
	private static class ThemedInformationControl extends InformationControl implements IInformationControlExtension2
	{
		public ThemedInformationControl(Shell parent)
		{
			super(parent);

			GridData gd = (GridData) getStyledTextWidget().getLayoutData();

			gd.horizontalIndent = 0;
			gd.verticalIndent = 0;
		}

		@Override
		protected Color getBackground()
		{
			return getThemeBackground();
		}

		@Override
		protected Color getBorderColor()
		{
			return getForeground();
		}

		protected ColorManager getColorManager()
		{
			return ThemePlugin.getDefault().getColorManager();
		}

		protected Theme getCurrentTheme()
		{
			return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		}

		@Override
		protected Color getForeground()
		{
			return getThemeForeground();
		}

		protected Color getThemeBackground()
		{
			return getColorManager().getColor(getCurrentTheme().getBackground());
		}

		protected Color getThemeForeground()
		{
			return getColorManager().getColor(getCurrentTheme().getForeground());
		}

		public void setInput(Object input)
		{
			if (input instanceof RGB)
			{
				setBackgroundColor(getColorManager().getColor((RGB) input));
			}
			else if (input instanceof String)
			{
				setInformation((String) input);
			}
		}
	}

	/*
	 * @see org.eclipse.jface.text.ITextHoverExtension#getHoverControlCreator()
	 */
	public IInformationControlCreator getHoverControlCreator()
	{
		return new IInformationControlCreator()
		{
			public IInformationControl createInformationControl(Shell parent)
			{
				return new ThemedInformationControl(parent);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		// Not called
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHoverExtension2#getHoverInfo2(org.eclipse.jface.text.ITextViewer,
	 * org.eclipse.jface.text.IRegion)
	 */
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		int offset = hoverRegion.getOffset();
		LexemeProvider<CSSTokenType> lexemeProvider = getLexemeProvider(textViewer, offset);
		Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexemeFromOffset(offset);
		Object result = null;

		switch (lexeme.getType())
		{
			case COLOR:
				result = parseHexRGB(CSSColors.to6CharHexWithLeadingHash(lexeme.getText()));
				break;

			case FUNCTION:
				if ("rgb".equals(lexeme.getText()))
				{
					int start = lexeme.getEndingOffset();
					List<Lexeme<CSSTokenType>> lexemes = this.getFunctionLexemes(lexemeProvider, start);

					if (lexemes.size() == 8)
					{
						Lexeme<CSSTokenType> redLexeme = lexemes.get(2);
						Lexeme<CSSTokenType> greenLexeme = lexemes.get(4);
						Lexeme<CSSTokenType> blueLexeme = lexemes.get(6);

						if (isNumber(redLexeme) && isNumber(greenLexeme) && isNumber(blueLexeme))
						{
							int red = Integer.parseInt(redLexeme.getText());
							int green = Integer.parseInt(greenLexeme.getText());
							int blue = Integer.parseInt(blueLexeme.getText());

							result = new RGB(red, green, blue);
						}
					}
				}

			case PROPERTY:
				CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
				PropertyElement property = queryHelper.getProperty(lexeme.getText());

				if (property != null)
				{
					result = property.getDescription();
				}
				break;

			default:
				System.out.println(lexeme.getType().name());
		}

		// try
		// {
		// String word = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
		//
		// if (CSSColors.namedColorExists(word))
		// {
		// word = CSSColors.to6CharHexWithLeadingHash(word);
		// }
		// else
		// {
		// // Match against a pattern to verify it's a color
		// if (!RGB_PATTERN.matcher(word).matches())
		// {
		// return null;
		// }
		// word = CSSColors.to6CharHexWithLeadingHash(word);
		// }
		//
		// return parseHexRGB(word);
		// }
		// catch (BadLocationException e)
		// {
		// // ignores the exception; just assumes no hover info is available
		// }

		return result;
	}

	private boolean isNumber(Lexeme<CSSTokenType> token)
	{
		return (token != null && token.getType() == CSSTokenType.NUMBER);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		// look for the current "word"
		LexemeProvider<CSSTokenType> lexemeProvider = getLexemeProvider(textViewer, offset);
		Lexeme<CSSTokenType> lexeme = lexemeProvider.getLexemeFromOffset(offset);
		IRegion result = null;

		if (lexeme != null)
		{
			switch (lexeme.getType())
			{
				case COLOR:
				case PROPERTY:
					result = new Region(lexeme.getStartingOffset(), lexeme.getLength());
					break;

				case FUNCTION:
					if ("rgb".equals(lexeme.getText()))
					{
						int start = lexeme.getStartingOffset();
						List<Lexeme<CSSTokenType>> lexemes = this.getFunctionLexemes(lexemeProvider, start);
						int end = lexemes.get(lexemes.size() - 1).getEndingOffset();

						result = new Region(start, end - start + 1);
					}
					break;
			}
		}

		if (result == null)
		{
			result = new Region(offset, 0);
		}

		return result;
	}

	/**
	 * getFunctionEndingOffset
	 * 
	 * @param lexemeProvider
	 * @param startingOffset
	 * @return
	 */
	protected List<Lexeme<CSSTokenType>> getFunctionLexemes(LexemeProvider<CSSTokenType> lexemeProvider, int startingOffset)
	{
		List<Lexeme<CSSTokenType>> result = new ArrayList<Lexeme<CSSTokenType>>();
		int index = lexemeProvider.getLexemeIndex(startingOffset);

		for (int i = index; i < lexemeProvider.size(); i++)
		{
			Lexeme<CSSTokenType> candidate = lexemeProvider.getLexeme(i);

			result.add(candidate);

			if (candidate.getType() == CSSTokenType.RPAREN)
			{
				break;
			}
		}

		return result;
	}

	/**
	 * getLexemeProvider
	 * 
	 * @param textViewer
	 * @param offset
	 * @return
	 */
	protected LexemeProvider<CSSTokenType> getLexemeProvider(ITextViewer textViewer, int offset)
	{
		IDocument document = textViewer.getDocument();

		LexemeProvider<CSSTokenType> lexemeProvider = new LexemeProvider<CSSTokenType>(document, offset, new CSSScopeScanner())
		{
			@Override
			protected CSSTokenType getTypeFromData(Object data)
			{
				return (CSSTokenType) data;
			}
		};
		return lexemeProvider;
	}

	/**
	 * parseHexRGB
	 * 
	 * @param token
	 * @return
	 */
	private RGB parseHexRGB(String token)
	{
		if (token == null)
		{
			return new RGB(0, 0, 0);
		}

		if (token.length() != 7 && token.length() != 9)
		{
			ThemePlugin.logError(MessageFormat.format("Received RGB Hex value with invalid length: {0}", token), null); //$NON-NLS-1$

			return new RGB(0, 0, 0);
		}

		String s = token.substring(1, 3);
		int r = Integer.parseInt(s, 16);
		s = token.substring(3, 5);
		int g = Integer.parseInt(s, 16);
		s = token.substring(5, 7);
		int b = Integer.parseInt(s, 16);

		return new RGB(r, g, b);
	}
}
