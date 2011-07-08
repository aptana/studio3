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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
		setDescription("This is the description");
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
		table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setWidth(350);

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
		return new LabelProvider()
		{
			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
			 */
			@Override
			public Image getImage(Object element)
			{
				return super.getImage(element);
			}

			/*
			 * (non-Javadoc)
			 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element)
			{
				String result;

				if (element instanceof BuildPathEntry)
				{
					result = ((BuildPathEntry) element).getDisplayName();
				}
				else
				{
					result = super.getText(element);
				}

				return result;
			}
		};
	}
}
