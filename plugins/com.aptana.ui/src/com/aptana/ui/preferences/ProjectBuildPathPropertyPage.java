/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.preferences;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

import com.aptana.buildpath.core.BuildPathEntry;
import com.aptana.buildpath.core.BuildPathManager;
import com.aptana.buildpath.core.IBuildPathEntry;
import com.aptana.ui.IDialogConstants;

public class ProjectBuildPathPropertyPage extends PropertyPage implements IWorkbenchPropertyPage
{
	private IProject project;
	private CheckboxTableViewer tableViewer;
	private Button upButton;
	private Button downButton;
	private SelectionListener buttonListener;
	private List<IBuildPathEntry> selectedEntries;

	/**
	 * ProjectBuildPathPropertyPage
	 */
	public ProjectBuildPathPropertyPage()
	{
		buttonListener = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				handleButtonPressed((Button) e.widget);
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		// get project
		project = (IProject) getElement().getAdapter(IResource.class);

		// get entire list and selected items in that list
		Set<IBuildPathEntry> entries = getBuildPathEntries(project);
		selectedEntries = new ArrayList<IBuildPathEntry>(getSelectedBuildPathEntries(project));

		// top-level composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// labels
		setDescription(MessageFormat.format(Messages.ProjectBuildPathPropertyPage_TableDescription, project.getName()));
		Label description = createDescriptionLabel(composite);
		description.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		// table composite
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 0).create());
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// table
		tableViewer = CheckboxTableViewer.newCheckList(tableComposite, SWT.TOP | SWT.BORDER);
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		table.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				handleTableSelection();
			}
		});
		table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseUp(MouseEvent e)
			{
				// Force a row selection when the user checks an item on an unselected row.
				TableItem item = table.getItem(new Point(e.x, e.y));
				if (item != null)
				{
					table.select(table.indexOf(item));
				}
			}
		});

		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setText(Messages.ProjectBuildPathPropertyPage_LibraryColumnLabel);
		column1.setWidth(165);

		TableColumn column2 = new TableColumn(table, SWT.LEFT);
		column2.setText(Messages.ProjectBuildPathPropertyPage_PathColumnLabel);
		column2.setWidth(350);

		// Up and Down buttons
		Composite buttonArea = new Composite(tableComposite, SWT.NONE);
		buttonArea.setLayout(GridLayoutFactory.fillDefaults().margins(0, 0).create());
		buttonArea.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.FILL).create());
		upButton = createButton(buttonArea, Messages.ProjectBuildPathPropertyPage_up, false);
		downButton = createButton(buttonArea, Messages.ProjectBuildPathPropertyPage_down, false);

		tableViewer.setContentProvider(getContentProvider());
		tableViewer.setLabelProvider(getLabelProvider());
		tableViewer.setInput(entries);
		tableViewer.setCheckedElements(selectedEntries.toArray());
		tableViewer.setComparator(new CheckPriorityComparator());

		tableViewer.addCheckStateListener(new ICheckStateListener()
		{

			public void checkStateChanged(CheckStateChangedEvent event)
			{
				tableViewer.refresh();
			}
		});

		return composite;
	}

	/**
	 * Creates a button that will be located by the table.
	 * 
	 * @param parent
	 * @param text
	 * @param enabled
	 * @return A {@link Button}
	 */
	private Button createButton(Composite parent, String text, boolean enabled)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.addSelectionListener(buttonListener);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		button.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.hint(Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x), SWT.DEFAULT).create());
		button.setEnabled(enabled);
		return button;
	}

	/**
	 * Handle a button selection.
	 * 
	 * @param button
	 */
	private void handleButtonPressed(Button button)
	{
		// With the Up and Down buttons, we already know the selection if for one item. Otherwise, those buttons will
		// not be enabled, so we just need to shift the item.
		if (button == upButton)
		{
			moveSelectedItem(true);
		}
		else if (button == downButton)
		{
			moveSelectedItem(false);
		}

		handleTableSelection();
		tableViewer.getTable().setFocus();
	}

	/**
	 * Move a selected item.
	 * 
	 * @param up
	 *            <code>true</code> for up, <code>false</code> for down.
	 */
	private void moveSelectedItem(boolean up)
	{
		Table table = tableViewer.getTable();
		int selectionIndex = table.getSelectionIndex();
		int newIndex = selectionIndex + (up ? -1 : 1);
		TableItem item = table.getItem(selectionIndex);
		Object data = item.getData();
		item.dispose();
		int selectedEntriesItemIndex = selectedEntries.indexOf(data);
		if (selectedEntriesItemIndex > -1)
		{
			// move it in the selected array as well, so our sorting will work correctly.
			if (up && selectedEntriesItemIndex > 0)
			{
				IBuildPathEntry toMove = selectedEntries.remove(selectedEntriesItemIndex);
				selectedEntries.add(selectedEntriesItemIndex - 1, toMove);
			}
			else if (!up && selectedEntriesItemIndex < selectedEntries.size() - 1)
			{
				IBuildPathEntry toMove = selectedEntries.remove(selectedEntriesItemIndex);
				selectedEntries.add(selectedEntriesItemIndex + 1, toMove);
			}
		}
		tableViewer.insert(data, newIndex);
		tableViewer.setChecked(data, selectedEntriesItemIndex > -1);
		table.setSelection(newIndex);
	}

	/**
	 * Handle table selection. In case it's a single selection, enable/disable the 'Up' and 'Down' buttons according to
	 * the selection. We only allow up and down for checked items.
	 */
	private void handleTableSelection()
	{
		ISelection selection = tableViewer.getSelection();
		if (selection instanceof StructuredSelection)
		{
			StructuredSelection structuredSelection = (StructuredSelection) selection;
			Table table = tableViewer.getTable();
			if (structuredSelection.size() == 1 && table.getItemCount() > 1)
			{
				int selectionIndex = table.getSelectionIndex();
				TableItem item = table.getItem(selectionIndex);
				IBuildPathEntry data = (IBuildPathEntry) item.getData();
				if (item.getChecked())
				{
					upButton.setEnabled(selectionIndex != 0);
					downButton.setEnabled(selectionIndex < table.getItemCount() - 1
							&& selectionIndex < tableViewer.getCheckedElements().length - 1);
					if (!selectedEntries.contains(data))
					{
						selectedEntries.add(data);
						tableViewer.refresh();
					}
				}
				else
				{
					if (selectedEntries.contains(data))
					{
						selectedEntries.remove(data);
						tableViewer.refresh();
					}
					upButton.setEnabled(false);
					downButton.setEnabled(false);
				}
			}
			else
			{
				upButton.setEnabled(false);
				downButton.setEnabled(false);
			}
		}
	}

	/**
	 * getBuildPathEntries
	 * 
	 * @param project
	 * @return
	 */
	private Set<IBuildPathEntry> getBuildPathEntries(IProject project)
	{
		return BuildPathManager.getInstance().getBuildPaths();
	}

	/**
	 * getSelectedBuildPathEntries
	 * 
	 * @param project
	 * @return
	 */
	private Set<IBuildPathEntry> getSelectedBuildPathEntries(IProject project)
	{
		return BuildPathManager.getInstance().getBuildPaths(project);
	}

	/**
	 * getContentProvider
	 * 
	 * @return
	 */
	private IStructuredContentProvider getContentProvider()
	{
		return new BaseWorkbenchContentProvider()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.ui.model.BaseWorkbenchContentProvider#getChildren(java.lang.Object)
			 */
			@Override
			public Object[] getChildren(Object element)
			{
				if (element instanceof Set<?>)
				{
					return ((Set<?>) element).toArray();
				}
				return super.getChildren(element);
			}
		};
	}

	/**
	 * getLabelProvider
	 * 
	 * @return
	 */
	private IBaseLabelProvider getLabelProvider()
	{
		return new ITableLabelProvider()
		{

			public void addListener(ILabelProviderListener listener)
			{
			}

			public void dispose()
			{
			}

			public boolean isLabelProperty(Object element, String property)
			{
				return false;
			}

			public void removeListener(ILabelProviderListener listener)
			{
			}

			public Image getColumnImage(Object element, int columnIndex)
			{
				return null;
			}

			public String getColumnText(Object element, int columnIndex)
			{
				if (element instanceof BuildPathEntry)
				{
					BuildPathEntry entry = (BuildPathEntry) element;

					switch (columnIndex)
					{
						case 0:
							return entry.getDisplayName();
						case 1:
							String result = entry.getPath().toString();

							if (result != null && result.startsWith("file:")) //$NON-NLS-1$
							{
								File f = new File(entry.getPath());
								return f.getAbsolutePath();
							}
							return result;
					}
				}

				return null;
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		Object[] items = tableViewer.getCheckedElements();
		List<IBuildPathEntry> entries = new ArrayList<IBuildPathEntry>();

		for (Object item : items)
		{
			if (item instanceof IBuildPathEntry)
			{
				entries.add((IBuildPathEntry) item);
			}
		}

		// FIXME - Make sure that the saved items are in order.

		BuildPathManager manager = BuildPathManager.getInstance();

		// determine if the selection, or order, has changed
		Set<IBuildPathEntry> currentEntries = manager.getBuildPaths(project);
		Set<IBuildPathEntry> newEntries = new LinkedHashSet<IBuildPathEntry>(entries);

		if (!Arrays.equals(currentEntries.toArray(new IBuildPathEntry[currentEntries.size()]),
				newEntries.toArray(new IBuildPathEntry[newEntries.size()])))
		{
			manager.setBuildPaths(project, entries);

			// // FIXME We shouldn't be doing this in the UI thread! Prompt to rebuild after like we do with
			// validators!
			// // rebuild project index
			// RebuildIndexJob job = new RebuildIndexJob(project.getLocationURI());
			//
			// job.run(new NullProgressMonitor());
		}

		return true;
	}

	/**
	 * A comparator that place the checked items on the top, while maintaining the selection order in those items as
	 * well.
	 */
	private class CheckPriorityComparator extends ViewerComparator
	{
		@Override
		public int compare(Viewer viewer, Object e1, Object e2)
		{
			int e1Index = selectedEntries.indexOf(e1);
			int e2Index = selectedEntries.indexOf(e2);
			if (e1Index == -1 && e2Index == -1)
			{
				return super.compare(viewer, e1, e2);
			}
			if (e1Index == -1)
			{
				return 1;
			}
			if (e2Index == -1)
			{
				return -1;
			}
			return e1Index - e2Index;
		}
	}
}
