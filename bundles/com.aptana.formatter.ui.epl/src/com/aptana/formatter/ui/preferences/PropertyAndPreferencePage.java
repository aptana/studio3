/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.formatter.ui.preferences;

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

import com.aptana.formatter.ui.dialogFields.DialogField;
import com.aptana.formatter.ui.dialogFields.IDialogFieldListener;
import com.aptana.formatter.ui.dialogFields.LayoutUtil;
import com.aptana.formatter.ui.dialogFields.SelectionButtonDialogField;
import com.aptana.formatter.ui.dialogs.ProjectSelectionDialog;
import com.aptana.formatter.ui.util.IStatusChangeListener;
import com.aptana.formatter.ui.util.StatusInfo;
import com.aptana.formatter.ui.util.StatusUtil;

/**
 * Direct port from the jdt ui, this class should not be extended by anyone but the internal dltk ui.
 * 
 * @see AbstractConfigurationBlockPropertyAndPreferencePage
 */
public abstract class PropertyAndPreferencePage extends PreferencePage implements IWorkbenchPreferencePage,
		IWorkbenchPropertyPage
{

	/*
	 * NOTE: this class was more or less a direct cut and paste from the jdt ui with some minor modifications to make it
	 * work for the dltk. why something like this doesn't exist in the 'eclipse core' i am not sure, perhaps this one of
	 * the areas where it would make sense to join forces with the imp folks.
	 */

	private Control fConfigurationBlockControl;
	private ControlEnableState fBlockEnableState;
	private Link fChangeWorkspaceSettings;
	private SelectionButtonDialogField fUseProjectSettings;
	private IStatus fBlockStatus;
	private Composite fParentComposite;

	private IProject fProject; // project or null
	private Map<?, ?> fData; // page data

	public static final String DATA_NO_LINK = "PropertyAndPreferencePage.nolink"; //$NON-NLS-1$

	public PropertyAndPreferencePage()
	{
		fBlockStatus = new StatusInfo();
		fBlockEnableState = null;
		fProject = null;
		fData = null;
	}

	protected abstract Control createPreferenceContent(Composite composite);

	protected abstract boolean hasProjectSpecificOptions(IProject project);

	protected abstract String getPreferencePageId();

	protected abstract String getPropertyPageId();

	protected boolean supportsProjectSpecificOptions()
	{
		return getPropertyPageId() != null;
	}

	protected String getContentType()
	{
		return null;
	}

	protected boolean offerLink()
	{
		return fData == null || !Boolean.TRUE.equals(fData.get(DATA_NO_LINK));
	}

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
			fUseProjectSettings.setLabelText(Messages.PropertyAndPreferencePage_enableProjectSpecific);
			fUseProjectSettings.doFillIntoGrid(composite, 1);
			LayoutUtil.setHorizontalGrabbing(fUseProjectSettings.getSelectionButton(null));

			if (offerLink())
			{
				fChangeWorkspaceSettings = createLink(composite,
						Messages.PropertyAndPreferencePage_configureWorkspaceSettings);
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
			fChangeWorkspaceSettings = createLink(parent, Messages.PropertyAndPreferencePage_configureProjectSettings);
			fChangeWorkspaceSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		}

		return super.createDescriptionLabel(parent);
	}

	/*
	 * @see IPreferencePage#createContents(Composite)
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

	protected boolean useProjectSettings()
	{
		return isProjectPreferencePage() && fUseProjectSettings != null && fUseProjectSettings.isSelected();
	}

	protected boolean isProjectPreferencePage()
	{
		return fProject != null;
	}

	protected IProject getProject()
	{
		return fProject;
	}

	protected void doLinkActivated(Link link)
	{
		Map<String, Boolean> data = new HashMap<String, Boolean>();
		data.put(DATA_NO_LINK, Boolean.TRUE);

		if (isProjectPreferencePage())
		{
			openWorkspacePreferences(data);
		}
		else
		{
			HashSet<IProject> projectsWithSpecifics = new HashSet<IProject>();
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
			for (int i = 0; i < projects.length; i++)
			{
				IProject curr = projects[i];
				if (hasProjectSpecificOptions(curr))
				{
					projectsWithSpecifics.add(curr);
				}
			}
			ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(), projectsWithSpecifics,
					getContentType());
			if (dialog.open() == Window.OK)
			{
				IProject res = (IProject) dialog.getFirstResult();
				openProjectProperties(res.getProject(), data);
			}
		}
	}

	protected final void openWorkspacePreferences(Object data)
	{
		String id = getPreferencePageId();
		PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, data).open();
	}

	protected final void openProjectProperties(IProject project, Object data)
	{
		String id = getPropertyPageId();
		if (id != null)
		{
			PreferencesUtil.createPropertyDialogOn(getShell(), project, id, new String[] { id }, data).open();
		}
	}

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

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
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

	/*
	 * @see IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
	 */
	public IAdaptable getElement()
	{
		return fProject;
	}

	/*
	 * @see IWorkbenchPropertyPage#setElement(IAdaptable)
	 */
	public void setElement(IAdaptable element)
	{
		fProject = (IProject) element.getAdapter(IResource.class);
	}

	/*
	 * @see PreferencePage#applyData(java.lang.Object)
	 */
	public void applyData(Object data)
	{
		if (data instanceof Map<?, ?>)
		{
			fData = (Map<?, ?>) data;
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
}
