/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.explorer.internal.ui;

import java.util.Comparator;

import org.eclipse.core.resources.IProject;

public class CaseInsensitiveProjectComparator implements Comparator<IProject>
{

	public int compare(IProject o1, IProject o2)
	{
		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}
