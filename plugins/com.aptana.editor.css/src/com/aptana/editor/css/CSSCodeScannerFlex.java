/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css;

import java.io.IOException;
import java.util.Set;
import java.util.regex.Matcher;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import beaver.Scanner.Exception;
import beaver.Symbol;

import com.aptana.core.util.CollectionsUtil;
import com.aptana.css.core.parsing.CSSTokenType;
import com.aptana.css.core.parsing.CSSTokenTypeSymbol;
import com.aptana.editor.common.parsing.AbstractFlexTokenScanner;
import com.aptana.editor.css.parsing.CSSColoringFlexScanner;

/**
 * JFlex-based scanner for CSS.
 */
@SuppressWarnings("deprecation")
public class CSSCodeScannerFlex extends AbstractFlexTokenScanner
{

	/**
	 * Keep the level of curlies...
	 */
	private int fCurlyState;
	private boolean fInMedia;
	protected boolean fInPropertyValue;
	protected boolean fInSelector;
	private String fContextToAppend = ""; //$NON-NLS-1$

	private static final Set<String> HTML_TAGS = CollectionsUtil.newSet(CSSCodeScannerRuleBased.HTML_TAGS);
	private static final Set<String> PROPERTY_NAMES = CollectionsUtil.newSet(CSSCodeScannerRuleBased.PROPERTY_NAMES);
	private static final Set<String> PROPERTY_VALUES = CollectionsUtil.newSet(CSSCodeScannerRuleBased.PROPERTY_VALUES);
	private static final Set<String> FUNCTIONS = CollectionsUtil.newSet(CSSCodeScannerRuleBased.FUNCTIONS);
	private static final Set<String> FONT_NAMES = CollectionsUtil.newSet(CSSCodeScannerRuleBased.FONT_NAMES);
	private static final Set<String> MEDIA = CollectionsUtil.newSet(CSSCodeScannerRuleBased.MEDIA);
	private static final Set<String> STANDARD_COLORS = CollectionsUtil.newSet(CSSCodeScannerRuleBased.STANDARD_COLORS);
	private static final Set<String> DEPRECATED_COLORS = CollectionsUtil
			.newSet(CSSCodeScannerRuleBased.DEPRECATED_COLORS);

	public CSSCodeScannerFlex()
	{
		super(new CSSColoringFlexScanner());
	}

	protected void setSource(String string)
	{
		((CSSColoringFlexScanner) fScanner).setSource(string);
	}

	@Override
	public void setRange(IDocument document, int offset, int length)
	{
		super.setRange(document, offset, length);

		this.fCurlyState = 0;
		this.fInMedia = false;
		this.fInPropertyValue = false;
		this.fInSelector = false;
		if (offset > 0)
		{
			String previous = null;
			try
			{
				// Note: keeping same approach from rule-based parser to discover if in media/curly state
				// but this may be very slow on a large document.
				ITypedRegion[] partitions = document.computePartitioning(0, offset);
				for (ITypedRegion region : partitions)
				{
					// skip strings and comments
					if (CSSSourceConfiguration.MULTILINE_COMMENT.equals(region.getType())
							|| CSSSourceConfiguration.STRING_DOUBLE.equals(region.getType())
							|| CSSSourceConfiguration.STRING_SINGLE.equals(region.getType()))
					{
						continue;
					}
					previous = document.get(region.getOffset(), region.getLength());
					// Calculate curly nesting level and whether we're inside media
					Matcher m = CSSCodeScannerRuleBased.CURLY_MEDIA_PATTERN.matcher(previous);
					while (m.find())
					{
						String found = m.group();
						if ("{".equals(found)) //$NON-NLS-1$
						{
							this.fCurlyState++;
						}
						else if ("}".equals(found)) //$NON-NLS-1$
						{
							this.fCurlyState--;
							if (this.fCurlyState <= 0 && this.fInMedia)
							{
								this.fInMedia = false;
							}
						}
						else if (CSSCodeScannerRuleBased.KEYWORD_MEDIA.equals(found))
						{
							this.fInMedia = true;
							this.fCurlyState = 0;
						}
					}
				}
			}
			catch (BadLocationException e)
			{
				// ignore
			}
		}
		buildContext();
	}

	@Override
	protected IToken mapToken(Symbol symbol) throws IOException, Exception
	{
		CSSTokenTypeSymbol tokenTypeSymbol = (CSSTokenTypeSymbol) symbol;
		CSSTokenType tokenData;
		// System.out.println(symbol.value + " - " + symbol.getId());
		switch (tokenTypeSymbol.token)
		{
			case IDENTIFIER:
				if (HTML_TAGS.contains(symbol.value))
				{
					tokenData = CSSTokenType.ELEMENT;
				}
				else if (FUNCTIONS.contains(symbol.value))
				{
					tokenData = CSSTokenType.FUNCTION;
				}
				else if (PROPERTY_VALUES.contains(symbol.value))
				{
					tokenData = CSSTokenType.VALUE;
				}
				else if (STANDARD_COLORS.contains(symbol.value))
				{
					tokenData = CSSTokenType.COLOR;
				}
				else if (DEPRECATED_COLORS.contains(symbol.value))
				{
					tokenData = CSSTokenType.DEPRECATED_COLOR;
				}
				else if (PROPERTY_NAMES.contains(symbol.value))
				{
					tokenData = CSSTokenType.PROPERTY;
				}
				else if (MEDIA.contains(symbol.value))
				{
					tokenData = CSSTokenType.MEDIA;
				}

				// slower ones as last
				else if (FONT_NAMES.contains(((String) symbol.value).toLowerCase()))
				{
					tokenData = CSSTokenType.FONT;
				}
				else if (CSSCodeScannerRuleBased.VENDOR_WORD_RULE.wordOK((String) symbol.value, null))
				{
					tokenData = CSSTokenType.PROPERTY;
				}
				else
				{
					tokenData = CSSTokenType.IDENTIFIER;
				}
				break;

			case EOF:
				return Token.EOF;

			default:
				tokenData = tokenTypeSymbol.token;

		}
		return makeTokenWithContext(tokenData, false);
	}

