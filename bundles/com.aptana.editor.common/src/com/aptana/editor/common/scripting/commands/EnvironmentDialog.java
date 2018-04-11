/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.scripting.commands;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

class EnvironmentDialog extends Dialog
{

	private Map<String, String> environment;

	private Color oddRowColor;

	protected EnvironmentDialog(Shell shell, Map<String, String> environment)
	{
		super(shell);
		this.environment = new TreeMap<String, String>();
		this.environment.putAll(environment);
		this.environment.putAll(new ProcessBuilder("").environment()); //$NON-NLS-1$
	}

	@Override
	public boolean close()
	{
		boolean close = super.close();
		if (oddRowColor != null)
		{
			oddRowColor.dispose();
			oddRowColor = null;
		}
		return close;
	}

	@Override
	protected boolean isResizable()
	{
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new FillLayout());

		Table table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		TableColumn valueColumn = new TableColumn(table, SWT.NONE);
		TableItem item;

		for (String name : environment.keySet())
		{
			String value = environment.get(name);
			item = new TableItem(table, SWT.NONE);
			item.setText(new String[] { name, value });
		}

		table.setSelection(0);
		table.deselectAll();

		if (oddRowColor == null)
		{
			oddRowColor = new Color(parent.getDisplay(), 240, 240, 250);
		}
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (i % 2 == 0)
			{
				items[i].setBackground(oddRowColor);
			}
		}

		nameColumn.pack();
		valueColumn.pack();

		return composite;
	}
}
