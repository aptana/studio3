/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.aptana.ui.util.StatusInfo;

/**
 * Base for project property and preference pages
 */
public abstract class PropertyAndPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
		IWorkbenchPropertyPage
{

	private Control fConfigurationBlockControl;
	private ControlEnableState fBlockEnableState;
	private Link fChangeWorkspaceSettings;
	private SelectionButtonDialogField fUseProjectSettings;
	private IStatus fBlockStatus;
	private Composite fParentComposite;

	private IProject fProject; // project or null
	private Map<String, Object> fData; // page data
	/**
	 * 
	 */
	protected IPreferenceStore store;

	/**
	 * 
	 */
	public static final String DATA_NO_LINK = "PropertyAndPreferencePage.nolink"; //$NON-NLS-1$

	/**
	 * @param store
	 */
	public PropertyAndPreferencePage(IPreferenceStore store)
	{
		fBlockStatus = new StatusInfo();
		fBlockEnableState = null;
		fProject = null;
		fData = null;
		this.store = store;
	}

	/**
	 * @param composite
	 * @return Control
	 */
	protected abstract Control createPreferenceContent(Composite composite);

	/**
	 * @param project
	 * @return value
	 */
	protected abstract boolean hasProjectSpecificOptions(IProject project);

	/**
	 * @return id
	 */
	protected abstract String getPreferencePageID();

	/**
	 * @return id
	 */
	protected abstract String getPropertyPageID();

	/**
	 * @return is property page workable
	 */
	protected boolean supportsProjectSpecificOptions()
	{
		return getPropertyPageID() != null;
	}

	/**
	 * @return value
	 */
	protected boolean offerLink()
	{
		return fData == null || !Boolean.TRUE.equals(fData.get(DATA_NO_LINK));
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createDescriptionLabel(org.eclipse.swt.widgets.Composite)
	 */
	protected Label createDescriptionLabel(Composite parent)
	{
		fParentComposite = parent;
		if (isProjectPreferencePage())
		{
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			GridLayout layout = new GridLayout();
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.numColumns = 2;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			IDialogFieldListener listener = new IDialogFieldListener()
			{
				public void dialogFieldChanged(DialogField field)
				{
					enableProjectSpecificSettings(((SelectionButtonDialogField) field).isSelected());
				}
			};

			fUseProjectSettings = new SelectionButtonDialogField(SWT.CHECK);
			fUseProjectSettings.setDialogFieldListener(listener);
			fUseProjectSettings.setLabelText(Messages.getString("PropertyAndPreferencePage.UseProjectSettings")); //$NON-NLS-1$
			fUseProjectSettings.doFillIntoGrid(composite, 1);
			LayoutUtil.setHorizontalGrabbing(fUseProjectSettings.getSelectionButton(null));

			if (offerLink())
			{
				fChangeWorkspaceSettings = createLink(composite, Messages
						.getString("PropertyAndPreferencePage.WorkspaceSettings")); //$NON-NLS-1$
				fChangeWorkspaceSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
			}
			else
			{
				LayoutUtil.setHorizontalSpan(fUseProjectSettings.getSelectionButton(null), 2);
			}

			Label horizontalLine = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			horizontalLine.setFont(composite.getFont());
		}
		else if (supportsProjectSpecificOptions() && offerLink())
		{
			fChangeWorkspaceSettings = createLink(parent, Messages
					.getString("PropertyAndPreferencePage.ShowProjectSpecificSettings")); //$NON-NLS-1$
			fChangeWorkspaceSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		}

		return super.createDescriptionLabel(parent);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());

		GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);

		fConfigurationBlockControl = createPreferenceContent(composite);
		fConfigurationBlockControl.setLayoutData(data);

		if (isProjectPreferencePage())
		{
			boolean useProjectSettings = hasProjectSpecificOptions(getProject());
			enableProjectSpecificSettings(useProjectSettings);
		}

		Dialog.applyDialogFont(composite);
		return composite;
	}

	private Link createLink(Composite composite, String text)
	{
		Link link = new Link(composite, SWT.NONE);
		link.setFont(composite.getFont());
		link.setText("<A>" + text + "</A>"); //$NON-NLS-1$//$NON-NLS-2$
		link.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				doLinkActivated((Link) e.widget);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				doLinkActivated((Link) e.widget);
			}
		});
		return link;
	}

	/**
	 * @return value
	 */
	protected boolean useProjectSettings()
	{
		return isProjectPreferencePage() && fUseProjectSettings != null && fUseProjectSettings.isSelected();
	}

	/**
	 * @return value
	 */
	protected boolean isProjectPreferencePage()
	{
		return fProject != null;
	}

	/**
	 * @return IProject
	 */
	protected IProject getProject()
	{
		return fProject;
	}

	final void doLinkActivated(Link link)
	{
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(DATA_NO_LINK, Boolean.TRUE);

		if (isProjectPreferencePage())
		{
			openWorkspacePreferences(data);
		}
		else
		{
			HashSet<IProject> projectsWithSpecifics = new HashSet<IProject>();
			try
			{
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				for (int i = 0; i < projects.length; i++)
				{
					IProject curr = projects[i];
					if (hasProjectSpecificOptions(curr.getProject()))
					{
						projectsWithSpecifics.add(curr);
					}
				}
			}
			catch (Exception e)
			{
				// ignore
			}
			ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(), projectsWithSpecifics);
			if (dialog.open() == Window.OK)
			{
				IProject res = (IProject) dialog.getFirstResult();
				openProjectProperties(res.getProject(), data);
			}
		}
	}

	/**
	 * @param data
	 */
	protected final void openWorkspacePreferences(Object data)
	{
		String id = getPreferencePageID();
		PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, data).open();
	}

	/**
	 * @param project
	 * @param data
	 */
	protected final void openProjectProperties(IProject project, Object data)
	{
		String id = getPropertyPageID();
		if (id != null)
		{
			PreferencesUtil.createPropertyDialogOn(getShell(), project, id, new String[] { id }, data).open();
		}
	}

	/**
	 * @param useProjectSpecificSettings
	 */
	protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings)
	{
		fUseProjectSettings.setSelection(useProjectSpecificSettings);
		enablePreferenceContent(useProjectSpecificSettings);
		updateLinkVisibility();
		doStatusChanged();
	}

	private void updateLinkVisibility()
	{
		if (fChangeWorkspaceSettings == null || fChangeWorkspaceSettings.isDisposed())
		{
			return;
		}

		if (isProjectPreferencePage())
		{
			fChangeWorkspaceSettings.setEnabled(!useProjectSettings());
		}
	}

	/**
	 * @param status
	 */
	protected void setPreferenceContentStatus(IStatus status)
	{
		fBlockStatus = status;
		doStatusChanged();
	}

	/**
	 * Returns a new status change listener that calls {@link #setPreferenceContentStatus(IStatus)} when the status has
	 * changed
	 * 
	 * @return The new listener
	 */
	protected IStatusChangeListener getNewStatusChangedListener()
	{
		return new IStatusChangeListener()
		{
			public void statusChanged(IStatus status)
			{
				setPreferenceContentStatus(status);
			}
		};
	}

	/**
	 * @return Status
	 */
	protected IStatus getPreferenceContentStatus()
	{
		return fBlockStatus;
	}

	/**
	 * 
	 */
	protected void doStatusChanged()
	{
		if (!isProjectPreferencePage() || useProjectSettings())
		{
			updateStatus(fBlockStatus);
		}
		else
		{
			updateStatus(new StatusInfo());
		}
	}

	/**
	 * @param enable
	 */
	protected void enablePreferenceContent(boolean enable)
	{
		if (enable)
		{
			if (fBlockEnableState != null)
			{
				fBlockEnableState.restore();
				fBlockEnableState = null;
			}
		}
		else
		{
			if (fBlockEnableState == null)
			{
				fBlockEnableState = ControlEnableState.disable(fConfigurationBlockControl);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		if (useProjectSettings())
		{
			enableProjectSpecificSettings(false);
		}
		super.performDefaults();
	}

	private void updateStatus(IStatus status)
	{
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
	 */
	public IAdaptable getElement()
	{
		return fProject;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	public void setElement(IAdaptable element)
	{
		fProject = (IProject) element.getAdapter(IResource.class);
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#applyData(java.lang.Object)
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" })
	public void applyData(Object data)
	{
		if (data instanceof Map<?, ?>)
		{
			fData = (Map<String, Object>) data;
		}
		if (fChangeWorkspaceSettings != null)
		{
			if (!offerLink())
			{
				fChangeWorkspaceSettings.dispose();
				fParentComposite.layout(true, true);
			}
		}
	}

	/**
	 * @return Map
	 */
	protected Map<String, Object> getData()
	{
		return fData;
	}

}
