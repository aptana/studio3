/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

import com.aptana.core.CoreStrings;
import com.aptana.core.IUserAgent;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ArrayUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.contentassist.UserAgentFilterType;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.ui.preferences.AptanaPreferencePage;
import com.aptana.ui.preferences.PropertyAndPreferenceFieldEditorPage;

/**
 * UserAgentPreferencePage
 */
public class ContentAssistPreferencePage extends PropertyAndPreferenceFieldEditorPage
{
	private static final String PREFERENCE_PAGE_ID = "com.aptana.editor.common.contentAssistPreferencePage"; //$NON-NLS-1$
	private static final String PROPERTY_PAGE_ID = "com.aptana.editor.common.contentAssistPropertyPage"; //$NON-NLS-1$

	private Combo natureCombo;
	private String activeNatureID;
	private CheckboxTableViewer categoryViewer;
	private SortedMap<String, String> natureIDsByName;
	private Map<String, IUserAgent[]> userAgentsByNatureID;

	/**
	 * UserAgentPreferencePage
	 */
	public ContentAssistPreferencePage()
	{
		super(GRID);

		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.ContentAssistPreferencePage_ContentAssistPreferencePageDescription);
	}

	/**
	 * createFieldEditors
	 */
	public void createFieldEditors()
	{
		Composite parent = getFieldEditorParent();

		// create user agent group
		Composite uaGroup = AptanaPreferencePage.createGroup(parent,
				Messages.ContentAssistPreferencePage_UserAgentGroupLabel);
		createUserAgentGroupContent(uaGroup);

		if (!isProjectPreferencePage())
		{
			// create proposal group
			Composite pGroup = AptanaPreferencePage.createGroup(parent,
					Messages.ContentAssistPreferencePage_ProposalsGroupLabel);
			createProposalGroupContent(pGroup);
		}
	}

	/**
	 * createFilterSelector
	 * 
	 * @param parent
	 */
	protected void createFilterSelector(Composite parent)
	{
		// @formatter:off
		ComboFieldEditor fieldEditor = new ComboFieldEditor(
			com.aptana.editor.common.contentassist.IPreferenceConstants.CONTENT_ASSIST_USER_AGENT_FILTER_TYPE,
			Messages.ContentAssistPreferencePage_ProposalFilterTypeLabel,
			new String[][]
			{
				{ Messages.ContentAssistPreferencePage_NoFilterLabel, UserAgentFilterType.NO_FILTER.getText() },
				{ Messages.ContentAssistPreferencePage_OneOrMoreFilterLabel, UserAgentFilterType.ONE_OR_MORE.getText() },
				{ Messages.ContentAssistPreferencePage_AllFilterLabel, UserAgentFilterType.ALL.getText() }
			},
			parent
		);
		addField(fieldEditor);
		// @formatter:on
		// We only want to enable this field editor for workspace-specific settings.
		// Since the UI will not draw anything unless we have at least one field-editor in this page, we have to add it
		// and disable it for project-specific.
		fieldEditor.setEnabled(!isProjectPreferencePage(), parent);
	}

	/**
	 * createNatureSelector
	 * 
	 * @param parent
	 */
	protected void createNatureSelector(Composite parent)
	{
		// grab nature label+id list
		SortedMap<String, String> natureMap = getNatureMap();

		// combo label
		Label label = new Label(parent, SWT.LEFT);
		label.setFont(parent.getFont());
		label.setText(Messages.ContentAssistPreferencePage_NatureComboLabel);

		// create combo
		natureCombo = new Combo(parent, SWT.READ_ONLY);
		natureCombo.setFont(parent.getFont());

		GridData gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		natureCombo.setLayoutData(gd);

		// Selected nature, in case it's a property page.
		boolean isProjectPreference = isProjectPreferencePage();
		String primaryProjectNature = null;
		if (isProjectPreference)
		{
			try
			{
				String[] aptanaNatures = ResourceUtil.getAptanaNatures(getProject().getDescription());
				if (!ArrayUtil.isEmpty(aptanaNatures))
				{
					primaryProjectNature = aptanaNatures[0];
				}
			}
			catch (CoreException e)
			{
			}
		}
		// set combo list
		for (Map.Entry<String, String> entry : natureMap.entrySet())
		{
			if (primaryProjectNature != null)
			{
				// Select only the matching entry
				if (primaryProjectNature.equals(entry.getValue()))
				{
					natureCombo.add(entry.getKey());
					break;
				}
			}
			else
			{
				natureCombo.add(entry.getKey());
			}
		}

		// select first item and save reference to that nature id for future selection updates
		natureCombo.select(0);
		activeNatureID = natureMap.get(natureCombo.getText());

		natureCombo.setEnabled(!isProjectPreference);
		if (!isProjectPreference)
		{
			natureCombo.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent evt)
				{
					// update selection in model
					userAgentsByNatureID.put(activeNatureID, getSelectedUserAgents());

					// update nature id
					activeNatureID = getNatureMap().get(natureCombo.getText());

					// update visible selection
					updateUserAgentSelection();
				}
			});
		}
	}

	/**
	 * createProposalGroupContent
	 * 
	 * @param parent
	 */
	protected void createProposalGroupContent(Composite parent)
	{
		// @formatter:off
		addField(
			new BooleanFieldEditor(
				IPreferenceConstants.CONTENT_ASSIST_AUTO_INSERT,
				Messages.EditorsPreferencePage_Content_Assist_Auto_Insert,
				parent
			)
		);

		addField(
			new ComboFieldEditor(
				IPreferenceConstants.CONTENT_ASSIST_DELAY,
				Messages.EditorsPreferencePage_Content_Assist_Auto_Display,
				new String[][]
				{
					{ Messages.EditorsPreferencePage_Instant,
						Integer.toString(CommonSourceViewerConfiguration.NO_CONTENT_ASSIST_DELAY) },
					{ Messages.EditorsPreferencePage_DefaultDelay,
						Integer.toString(CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY) },
					{ Messages.EditorsPreferencePage_Content_Assist_Short_Delay,
						Integer.toString(CommonSourceViewerConfiguration.LONG_CONTENT_ASSIST_DELAY) },
					{ CoreStrings.OFF, String.valueOf(CommonSourceViewerConfiguration.CONTENT_ASSIST_OFF_DELAY) }
				},
				parent
			)
		);

		addField(
			new ComboFieldEditor(
				IPreferenceConstants.CONTENT_ASSIST_HOVER,
				Messages.EditorsPreferencePage_Content_Assist_Hover,
				new String[][]
				{
					{ CoreStrings.ON, Boolean.toString(true) },
					{ CoreStrings.OFF, Boolean.toString(false) }
				},
				parent
			)
		);
		// @formatter:on
	}

	/**
	 * createUserAgentButtons
	 * 
	 * @param parent
	 */
	protected void createUserAgentButtons(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().span(2, 0).grab(true, false).create());

		Button enableAll = new Button(composite, SWT.PUSH);
		enableAll.setFont(parent.getFont());
		enableAll.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				categoryViewer.setCheckedElements(UserAgentManager.getInstance().getAllUserAgents());
			}
		});
		enableAll.setText(Messages.UserAgentPreferencePage_Select_All);
		setButtonLayoutData(enableAll);

		Button disableAll = new Button(composite, SWT.PUSH);
		disableAll.setFont(parent.getFont());
		disableAll.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				categoryViewer.setCheckedElements(new Object[0]);
			}
		});
		disableAll.setText(Messages.UserAgentPreferencePage_Select_None);
		setButtonLayoutData(disableAll);
	}

	/**
	 * createUserAgentGroupContent
	 * 
	 * @param parent
	 */
	protected void createUserAgentGroupContent(Composite parent)
	{
		createNatureSelector(parent);
		createUserAgentTable(parent);
		createUserAgentButtons(parent);
		createFilterSelector(parent);
		UserAgentManager manager = UserAgentManager.getInstance();
		// initialize nature to user agent map
		userAgentsByNatureID = new HashMap<String, IUserAgent[]>();
		if (isProjectPreferencePage())
		{
			try
			{
				IProject project = getProject();
				if (project.isAccessible())
				{
					String[] aptanaNatures = ResourceUtil.getAptanaNatures(project.getDescription());
					if (!ArrayUtil.isEmpty(aptanaNatures))
					{
						userAgentsByNatureID.put(aptanaNatures[0], manager.getActiveUserAgents(project));
					}
				}
			}
			catch (CoreException e)
			{
				IdeLog.logError(CommonEditorPlugin.getDefault(), e);
			}
		}
		else
		{
			// Filter selection only visible on the workspace setting level.

			for (String natureID : ResourceUtil.getAptanaNaturesMap().values())
			{
				userAgentsByNatureID.put(natureID, manager.getActiveUserAgents(natureID));
			}
		}
		updateUserAgentSelection();
	}

	/**
	 * createUserAgentTable
	 * 
	 * @param parent
	 */
	protected void createUserAgentTable(Composite parent)
	{
		Label label = new Label(parent, SWT.WRAP);
		label.setText(Messages.UserAgentPreferencePage_Select_User_Agents);
		label.setLayoutData(GridDataFactory.fillDefaults().span(2, 0).grab(true, true).create());

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		composite.setLayoutData(GridDataFactory.fillDefaults().span(2, 0).hint(400, 120).grab(true, true).create());

		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(parent.getFont());

		categoryViewer = new CheckboxTableViewer(table);
		categoryViewer.getControl().setFont(parent.getFont());
		categoryViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryViewer.setContentProvider(ArrayContentProvider.getInstance());

		CategoryLabelProvider categoryLabelProvider = new CategoryLabelProvider(true);
		categoryViewer.setLabelProvider(categoryLabelProvider);
		categoryViewer.setComparator(new ViewerComparator()
		{
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				if (e1 instanceof IUserAgent && e2 instanceof IUserAgent)
				{
					IUserAgent ua1 = (IUserAgent) e1;
					IUserAgent ua2 = (IUserAgent) e2;

					String uaName1 = StringUtil.getStringValue(ua1.getName());
					String uaName2 = StringUtil.getStringValue(ua2.getName());

					return uaName1.compareToIgnoreCase(uaName2);
				}

				return super.compare(viewer, e1, e2);
			}
		});

		categoryViewer.setInput(UserAgentManager.getInstance().getAllUserAgents());
	}

	/**
	 * getNatures
	 * 
	 * @return
	 */
	protected SortedMap<String, String> getNatureMap()
	{
		if (natureIDsByName == null)
		{
			natureIDsByName = new TreeMap<String, String>(ResourceUtil.getAptanaNaturesMap());
		}

		return natureIDsByName;
	}

	/**
	 * getSelectedUserAgents
	 * 
	 * @return
	 */
	protected IUserAgent[] getSelectedUserAgents()
	{
		Object[] elements = categoryViewer.getCheckedElements();
		IUserAgent[] userAgents = new IUserAgent[elements.length];

		for (int i = 0; i < elements.length; i++)
		{
			userAgents[i] = (IUserAgent) elements[i];
		}

		return userAgents;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults()
	{
		userAgentsByNatureID = new HashMap<String, IUserAgent[]>();

		UserAgentManager manager = UserAgentManager.getInstance();

		for (String natureID : ResourceUtil.getAptanaNaturesMap().values())
		{
			userAgentsByNatureID.put(natureID, manager.getDefaultUserAgents(natureID));
		}

		updateUserAgentSelection();

		super.performDefaults();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	@Override
	public boolean performOk()
	{
		// update selection in model
		userAgentsByNatureID.put(activeNatureID, getSelectedUserAgents());

		// set active user agents for all natures
		UserAgentManager manager = UserAgentManager.getInstance();

		IProject project = getProject();
		if (project == null)
		{
			for (Map.Entry<String, IUserAgent[]> entry : userAgentsByNatureID.entrySet())
			{
				String natureID = entry.getKey();
				IUserAgent[] userAgents = entry.getValue();
				String[] userAgentIDs = new String[userAgents.length];

				for (int i = 0; i < userAgents.length; i++)
				{
					userAgentIDs[i] = userAgents[i].getID();
				}

				manager.setActiveUserAgents(natureID, userAgentIDs);
				// Write changes to preferences (workspace scope)
				manager.savePreference();
			}
		}
		else if (useProjectSettings())
		{
			// Write changes to preferences (project-scope).
			if (activeNatureID != null && !CollectionsUtil.isEmpty(userAgentsByNatureID))
			{
				IUserAgent[] userAgents = userAgentsByNatureID.get(activeNatureID);
				String[] userAgentIDs = new String[userAgents.length];
				for (int i = 0; i < userAgents.length; i++)
				{
					userAgentIDs[i] = userAgents[i].getID();
				}
				manager.savePreference(project,
						CollectionsUtil.newTypedMap(String.class, String[].class, activeNatureID, userAgentIDs));
			}
			else
			{
				IdeLog.logWarning(CommonEditorPlugin.getDefault(),
						"ContentAssist preferences - Did not save. Expected to have a valid Aptana nature"); //$NON-NLS-1$
			}
		}
		else
		{
			// This is a project property page. However, the user have no selection to enable a project-specific.
			// We need to make sure that the project-scope preferences are cleared.
			manager.clearPreferences(project);
		}
		return super.performOk();
	}

	/**
	 * updateUserAgentSelection
	 */
	private void updateUserAgentSelection()
	{
		String name = natureCombo.getText();
		String natureID = natureIDsByName.get(name);
		IUserAgent[] userAgents = userAgentsByNatureID.get(natureID);

		categoryViewer.setCheckedElements(userAgents != null ? userAgents : UserAgentManager.NO_USER_AGENTS);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.ui.preferences.PropertyAndPreferenceFieldEditorPage#hasProjectSpecificOptions(org.eclipse.core.resources
	 * .IProject )
	 */
	@Override
	protected boolean hasProjectSpecificOptions(IProject project)
	{
		ProjectScope scope = new ProjectScope(project);
		IEclipsePreferences node = scope.getNode(CommonEditorPlugin.PLUGIN_ID);
		if (node != null)
		{
			return node.get(com.aptana.editor.common.contentassist.IPreferenceConstants.USER_AGENT_PREFERENCE, null) != null;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.preferences.PropertyAndPreferenceFieldEditorPage#getPreferencePageId()
	 */
	@Override
	protected String getPreferencePageId()
	{
		return PREFERENCE_PAGE_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.preferences.PropertyAndPreferenceFieldEditorPage#getNatureIDs()
	 */
	protected Set<String> getNatureIDs()
	{
		return new HashSet<String>(ResourceUtil.getAptanaNaturesMap().values());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.preferences.PropertyAndPreferenceFieldEditorPage#getPropertyPageId()
	 */
	@Override
	protected String getPropertyPageId()
	{
		return PROPERTY_PAGE_ID;
	}

	private class CategoryLabelProvider extends LabelProvider implements ITableLabelProvider
	{
		/**
		 * @param decorate
		 */
		public CategoryLabelProvider(boolean decorate)
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex)
		{
			IUserAgent agent = (IUserAgent) element;
			return UserAgentManager.getInstance().getEnabledIcon(agent);
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			return ((IUserAgent) element).getName();
		}
	}
}
