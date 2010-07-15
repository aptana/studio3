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
