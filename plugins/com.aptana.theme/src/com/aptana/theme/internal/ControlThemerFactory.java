/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.aptana.theme.IControlThemer;
import com.aptana.theme.IControlThemerFactory;

public class ControlThemerFactory implements IControlThemerFactory
{

	private Map<Control, IControlThemer> themers = new HashMap<Control, IControlThemer>();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#apply(org.eclipse.swt.widgets.Control)
	 */
	public void apply(Control control)
	{
		apply(control, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#apply(org.eclipse.swt.widgets.Control,
	 * org.eclipse.swt.graphics.Color)
	 */
	public void apply(Control control, Color defaultBg)
	{
		apply(control, SWT.NONE, -1, defaultBg);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#applyWithStyle(org.eclipse.swt.widgets.Control, int)
	 */
	public void applyWithFontStyle(Control control, int fontStyle)
	{
		applyWithAlpha(control, fontStyle, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#applyWithFontStyle(org.eclipse.swt.widgets.Control, int)
	 */
	public void applyWithFontStyle(Control control, int fontStyle, Color defaultBg)
	{
		apply(control, fontStyle, -1, defaultBg);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#applyWithAlpha(org.eclipse.swt.widgets.Control, int)
	 */
	public void applyWithAlpha(Control control, int alpha)
	{
		applyWithAlpha(control, alpha, null);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#applyWithAlpha(org.eclipse.swt.widgets.Control, int,
	 * org.eclipse.swt.graphics.Color)
	 */
	public void applyWithAlpha(Control control, int alpha, Color defaultBg)
	{
		apply(control, SWT.NONE, alpha, defaultBg);
	}

	private void apply(Control control, int fontStyle, int alpha, Color defaultBg)
	{
		// If a themer already exists for the control, just return it
		IControlThemer themer = themers.get(control);
		if (themer != null)
		{
			return;
		}

		themer = createThemer(control, fontStyle, alpha, defaultBg);
		synchronized (themers)
		{
			themers.put(control, themer);
		}
		themer.apply();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#dispose(org.eclipse.swt.widgets.Control)
	 */
	public void dispose(Control control)
	{
		IControlThemer themer = null;
		synchronized (themers)
		{
			themer = themers.remove(control);
		}
		if (themer != null)
		{
			themer.dispose();
		}
	}

	private IControlThemer createThemer(Control control, int fontStyle, int alpha, Color defaultBg)
	{
		// No themer exists, create a new one
		if (control instanceof Tree)
		{
			return new TreeThemer((Tree) control);
		}
		if (control instanceof Table)
		{
			return new TableThemer((Table) control);
		}
		if (fontStyle != SWT.NONE)
		{
			return new StyledFontThemer(control, fontStyle, defaultBg);
		}
		if (alpha > -1)
		{
			return new AlphaBlendThemer(control, alpha, defaultBg);
		}

		return new ControlThemer(control, defaultBg);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#dispose()
	 */
	public void dispose()
	{
		synchronized (themers)
		{
			for (Control control : new ArrayList<Control>(themers.keySet()))
			{
				dispose(control);
			}
			themers.clear();
		}
	}

	public void apply(Viewer viewer)
	{
		if (viewer instanceof TreeViewer)
		{
			// If a themer already exists for the control, just return it
			IControlThemer themer = themers.get(viewer.getControl());
			if (themer != null)
			{
				return;
			}

			// No themer exists, create a new one
			themer = new TreeThemer((TreeViewer) viewer);
			synchronized (themers)
			{
				themers.put(viewer.getControl(), themer);
			}
			themer.apply();
		}
		if (viewer instanceof TextViewer)
		{
			// If a themer already exists for the control, just return it
			IControlThemer themer = themers.get(viewer.getControl());
			if (themer != null)
			{
				return;
			}

			// No themer exists, create a new one
			themer = new TextViewerThemer((TextViewer) viewer);
			synchronized (themers)
			{
				themers.put(viewer.getControl(), themer);
			}
			themer.apply();
		}
		else
		{
			apply(viewer.getControl());
		}
	}

	public void dispose(Viewer viewer)
	{
		dispose(viewer.getControl());
	}

}
