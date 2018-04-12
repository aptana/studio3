/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;
import com.aptana.ui.util.SWTUtils;

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

		public void itemsChanged(List<Object> rawFilters);
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
					for (Listener listener : listeners)
					{
						listener.itemsChanged(getItems());
					}
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
						for (Listener listener : listeners)
						{
							listener.itemsChanged(getItems());
						}
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
				Object[] elements = selection.toArray();
				for (Object element : elements)
				{
					items.remove(element);
				}
				tableViewer.refresh();
				updateStates();
				for (Listener listener : listeners)
				{
					listener.itemsChanged(getItems());
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
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
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
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		boolean hasSelection = !selection.isEmpty();
		boolean hasMultiSelections = selection.size() > 1;
		editButton.setEnabled(hasSelection && !hasMultiSelections);
		removeButton.setEnabled(hasSelection);
		addButton.setEnabled(true);
	}
	
	public void setEnabled(boolean enabled)
	{
		if (!enabled)
		{
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
		else
		{
			updateStates();
		}
		tableViewer.getTable().setEnabled(enabled);
		super.setEnabled(enabled);
	}
}
