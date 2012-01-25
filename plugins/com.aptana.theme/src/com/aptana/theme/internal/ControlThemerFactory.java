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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
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
		apply(control, SWT.NONE, -1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#applyWithStyle(org.eclipse.swt.widgets.Control, int)
	 */
	public void applyWithFontStyle(Control control, int fontStyle)
	{
		apply(control, fontStyle, -1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#applyWithAlpha(org.eclipse.swt.widgets.Control, int)
	 */
	public void applyWithAlpha(Control control, int alpha)
	{
		apply(control, SWT.NONE, alpha);
	}

	private void apply(Control control, int fontStyle, int alpha)
	{
		// If a themer already exists for the control, just return it
		IControlThemer themer = themers.get(control);
		if (themer != null)
		{
			return;
		}

		themer = createThemer(control, fontStyle, alpha);
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

	private IControlThemer createThemer(Control control, int fontStyle, int alpha)
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
			return new StyledFontThemer(control, fontStyle);
		}
		if (alpha > -1)
		{
			return new AlphaBlendThemer(control, alpha);
		}

		return new ControlThemer(control);
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
			themer =  new TreeThemer((TreeViewer) viewer);
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
