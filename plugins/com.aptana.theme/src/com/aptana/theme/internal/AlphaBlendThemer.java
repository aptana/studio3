/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

import com.aptana.theme.Theme;

/**
 * Extends the ControlThemer behavior to alpha blend the foreground color with the background color
 * 
 * @author nle
 */
public class AlphaBlendThemer extends ControlThemer
{
	private int alpha;

	public AlphaBlendThemer(Control control, int alpha, Color defaultBg)
	{
		super(control, defaultBg);
		this.alpha = alpha;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.internal.ControlThemer#applyControlColors()
	 */
	@Override
	protected void applyControlColors()
	{
		super.applyControlColors();
		setBlendedColor();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.internal.ControlThemer#unapplyControlColors()
	 */
	@Override
	protected void unapplyControlColors()
	{
		super.unapplyControlColors();
		setBlendedColor();
	}

	private void setBlendedColor()
	{
		if (!controlIsDisposed())
		{
			Control c = getControl();
			c.setForeground(getColorManager().getColor(
					Theme.alphaBlend(c.getBackground().getRGB(), c.getForeground().getRGB(), alpha)));
		}
	}
}
