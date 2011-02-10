/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.theme.internal;

import org.eclipse.swt.widgets.Table;

class TableThemer extends ControlThemer
{

	public TableThemer(Table table)
	{
		super(table);
	}

	@Override
	public void apply()
	{
		addSelectionColorOverride();
		super.apply();
	}

	@Override
	public void dispose()
	{
		removeSelectionOverride();
		super.dispose();
	}

	protected Table getTable()
	{
		return (Table) getControl();
	}
}
