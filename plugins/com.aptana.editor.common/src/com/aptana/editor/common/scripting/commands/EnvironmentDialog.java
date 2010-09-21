/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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

	private static Color ODD_ROW_COLOR;

	protected EnvironmentDialog(Shell shell, Map<String, String> environment)
	{
		super(shell);
		this.environment = new TreeMap<String, String>();
		this.environment.putAll(environment);
		this.environment.putAll(new ProcessBuilder("").environment()); //$NON-NLS-1$
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

		ODD_ROW_COLOR = new Color(parent.getDisplay(), 240, 240, 250);
		TableItem[] items = table.getItems();
		for (int i = 0; i < items.length; i++)
		{
			if (i % 2 == 0)
			{
				items[i].setBackground(ODD_ROW_COLOR);
			}
		}

		nameColumn.pack();
		valueColumn.pack();

		return composite;
	}
}
