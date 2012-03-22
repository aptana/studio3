/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;

import com.aptana.ui.util.SWTUtils;

/**
 * Extends the ControlThemer to always apply an additional font style to the theme font
 * 
 * @author nle
 */
public class StyledFontThemer extends ControlThemer
{
	int fontStyle;
	Font styledFont = null;
	Font defaultStyledFont = null;

	public StyledFontThemer(Control control, int fontStyle, Color defaultBg)
	{
		super(control, defaultBg);
		this.fontStyle = fontStyle;
		defaultStyledFont = new Font(control.getDisplay(), SWTUtils.styleFont(control.getFont(), fontStyle));
	}

	protected void applyControlFont()
	{
		if (!controlIsDisposed())
		{
			if (useEditorFont())
			{
				getControl().setFont(getFont());
			}
			else
			{
				getControl().setFont(defaultStyledFont);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.internal.ControlThemer#getFont()
	 */
	@Override
	protected Font getFont()
	{
		Font font = super.getFont();
		FontData[] fontData = SWTUtils.styleFont(font, fontStyle);

		if (styledFont != null && !fontDataEquals(fontData, styledFont.getFontData()))
		{
			styledFont.dispose();
			styledFont = null;
		}

		if (styledFont == null)
		{
			styledFont = new Font(getControl().getDisplay(), fontData);
		}

		return styledFont;
	}

	private boolean fontDataEquals(FontData[] fd1, FontData[] fd2)
	{
		if (fd1 != null && fd2 != null && fd1.length == fd2.length)
		{
			for (int i = 0; i < fd1.length; i++)
			{
				if (!fd1[i].equals(fd2[i]))
				{
					return false;
				}
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.internal.ControlThemer#unapplyControlFont()
	 */
	@Override
	protected void unapplyControlFont()
	{
		if (!controlIsDisposed())
		{
			getControl().setFont(
					defaultStyledFont != null && !defaultStyledFont.isDisposed() ? defaultStyledFont : null);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.internal.ControlThemer#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();

		if (styledFont != null && !styledFont.isDisposed())
		{
			styledFont.dispose();
		}
		if (defaultStyledFont != null && !defaultStyledFont.isDisposed())
		{
			defaultStyledFont.dispose();
		}
	}

}
