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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.ide.syncing.ui.old.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;

/**
 * @author Kevin Sawicki (ksawicki@aptana.com)
 * @author Michael Xia (mxia@aptana.com)
 */
public class OptionsToolBar
{

	/**
	 * The client to receive feedback from the toolbar
	 */
	public static interface Client
	{
		/**
		 * Indicates the state of the toolbar has changed.
		 */
		public void stateChanged(int type);

		/**
		 * Indicates the menu selection for showing the dates has changed.
		 * 
		 * @param show
		 *            true if the menu is checked, false otherwise
		 */
		public void showDatesSelected(boolean show);
	}

	/**
	 * FLAT_VIEW
	 */
	public static final int FLAT_VIEW = 0;

	/**
	 * TREE_VIEW
	 */
	public static final int TREE_VIEW = 1;
	// public static final int COMPRESSED_VIEW = 2;

	private ToolBar fOptionsBar;
	private ToolItem fDropdown;
	private MenuItem fFlatView;
	private MenuItem fTreeView;
	// private MenuItem compressedMenuItem;
	private MenuItem fShowDates;

	private Client fClient;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param client
	 *            the client to be notified for possible events
	 */
	public OptionsToolBar(Composite parent, Client client)
	{
		fOptionsBar = createContents(parent);
		fClient = client;
	}

	/**
	 * Returns the type of presentation.
	 * 
	 * @return the type of presentation (could be FLAT_VIEW or TREE_VIEW)
	 */
	public int getPresentationType()
	{
		if (fTreeView.getSelection())
		{
			return TREE_VIEW;
		}
		return FLAT_VIEW;
	}

	/**
	 * Sets the current type of presentation.
	 * 
	 * @param type
	 *            the specific presentation type to be set (could be FLAT_VIEW or TREE_VIEW)
	 */
	public void setPresentationType(int type)
	{
		if (type == FLAT_VIEW)
		{
			fFlatView.setSelection(true);
		}
		else if (type == TREE_VIEW)
		{
			fTreeView.setSelection(true);
		}
	}

	/**
	 * Sets if showing of the modification dates is selected.
	 * 
	 * @param selected
	 *            true if it is to be selected, false otherwise
	 */
	public void setShowDatesSelected(boolean selected)
	{
		fShowDates.setSelection(selected);
	}

	/**
	 * Sets the tool bar to be enabled or disabled.
	 * 
	 * @param enabled
	 *            true if the tool bar is to be enabled, false for disabling it
	 */
	public void setEnabled(boolean enabled)
	{
		if (!fOptionsBar.isDisposed())
		{
			fOptionsBar.setEnabled(enabled);
		}
	}

	private ToolBar createContents(final Composite parent)
	{
		final ToolBar optionsBar = new ToolBar(parent, SWT.FLAT);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		optionsBar.setLayout(layout);

		fDropdown = new ToolItem(optionsBar, SWT.DROP_DOWN);
		// fDropdown.setText(Messages.SmartSyncDialog_ViewOptions);
		fDropdown.setToolTipText(Messages.SmartSyncDialog_OptionsToolTip);
		fDropdown.setImage(SyncingUIPlugin.getImage("icons/full/obj16/configure.gif")); //$NON-NLS-1$

		final Menu menu = new Menu(optionsBar);
		fDropdown.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				Rectangle rect = optionsBar.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = parent.toDisplay(pt);
				menu.setLocation(pt.x, pt.y);
				menu.setVisible(true);
			}

		});

		fFlatView = new MenuItem(menu, SWT.RADIO);
		fFlatView.setText(Messages.SmartSyncDialog_FlatView);
		fFlatView.setImage(SyncingUIPlugin.getImage("icons/full/obj16/flatView.gif")); //$NON-NLS-1$

		SelectionAdapter refreshAdapter = new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				fClient.stateChanged(getPresentationType());
			}

		};
		fFlatView.addSelectionListener(refreshAdapter);

		fTreeView = new MenuItem(menu, SWT.RADIO);
		fTreeView.setText(Messages.SmartSyncDialog_TreeView);
		fTreeView.setImage(SyncingUIPlugin.getImage("icons/full/obj16/treeView.gif")); //$NON-NLS-1$
		fTreeView.addSelectionListener(refreshAdapter);

		fShowDates = new MenuItem(menu, SWT.CHECK);
		fShowDates.setText(Messages.SmartSyncDialog_ShowDates);
		fShowDates.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				fClient.showDatesSelected(fShowDates.getSelection());
			}

		});

		return optionsBar;
	}

}
