/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class AptanaPreferencePage extends GenericRootPreferencePage
{

	protected static final String PAGE_ID = "com.aptana.ui.AptanaPreferencePage"; //$NON-NLS-1$

	@Override
	protected String getPageId()
	{
		return PAGE_ID;
	}

	/**
	 * Creates a field editor group for use in grouping items on a page
	 * 
	 * @param appearanceComposite
	 * @param string
	 * @return Composite
	 */
	public static Composite createGroup(Composite appearanceComposite, String string)
	{
		Group group = new Group(appearanceComposite, SWT.NONE);
		group.setFont(appearanceComposite.getFont());
		group.setText(string);

		group.setLayout(GridLayoutFactory.fillDefaults().margins(5, 5).numColumns(2).create());
		group.setLayoutData(GridDataFactory.fillDefaults().span(2, 0).grab(true, false).create());

		Composite c = new Composite(group, SWT.NONE);
		c.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

		return c;
	}
}
