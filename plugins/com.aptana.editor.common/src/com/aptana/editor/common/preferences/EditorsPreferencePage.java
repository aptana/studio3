/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.CommonSourceViewerConfiguration;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.AptanaPreferencePage;

/**
 * The form for configuring the general top-level preferences for this plugin
 */

@SuppressWarnings("restriction")
public class EditorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	private Button spaces;
	private Button tabs;
	private IPreferenceStore editorPreferenceStore;
	private IPreferenceStore eplPreferenceStore;
	private CheckboxTableViewer categoryViewer;
	private String GENERAL_TEXT_EDITOR_PREF_ID = "org.eclipse.ui.preferencePages.GeneralTextEditor"; //$NON-NLS-1$
	
	/**
	 * CategoryLabelProvider
	 * 
	 * @author Ingo Muschenetz
	 */
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
			return ((UserAgentManager.UserAgent) element).enabledIcon;
		}

		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			return ((UserAgentManager.UserAgent) element).name;
		}
	}

	private IPropertyChangeListener prefListener = new IPropertyChangeListener()
	{

		public void propertyChange(PropertyChangeEvent event)
		{
			if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(event.getProperty())
					&& spaces != null && !spaces.isDisposed())
			{
				spaces.setText(StringUtil.format(Messages.EditorsPreferencePage_UseSpaces, event.getNewValue()));
			}

			if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.equals(event.getProperty())
					&& spaces != null && !spaces.isDisposed() && tabs != null && !tabs.isDisposed())
			{
				Boolean val = (Boolean) event.getNewValue();
				spaces.setSelection(val);
				tabs.setSelection(!val);
			}

		}

	};

	/**
	 * EditorsPreferencePage
	 */
	public EditorsPreferencePage()
	{
		super(GRID);
		editorPreferenceStore = EditorsPlugin.getDefault().getPreferenceStore();
		eplPreferenceStore = UIEplPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(CommonEditorPlugin.getDefault().getPreferenceStore());
		setDescription(Messages.EditorsPreferencePage_PreferenceDescription);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to manipulate various
	 * types of preferences. Each field editor knows how to save and restore itself.
	 */
	public void createFieldEditors()
	{
		Composite appearanceComposite = getFieldEditorParent();
		Composite group = AptanaPreferencePage.createGroup(appearanceComposite, Messages.EditorsPreferencePage_Typing);

		addField(new BooleanFieldEditor(IPreferenceConstants.ENABLE_CHARACTER_PAIR_COLORING,
				Messages.EditorsPreferencePage_Colorize_Matching_Character_Pairs, group));

		addField(new BooleanFieldEditor(IPreferenceConstants.EDITOR_PEER_CHARACTER_CLOSE,
				Messages.EditorsPreferencePage_Close_Matching_Character_Pairs, group));

		// In Studio 2.0, commenting out until requested, or it's determined we have enough available space
		// addField(new RadioGroupFieldEditor(AbstractTextEditor.PREFERENCE_NAVIGATION_SMART_HOME_END,
		// Messages.EditorsPreferencePage_HomeEndBehavior, 1, new String[][] {
		//								{ Messages.EditorsPreferencePage_ToggleBetween, "true" }, //$NON-NLS-1$
		//								{ Messages.EditorsPreferencePage_JumpsStartEnd, "false" } }, //$NON-NLS-1$
		// appearanceComposite, true));

		Composite caGroup = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.EditorsPreferencePage_Content_Assist);

		addField(new RadioGroupFieldEditor(IPreferenceConstants.CONTENT_ASSIST_DELAY,
				Messages.EditorsPreferencePage_Content_Assist_Auto_Display, 3, new String[][] {
						{ "On", Integer.toString(CommonSourceViewerConfiguration.DEFAULT_CONTENT_ASSIST_DELAY) }, //$NON-NLS-1$
						{ Messages.EditorsPreferencePage_Content_Assist_Short_Delay,
								Integer.toString(CommonSourceViewerConfiguration.LONG_CONTENT_ASSIST_DELAY) },
						{ "Off", "-1" } }, //$NON-NLS-1$ //$NON-NLS-2$
				caGroup, false));

		createUserAgentCategoryArea(caGroup);
		createUserAgentButtons(caGroup);

		createTextEditorLink(appearanceComposite);
	}

	/**
	 * @param parent
	 */
	private void createUserAgentCategoryArea(Composite parent)
	{
		Label label = new Label(parent, SWT.WRAP);
		label.setText(Messages.UserAgentPreferencePage_Select_User_Agents);
		label.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().create());
		composite.setLayoutData(GridDataFactory.fillDefaults().hint(400, 120).grab(true, true).create());

		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(parent.getFont());

		categoryViewer = new CheckboxTableViewer(table);
		categoryViewer.getControl().setFont(parent.getFont());
		categoryViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryViewer.setContentProvider(new ArrayContentProvider());
		CategoryLabelProvider categoryLabelProvider = new CategoryLabelProvider(true);
		categoryViewer.setLabelProvider(categoryLabelProvider);
		categoryViewer.setSorter(new ViewerSorter());

		categoryViewer.setInput(UserAgentManager.getInstance().getAllUserAgents());
		categoryViewer.setCheckedElements(UserAgentManager.getInstance().getActiveUserAgents());
	}

	private void createUserAgentButtons(Composite parent)
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

	private void createTextEditorLink(Composite appearanceComposite)
	{
		// Link to general text editor prefs from Eclipse - they can set tabs/spaces/whitespace drawing, etc
		Link link = new Link(appearanceComposite, SWT.NONE);
		link.setText(Messages.EditorsPreferencePage_GeneralTextEditorPrefLink);
		link.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						GENERAL_TEXT_EDITOR_PREF_ID, null);
			}

		});
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#dispose()
	 */
	public void dispose()
	{
		editorPreferenceStore.removePropertyChangeListener(prefListener);
		super.dispose();
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performDefaults()
	 */
	protected void performDefaults()
	{
		boolean useSpaces = editorPreferenceStore
				.getDefaultBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		spaces.setSelection(useSpaces);
		tabs.setSelection(!useSpaces);

		categoryViewer.setCheckedElements(UserAgentManager.getInstance().getDefaultActiveUserAgents());

		super.performDefaults();

	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	public boolean performOk()
	{
		editorPreferenceStore.setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS,
				spaces.getSelection());

		List<String> al = new ArrayList<String>();
		Object[] elements = categoryViewer.getCheckedElements();

		for (Object i : elements)
		{
			UserAgentManager.UserAgent userAgent = (UserAgentManager.UserAgent)i;
			al.add(userAgent.ID);
		}

		eplPreferenceStore.setValue(com.aptana.editor.common.contentassist.IPreferenceConstants.USER_AGENT_PREFERENCE,
				StringUtil.join(",", al.toArray(new String[al.size()])) //$NON-NLS-1$
				);

		return super.performOk();
	}

}
