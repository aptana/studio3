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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextViewer;

import com.aptana.theme.IControlThemer;
import com.aptana.theme.IControlThemerFactory;

public class ControlThemerFactory implements IControlThemerFactory
{

	private Map<ITextViewer, IControlThemer> themers = new HashMap<ITextViewer, IControlThemer>();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.theme.IControlThemerFactory#dispose()
	 */
	public void dispose()
	{
		synchronized (themers)
		{
			for (ITextViewer control : new ArrayList<ITextViewer>(themers.keySet()))
			{
				dispose(control);
			}
			themers.clear();
		}
	}

	public void apply(ITextViewer viewer)
	{
		if (viewer instanceof TextViewer)
		{
			// If a themer already exists for the control, just return it
			IControlThemer themer = themers.get(viewer);
			if (themer != null)
			{
				return;
			}

			// No themer exists, create a new one
			themer = new TextViewerThemer(viewer);
			synchronized (themers)
			{
				themers.put(viewer, themer);
			}
			themer.apply();
		}
	}

	public void dispose(ITextViewer viewer)
	{
		IControlThemer themer;
		synchronized (themers)
		{
			themer = themers.remove(viewer);
		}
		if (themer != null)
		{
			themer.dispose();
		}
	}

}
