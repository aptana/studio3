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
package com.aptana.ui.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.SWTUtils;
import com.aptana.ui.UIPlugin;

/**
 * @author Ingo Muschenetz
 * @author Michael Xia
 */
public class CListTable extends Composite
{

	public static interface Listener
	{

		public Object addItem();

		public Object editItem(Object item);
	}

	private Button addButton;
	private Button editButton;
	private Button removeButton;
	private Label descriptionLabel;
	private TableViewer tableViewer;
	private List<Object> items;

	private List<Listener> listeners;

	/**
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            the style bits
	 */
	public CListTable(Composite parent, int style)
	{
		super(parent, style);
		items = new ArrayList<Object>();
		listeners = new ArrayList<Listener>();

		setLayout(GridLayoutFactory.fillDefaults().create());

		descriptionLabel = new Label(this, SWT.WRAP);
		descriptionLabel.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
		buttons.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		addButton = new Button(buttons, SWT.PUSH);
		addButton.setToolTipText(StringUtil.ellipsify(CoreStrings.ADD));
		addButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean itemAdded = false;
				Object newItem = null;
				for (Listener listener : listeners)
				{
					newItem = listener.addItem();
					if (newItem != null)
					{
						items.add(newItem);
						itemAdded = true;
					}
				}
				if (itemAdded)
				{
					tableViewer.refresh();
					tableViewer.setSelection(new StructuredSelection(newItem));
				}
			}
		});
		addButton.setImage(SWTUtils.getImage(UIPlugin.getDefault(), "/icons/add.gif")); //$NON-NLS-1$

		editButton = new Button(buttons, SWT.PUSH);
		editButton.setToolTipText(StringUtil.ellipsify(CoreStrings.EDIT));
		editButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (!selection.isEmpty())
				{
					Object element = selection.getFirstElement();
					items.remove(element);
					Object newElement = null;
					boolean changed = false;
					for (Listener listener : listeners)
					{
						newElement = listener.editItem(element);
						if (newElement != null)
						{
							items.add(newElement);
							changed = true;
						}
					}
					if (changed)
					{
						tableViewer.refresh();
						tableViewer.setSelection(new StructuredSelection(newElement));
					}
				}
			}

		});
		editButton.setImage(SWTUtils.getImage(UIPlugin.getDefault(), "/icons/edit.png")); //$NON-NLS-1$

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setToolTipText(CoreStrings.REMOVE);
		removeButton.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if (!selection.isEmpty())
				{
					Object element = selection.getFirstElement();
					items.remove(element);
					tableViewer.refresh();
					updateStates();
				}
			}
		});
		removeButton.setImage(SWTUtils.getImage(UIPlugin.getDefault(), "/icons/delete.gif")); //$NON-NLS-1$

		createTable(this);
	}

	public void addListener(Listener listener)
	{
		if (!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}

	public void removeListener(Listener listener)
	{
		listeners.remove(listener);
	}

	public List<Object> getItems()
	{
		return Collections.unmodifiableList(items);
	}

	public void setDescription(String description)
	{
		descriptionLabel.setText(description);
	}

	public void setItems(Object[] items)
	{
		this.items.clear();
		for (Object item : items)
		{
			this.items.add(item);
		}
		tableViewer.refresh();
	}

	private void createTable(Composite parent)
	{
		tableViewer = new TableViewer(parent, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setComparator(new ViewerComparator());
		tableViewer.setInput(items);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				updateStates();
			}

		});
		updateStates();
	}

	private void updateStates()
	{
		boolean hasSelection = !tableViewer.getSelection().isEmpty();
		editButton.setEnabled(hasSelection);
		removeButton.setEnabled(hasSelection);
	}

	public class ContentProvider implements IStructuredContentProvider
	{

		public void dispose()
		{
		}

		public Object[] getElements(Object inputElement)
		{
			return items.toArray(new Object[items.size()]);
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}
}
