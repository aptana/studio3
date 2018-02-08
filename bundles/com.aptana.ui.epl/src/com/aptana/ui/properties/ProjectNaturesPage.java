/*******************************************************************************
 * Copyright (c) 2012 Gunnar Wagenknecht and others.
 * All rights reserved. This program and the accompanying materials

 * are made available under the terms of the Eclipse Public License v1.0

 * which accompanies this distribution, and is available at

 * http://www.eclipse.org/legal/epl-v10.html

 *

 * Contributors:

 *     IBM Corporation - initial API and implementation

 *     Gunnar Wagenknecht - initial API and implementation
 
 *     Shalom Gibly - Aptana additions and modifications

 ******************************************************************************/

package com.aptana.ui.properties;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNatureDescriptor;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.actions.CloseResourceAction;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.progress.ProgressMonitorJobsDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ResourceUtil;
import com.aptana.ui.epl.UIEplPlugin;

@SuppressWarnings("restriction")
public class ProjectNaturesPage extends PropertyPage implements IWorkbenchPropertyPage, ICheckStateListener,
		SelectionListener
{
	private CheckboxTableViewer fTableViewer;
	private MenuItem fSetPrimaryMenuItem;
	private Button fMakePrimaryButton;

	private IProject fProject;
	private String[] fCurrentProjectNatures;
	private String fPrimaryNature;
	// a map between nature id and its text description
	private Map<String, String> fNatureDescriptions;

	private Object[] fInitialCheckedItems;
	private String fInitialPrimaryNature;
	private boolean fNaturesModified;
	private NaturesLabelProvider fLabelProvider;

	public ProjectNaturesPage()
	{
		fNatureDescriptions = new HashMap<String, String>();
	}

	@Override
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		fProject = (IProject) getElement().getAdapter(IResource.class);
		try
		{
			if (fProject.isOpen())
			{
				// Can only access decription if project exists and is open...
				fCurrentProjectNatures = fProject.getDescription().getNatureIds();
			}
			else
			{
				fCurrentProjectNatures = new String[0];
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(UIEplPlugin.getDefault(), EplMessages.ProjectNaturesPage_ERR_RetrieveNatures, e);
			fCurrentProjectNatures = new String[0];
		}
		fLabelProvider = new NaturesLabelProvider(fNatureDescriptions);

		// assumes the first one in the array is the primary nature
		fInitialPrimaryNature = fCurrentProjectNatures.length == 0 ? null : fCurrentProjectNatures[0];
		updatePrimaryNature(fInitialPrimaryNature);

		setDescription(MessageFormat.format(EplMessages.ProjectNaturesPage_Description, fProject.getName()));
		Label description = createDescriptionLabel(composite);
		description.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
		tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		fTableViewer = CheckboxTableViewer.newCheckList(tableComposite, SWT.TOP | SWT.BORDER);
		Table table = fTableViewer.getTable();
		table.setLinesVisible(true);
		table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		TableColumn column = new TableColumn(table, SWT.LEFT);
		column.setWidth(350);

		fTableViewer.setContentProvider(getContentProvider());
		fTableViewer.setLabelProvider(getLabelProvider());
		fTableViewer.setComparator(getViewerComperator());
		fTableViewer.setInput(fProject.getWorkspace());
		if (!fProject.isAccessible())
		{
			fTableViewer.getControl().setEnabled(false);
		}
		fTableViewer.setCheckedElements(fCurrentProjectNatures);
		fTableViewer.addCheckStateListener(this);
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateButtons();
			}
		});
		fInitialCheckedItems = fTableViewer.getCheckedElements();
		table.setMenu(createMenu(table));

		// Add the buttons
		Composite buttons = new Composite(tableComposite, SWT.NONE);
		buttons.setLayout(GridLayoutFactory.fillDefaults().create());
		buttons.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());
		fMakePrimaryButton = createButton(EplMessages.ProjectNaturesPage_LBL_MakePrimary, buttons);
		updateButtons();

		noDefaultAndApplyButton();

		return composite;
	}

	@Override
	public boolean performOk()
	{
		if (!fNaturesModified && !isPrimaryNatureModified())
		{
			return true;
		}

		Object[] checkedNatures = fTableViewer.getCheckedElements();
		final List<String> natureIds = new ArrayList<String>();
		for (Object nature : checkedNatures)
		{
			natureIds.add(nature.toString());
		}
		// promotes the primary nature to the front
		if (fPrimaryNature != null)
		{
			natureIds.remove(fPrimaryNature);
			natureIds.add(0, fPrimaryNature);
		}

		// set nature ids on the project
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				try
				{
					IProjectDescription description = fProject.getDescription();
					description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));
					fProject.setDescription(description, monitor);
				}
				catch (CoreException e)
				{
					throw new InvocationTargetException(e);
				}
			}
		};
		try
		{
			// this will block until the progress is done
			new ProgressMonitorJobsDialog(getControl().getShell()).run(true, true, runnable);
		}
		catch (InterruptedException e)
		{
			// ignore
		}
		catch (InvocationTargetException e)
		{
			IdeLog.logError(UIEplPlugin.getDefault(), EplMessages.ProjectNaturesPage_ERR_SetNatures, e);
			return false;
		}
		resetProject();
		return true;
	}

	public void checkStateChanged(CheckStateChangedEvent event)
	{
		// Check if the current checked items are the same as the initial ones.
		Object[] checkedElements = fTableViewer.getCheckedElements();
		fNaturesModified = !Arrays.equals(fInitialCheckedItems, checkedElements);
		if (fPrimaryNature == null)
		{
			// in case that the item was checked, set it as the primary
			if (event.getChecked())
			{
				updatePrimaryNature(event.getElement().toString());
				fTableViewer.refresh();
			}
		}
		else
		{
			if (!event.getChecked() && isPrimary(event.getElement()))
			{
				// find the next available item which is checked and set it to
				// the primary
				if (checkedElements.length == 0)
				{
					updatePrimaryNature(null);
				}
				else
				{
					updatePrimaryNature(checkedElements[0].toString());
				}
				fTableViewer.refresh();
			}
		}
		updateButtons();
	}

	public void widgetSelected(SelectionEvent e)
	{
		Object source = e.getSource();
		if (source == fSetPrimaryMenuItem || source == fMakePrimaryButton)
		{
			ISelection selection = fTableViewer.getSelection();
			if (!selection.isEmpty() && selection instanceof StructuredSelection)
			{
				Object firstElement = ((StructuredSelection) selection).getFirstElement();
				// make the element checked
				fTableViewer.setChecked(firstElement, true);
				// make it as primary
				updatePrimaryNature(firstElement.toString());
				fTableViewer.refresh();
				updateButtons();
			}
		}
	}

	public void widgetDefaultSelected(SelectionEvent e)
	{
	}

	protected Menu createMenu(Table table)
	{
		Menu menu = new Menu(table);
		fSetPrimaryMenuItem = new MenuItem(menu, SWT.PUSH);
		fSetPrimaryMenuItem.setText(EplMessages.ProjectNaturesPage_LBL_SetAsPrimary);
		fSetPrimaryMenuItem.addSelectionListener(this);
		return menu;
	}

	/**
	 * Returns true if the given element string is set as the primary nature.
	 * 
	 * @param element
	 * @return true if the element is set as the primary nature, false otherwise
	 */
	protected boolean isPrimary(Object element)
	{
		return fPrimaryNature != null && fPrimaryNature.equals(element);
	}

	/**
	 * Ask to reset the project (e.g. Close and Open) to apply the changes.
	 */
	protected void resetProject()
	{
		boolean reset = MessageDialog.openQuestion(getControl().getShell(), EplMessages.ProjectNaturesPage_ResetTitle,
				EplMessages.ProjectNaturesPage_ResetMessage);
		if (reset)
		{
			// close the project
			IRunnableWithProgress close = new IRunnableWithProgress()
			{

				public void run(final IProgressMonitor monitor) throws InvocationTargetException
				{
					// use the CloseResourceAction to provide a file saving
					// dialog in case the project has some unsaved
					// files
					UIJob job = new UIJob(EplMessages.ProjectNaturesPage_CloseProjectJob_Title + "...") //$NON-NLS-1$
					{
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							CloseResourceAction closeAction = new CloseResourceAction(new IShellProvider()
							{
								public Shell getShell()
								{
									return Display.getDefault().getActiveShell();
								}
							});
							closeAction.selectionChanged(new StructuredSelection(new Object[] { fProject }));
							closeAction.run();
							monitor.done();
							return Status.OK_STATUS;
						}
					};
					job.schedule();
					try
					{
						job.join();
					}
					catch (InterruptedException e)
					{
						// ignore
					}
					monitor.done();
				}
			};
			try
			{
				new ProgressMonitorJobsDialog(getControl().getShell()).run(true, true, close);
			}
			catch (InterruptedException e)
			{
				// ignore
			}
			catch (InvocationTargetException e)
			{
				IdeLog.logError(UIEplPlugin.getDefault(), EplMessages.ProjectNaturesPage_ERR_CloseProject, e);
			}

			// re-open the project
			IRunnableWithProgress open = new IRunnableWithProgress()
			{
				public void run(IProgressMonitor monitor) throws InvocationTargetException
				{
					try
					{
						fProject.open(monitor);
					}
					catch (CoreException e)
					{
						throw new InvocationTargetException(e);
					}
				}
			};
			try
			{
				new ProgressMonitorJobsDialog(getControl().getShell()).run(true, true, open);
			}
			catch (InterruptedException e)
			{
				// ignore
			}
			catch (InvocationTargetException e)
			{
				IdeLog.logError(UIEplPlugin.getDefault(), EplMessages.ProjectNaturesPage_ERR_OpenProject, e);
			}
		}
	}

	/**
	 * Returns a content provider for the list dialog. The content provider will include all available natures as
	 * strings.
	 * 
	 * @return the content provider that shows the natures (as string children)
	 */
	private IStructuredContentProvider getContentProvider()
	{
		return new BaseWorkbenchContentProvider()
		{

			@Override
			public Object[] getChildren(Object o)
			{
				if (!(o instanceof IWorkspace))
				{
					return new Object[0];
				}
				Set<String> elements = new HashSet<String>();
				// collect all available natures in the workspace
				IProjectNatureDescriptor[] natureDescriptors = ((IWorkspace) o).getNatureDescriptors();
				String natureId;
				for (IProjectNatureDescriptor descriptor : natureDescriptors)
				{
					natureId = descriptor.getNatureId();
					if (natureId != null)
					{
						if (ResourceUtil.isAptanaNature(natureId))
						{
							elements.add(natureId);
							fNatureDescriptions.put(natureId, descriptor.getLabel());
						}
					}
				}
				// add any natures that exist in the project but not in the
				// workbench
				// (this could happen when importing a project from a different
				// workspace or when the nature provider
				// got uninstalled)
				for (String nature : fCurrentProjectNatures)
				{
					if (elements.add(nature))
					{
						// since we don't have the nature descriptor here, just
						// use the nature id for the value instead
						fNatureDescriptions.put(nature, nature);
					}
				}
				return elements.toArray();
			}
		};
	}

	private ILabelProvider getLabelProvider()
	{
		return fLabelProvider;
	}

	private ViewerComparator getViewerComperator()
	{
		return new ViewerComparator(new Comparator<String>()
		{

			public int compare(String o1, String o2)
			{
				// set Aptana natures ahead of others
				if (ResourceUtil.isAptanaNature(o1))
				{
					return ResourceUtil.isAptanaNature(o2) ? o1.compareTo(o2) : -1;
				}
				return ResourceUtil.isAptanaNature(o2) ? 1 : o1.compareTo(o2);
			}
		});
	}

	private Button createButton(String text, Composite parent)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setLayoutData(GridDataFactory.fillDefaults().create());
		button.addSelectionListener(this);
		return button;
	}

	/**
	 * Updates the buttons' enablement.
	 */
	private void updateButtons()
	{
		StructuredSelection selection = (StructuredSelection) fTableViewer.getSelection();
		fMakePrimaryButton.setEnabled(!selection.isEmpty() && !isPrimary(selection.getFirstElement()));
	}

	private boolean isPrimaryNatureModified()
	{
		if (fInitialPrimaryNature == null)
		{
			return fPrimaryNature != null;
		}
		return !fInitialPrimaryNature.equals(fPrimaryNature);
	}

	private void updatePrimaryNature(String nature)
	{
		fPrimaryNature = nature;
		fLabelProvider.setPrimaryNature(fPrimaryNature);
	}

}