	@Override
	protected IToken getWhitespace()
	{
		return makeTokenWithContext(null, true);
	}

	protected IToken getUndefinedToken()
	{
		return new Token(CSSTokenType.UNDEFINED.getScope());
	}

	private IToken makeTokenWithContext(CSSTokenType tokenData, boolean isWhitespace)
	{
		if (CSSTokenType.MEDIA_KEYWORD == tokenData)
		{
			this.fInMedia = true;
			this.fCurlyState = 0;
			buildContext();
		}
		else if (CSSTokenType.LCURLY == tokenData)
		{
			// Use a different punctuation scope if opening @media
			if (this.fInMedia && this.fCurlyState == 0)
			{
				tokenData = CSSTokenType.LCURLY_MEDIA;
			}
			this.fCurlyState++;
			buildContext();
		}
		else if (CSSTokenType.RCURLY == tokenData)
		{
			this.fInPropertyValue = false;
			buildContext();
		}
		else if (CSSTokenType.PROPERTY == tokenData)
		{
			this.fInSelector = false;
			buildContext();
		}
		else if (CSSTokenType.COLON == tokenData)
		{
			this.fInPropertyValue = true;
			buildContext();
		}
		else if (CSSTokenType.CLASS == tokenData || CSSTokenType.ID == tokenData || CSSTokenType.STAR == tokenData
				|| CSSTokenType.ELEMENT == tokenData)
		{
			this.fInSelector = true;
			buildContext();
		}

		IToken ret = null;

		// Constant property value, like "top" or "left", but not inside property value. Assume it's a property name!
		if (!this.fInPropertyValue && CSSTokenType.VALUE == tokenData)
		{
			tokenData = CSSTokenType.PROPERTY;
			ret = computeReturn(tokenData, isWhitespace);
			buildContext();
		}
		// left curly ends selector meta scope
		else if (CSSTokenType.LCURLY == tokenData)
		{
			this.fInSelector = false;
			ret = computeReturn(tokenData, isWhitespace);
			buildContext();
		}
		// right curly might end media/rule meta scopes
		else if (CSSTokenType.RCURLY == tokenData)
		{
			this.fCurlyState--;
			if (this.fCurlyState <= 0 && this.fInMedia)
			{
				tokenData = CSSTokenType.RCURLY_MEDIA;
				this.fInMedia = false;
			}
			ret = computeReturn(tokenData, isWhitespace);
			buildContext();
		}
		// semicolon ends property value meta scope
		else if (CSSTokenType.SEMICOLON == tokenData)
		{
			ret = computeReturn(tokenData, isWhitespace);
			this.fInPropertyValue = false;
			buildContext();
		}
		else
		{
			ret = computeReturn(tokenData, isWhitespace);
		}

		return ret;
	}

	private IToken computeReturn(CSSTokenType tokenData, boolean isWhitespace)
	{
		if (isWhitespace)
		{
			return tokenOnWhitespace;
		}

		// Grab data again, because we may have changed the token above...
		if (tokenData != null)
		{
			String scope = tokenData.getScope();
			int contextLen = fContextToAppend.length();
			if (contextLen == 0)
			{
				return new Token(scope);
			}
			int scopeLen = scope.length();

			// Note: optimized creating string with context+scope as this was a bottleneck when profiling.
			char[] array = new char[contextLen + scopeLen];
			fContextToAppend.getChars(0, contextLen, array, 0);
			scope.getChars(0, scopeLen, array, contextLen);

			return new Token(new String(array));
		}
		else
		{
			return new Token(CSSTokenType.UNDEFINED.getScope());
		}
	}

	private IToken tokenOnWhitespace;

	/**
	 * Creates the context for the returned tokens. Must be called whenever our context changes (i.e.: media, curly
	 * state, selector, property value)
	 */
	private void buildContext()
	{
		StringBuilder builder = null;
		// Media META scope
		if (this.fInMedia)
		{
			// Note: 56 is the size of the largest CSSTokenType scope.
			builder = new StringBuilder(56);
			builder.append(CSSTokenType.META_MEDIA.getScope()).append(' ');
		}
		// Ruleset META scope

		// isInsideRule: media adds a curly nesting level!
		if (this.fInMedia ? this.fCurlyState > 1 : this.fCurlyState > 0)
		{
			builder = builder != null ? builder : new StringBuilder(56);
			builder.append(CSSTokenType.META_RULE.getScope()).append(' ');
		}
		// Selector META scope
		else if (this.fInSelector)
		{
			builder = builder != null ? builder : new StringBuilder(56);
			builder.append(CSSTokenType.META_SELECTOR.getScope()).append(' ');
		}
		// Property value META scope
		if (this.fInPropertyValue)
		{
			builder = builder != null ? builder : new StringBuilder(56);
			builder.append(CSSTokenType.META_PROPERTY_VALUE.getScope()).append(' ');
		}

		if (builder == null)
		{
			fContextToAppend = ""; //$NON-NLS-1$
			tokenOnWhitespace = Token.WHITESPACE;
		}
		else
		{
			this.fContextToAppend = builder.toString();
			tokenOnWhitespace = new Token(fContextToAppend.substring(0, builder.length() - 1));
		}
	}

}
