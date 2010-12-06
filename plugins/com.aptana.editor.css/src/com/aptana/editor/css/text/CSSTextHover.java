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
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
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
import com.aptana.editor.css.Activator;
import com.aptana.editor.css.CSSColors;
import com.aptana.theme.ColorManager;
import com.aptana.theme.Theme;
import com.aptana.theme.ThemePlugin;

public class CSSTextHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2
{

	private static final Pattern RGB_PATTERN = Pattern.compile("#([a-fA-F0-9]{3}|[a-fA-F0-9]{6})"); //$NON-NLS-1$

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion)
	{
		// Not called
		return null;
	}

	public IRegion getHoverRegion(ITextViewer textViewer, int offset)
	{
		// look for the current "word"
		IDocument doc = textViewer.getDocument();
		try
		{
			final int origOffset = offset;
			int start = offset;
			while (offset >= 0)
			{
				char c = doc.getChar(offset--);
				if (!isWordPart(c))
				{
					start = offset + 2;
					break;
				}
			}
			offset = origOffset;
			int docLength = doc.getLength();
			while (offset < docLength)
			{
				char c = doc.getChar(offset++);
				if (!isWordPart(c))
				{
					offset--;
					break;
				}
			}
			return new Region(start, offset - start);
		}
		catch (BadLocationException e)
		{
			Activator.logError(e.getMessage(), e);
		}
		return new Region(offset, 0);
	}

	private boolean isWordPart(char c)
	{
		return Character.isLetterOrDigit(c) || c == '-' || c == '_' || c == '.' || c == '#';
	}

	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion)
	{
		try
		{
			String word = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
			if (CSSColors.namedColorExists(word))
			{
				word = CSSColors.to6CharHexWithLeadingHash(word);
			}
			else
			{
				// Match against a pattern to verify it's a color
				if (!RGB_PATTERN.matcher(word).matches())
				{
					return null;
				}
				word = CSSColors.to6CharHexWithLeadingHash(word);
			}
			return parseHexRGB(word);
		}
		catch (BadLocationException e)
		{
			// ignores the exception; just assumes no hover info is available
		}
		return null;
	}

	private RGB parseHexRGB(String token)
	{
		if (token == null)
			return new RGB(0, 0, 0);
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
		protected Color getForeground()
		{
			return getThemeForeground();
		}

		@Override
		protected Color getBorderColor()
		{
			return getForeground();
		}

		protected Color getThemeForeground()
		{
			return getColorManager().getColor(getCurrentTheme().getForeground());
		}

		protected Color getThemeBackground()
		{
			return getColorManager().getColor(getCurrentTheme().getBackground());
		}

		protected Theme getCurrentTheme()
		{
			return ThemePlugin.getDefault().getThemeManager().getCurrentTheme();
		}

		protected ColorManager getColorManager()
		{
			return ThemePlugin.getDefault().getColorManager();
		}

		public void setInput(Object input)
		{
			if (input instanceof RGB)
			{
				setBackgroundColor(getColorManager().getColor((RGB) input));
			}
		}
	}
}
