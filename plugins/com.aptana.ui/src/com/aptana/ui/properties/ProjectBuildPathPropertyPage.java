package com.aptana.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

public class ProjectBuildPathPropertyPage extends PropertyPage implements IWorkbenchPropertyPage, ICheckStateListener
{
	/**
	 * ProjectBuildPathPropertyPage
	 */
	public ProjectBuildPathPropertyPage()
	{
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.jface.viewers.ICheckStateListener#checkStateChanged(org.eclipse.jface.viewers.CheckStateChangedEvent)
	 */
	public void checkStateChanged(CheckStateChangedEvent event)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent)
	{
		// get project
		IProject project = (IProject) getElement().getAdapter(IResource.class);
		List<BuildPathEntry> entries = getBuildPathEntries(project);

		// top-level composite
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// labels
		setDescription("Project build path for '" + project.getName() + "':");
		Label description = createDescriptionLabel(composite);
		description.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		// table composite
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		// table
		CheckboxTableViewer tableViewer = CheckboxTableViewer.newCheckList(tableComposite, SWT.TOP | SWT.BORDER);
		Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		TableColumn column1 = new TableColumn(table, SWT.LEFT);
		column1.setText("Library");
		column1.setWidth(150);

		TableColumn column2 = new TableColumn(table, SWT.LEFT);
		column2.setText("Path");
		column2.setWidth(350);

		tableViewer.setContentProvider(getContentProvider());
		tableViewer.setLabelProvider(getLabelProvider());
		// tableViewer.setComparator(getCompartor());
		tableViewer.setInput(entries);
		// tableViewer.setCheckedElements(elements);
		tableViewer.addCheckStateListener(this);
		// @formatter:off
		tableViewer.addSelectionChangedListener(
			new ISelectionChangedListener()
			{
				public void selectionChanged(SelectionChangedEvent event)
				{
				}
			}
		);
		// @formatter:on
		// initialCheckedItems = tableViewer.getCheckedElements();

		return composite;
	}

	private List<BuildPathEntry> getBuildPathEntries(IProject project)
	{
		List<BuildPathEntry> result = new ArrayList<BuildPathEntry>();

		result.add(new BuildPathEntry("JS Library, v1.0", "/Users/klindsey/Documents/testing/test.js"));

		return result;
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
				if (element instanceof List<?>)
				{
					return ((List<?>) element).toArray();
				}
				else
				{
					return super.getChildren(element);
				}
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
				String result = null;

				if (element instanceof BuildPathEntry)
				{
					BuildPathEntry entry = (BuildPathEntry) element;

					switch (columnIndex)
					{
						case 0:
							result = entry.getDisplayName();
							break;

						case 1:
							result = entry.getPath();
							break;
					}
				}

				return result;
			}
		};
	}
}
