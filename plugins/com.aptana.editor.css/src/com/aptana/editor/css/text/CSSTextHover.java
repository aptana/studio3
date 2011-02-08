/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
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
import com.aptana.editor.css.contentassist.model.ElementElement;
import com.aptana.editor.css.contentassist.model.PropertyElement;
import com.aptana.editor.css.parsing.lexer.CSSTokenType;
import com.aptana.parsing.lexer.Lexeme;
import com.aptana.theme.ColorManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public class CSSTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
{
	private static final String RGB = "rgb"; //$NON-NLS-1$

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

		if (lexeme != null)
		{
			switch (lexeme.getType())
			{
				case COLOR:
				case RGB:
				{
					result = parseHexRGB(CSSColors.to6CharHexWithLeadingHash(lexeme.getText()));
					break;
				}

				case FUNCTION:
				{
					if (RGB.equals(lexeme.getText()))
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
					break;
				}

				case ELEMENT:
				{
					CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
					ElementElement element = queryHelper.getElement(lexeme.getText());

					if (element != null)
					{
						result = element.getDescription();
					}
					break;
				}

				case PROPERTY:
				{
					CSSIndexQueryHelper queryHelper = new CSSIndexQueryHelper();
					PropertyElement property = queryHelper.getProperty(lexeme.getText());

					if (property != null)
					{
						result = property.getDescription();
					}
					break;
				}

				default:
					if (Platform.inDevelopmentMode())
					{
						System.out.println(lexeme.getType().name());
					}
			}
		}

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
					if (RGB.equals(lexeme.getText()))
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
		RGB result;

		if (token == null)
		{
			result = new RGB(0, 0, 0);
		}
		else if (token.length() == 4)
		{
			String s = token.substring(1, 2);
			int r = Integer.parseInt(s + s, 16);
			s = token.substring(2, 3);
			int g = Integer.parseInt(s + s, 16);
			s = token.substring(3, 4);
			int b = Integer.parseInt(s + s, 16);

			result = new RGB(r, g, b);
		}
		else if (token.length() == 7 || token.length() == 9)
		{
			String s = token.substring(1, 3);
			int r = Integer.parseInt(s, 16);
			s = token.substring(3, 5);
			int g = Integer.parseInt(s, 16);
			s = token.substring(5, 7);
			int b = Integer.parseInt(s, 16);

			result = new RGB(r, g, b);
		}
		else
		{
			String message = MessageFormat.format(Messages.CSSTextHover_Invalid_RGB_Hex_Value, token);

			ThemePlugin.logError(message, null);

			result = new RGB(0, 0, 0);
		}

		return result;
	}
}
