/*******************************************************************************
 * Copyright (c) 2005, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.aptana.ui.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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

import com.aptana.ui.dialogfields.DialogField;
import com.aptana.ui.dialogfields.IDialogFieldListener;
import com.aptana.ui.dialogfields.SelectionButtonDialogField;
import com.aptana.ui.dialogs.ProjectSelectionDialog;

/**
 * Ported from the JDT and modified to fit our needs. The page provides common interface for FieldEditorPreferencePages
 * that acts as both property and preference pages.
 */
public abstract class PropertyAndPreferenceFieldEditorPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, IWorkbenchPropertyPage
{
	private Control fConfigurationBlockControl;
	private ControlEnableState fBlockEnableState;
	private Link fChangeWorkspaceSettings;
	private SelectionButtonDialogField fUseProjectSettings;
	private Composite fParentComposite;

	private IProject fProject; // project or null
	private Map<?, ?> fData; // page data

	public static final String DATA_NO_LINK = "PropertyAndPreferenceFieldEditorPage.nolink"; //$NON-NLS-1$

	public PropertyAndPreferenceFieldEditorPage()
	{
	}

	public PropertyAndPreferenceFieldEditorPage(String title, int style)
	{
		super(title, style);
	}

	public PropertyAndPreferenceFieldEditorPage(int style)
	{
		super(style);
	}

	protected abstract boolean hasProjectSpecificOptions(IProject project);

	protected abstract String getPreferencePageId();

	protected abstract String getPropertyPageId();

	protected boolean supportsProjectSpecificOptions()
	{
		return getPropertyPageId() != null;
	}

	/**
	 * Returns the nature ID's to filter by when displaying the available projects that can have a project-specific
	 * settings.
	 * 
	 * @return A Set of nature IDs.
	 */
	protected Set<String> getNatureIDs()
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
			fUseProjectSettings.setLabelText(EplMessages.PropertyAndPreferencePage_enableProjectSpecific);
			fUseProjectSettings.doFillIntoGrid(composite, 1);
			setHorizontalGrabbing(fUseProjectSettings.getSelectionButton(null));

			if (offerLink())
			{
				fChangeWorkspaceSettings = createLink(composite,
						EplMessages.PropertyAndPreferencePage_workspaceSettingsLabel);
				fChangeWorkspaceSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
			}
			else
			{
				setHorizontalSpan(fUseProjectSettings.getSelectionButton(null), 2);
			}

			Label horizontalLine = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
			horizontalLine.setFont(composite.getFont());
		}
		else if (supportsProjectSpecificOptions() && offerLink())
		{
			fChangeWorkspaceSettings = createLink(parent, EplMessages.PropertyAndPreferencePage_projectSettingsLabel);
			fChangeWorkspaceSettings.setLayoutData(new GridData(SWT.END, SWT.CENTER, true, false));
		}

		return super.createDescriptionLabel(parent);
	}

	private static void setHorizontalGrabbing(Control control)
	{
		Object ld = control.getLayoutData();
		if (ld instanceof GridData)
		{
			((GridData) ld).grabExcessHorizontalSpace = true;
		}
	}

	/**
	 * Sets the span of a control. Assumes that GridData is used.
	 */
	private static void setHorizontalSpan(Control control, int span)
	{
		Object ld = control.getLayoutData();
		if (ld instanceof GridData)
		{
			((GridData) ld).horizontalSpan = span;
		}
		else if (span != 1)
		{
			GridData gd = new GridData();
			gd.horizontalSpan = span;
			control.setLayoutData(gd);
		}
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

		fConfigurationBlockControl = super.createContents(composite);
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
					getNatureIDs());
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
