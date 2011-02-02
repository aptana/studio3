/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;

public interface IControlThemerFactory
{

	public abstract void apply(Control control);

	public abstract void dispose(Control control);
	
	public abstract void apply(Viewer viewer);

	public abstract void dispose(Viewer viewer);

	public abstract void dispose();

}