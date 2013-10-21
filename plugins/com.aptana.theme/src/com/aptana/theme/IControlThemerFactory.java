/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme;

import org.eclipse.jface.text.ITextViewer;

public interface IControlThemerFactory
{

	public abstract void apply(ITextViewer viewer);

	public abstract void dispose(ITextViewer viewer);

	public abstract void dispose();

}