/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

	/**
	 * CategoryContentProvider
	 * 
	 * @author Ingo Muschenetz
	 */
	private class CategoryContentProvider implements IStructuredContentProvider
	{
		/**
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			return (Object[]) inputElement;
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

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
			if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH)
					&& spaces != null && !spaces.isDisposed())
			{
				spaces.setText(StringUtil.format(Messages.EditorsPreferencePage_UseSpaces, event.getNewValue()));
			}

			if (event.getProperty().equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS)
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

		createTabInsertionEditors(appearanceComposite);
	}

	/**
	 * @param parent
	 */
	private void createUserAgentCategoryArea(Composite parent)
	{
		Label label = new Label(parent, SWT.WRAP);
		label.setText(Messages.UserAgentPreferencePage_Select_User_Agents);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 400;
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 200;
		composite.setLayoutData(data);
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.SINGLE);
		table.setFont(parent.getFont());
		table.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
			}
		});
		categoryViewer = new CheckboxTableViewer(table);
		categoryViewer.getControl().setFont(parent.getFont());
		categoryViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryViewer.setContentProvider(new CategoryContentProvider());
		CategoryLabelProvider categoryLabelProvider = new CategoryLabelProvider(true);
		categoryViewer.setLabelProvider(categoryLabelProvider);
		categoryViewer.setSorter(new ViewerSorter());

		categoryViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent event)
			{
			}
		});
		categoryViewer.setInput(UserAgentManager.getInstance().getAllUserAgents());
		categoryViewer.setCheckedElements(UserAgentManager.getInstance().getActiveUserAgents());
	}

	private void createUserAgentButtons(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		composite.setLayoutData(data);

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

	private void createTabInsertionEditors(Composite appearanceComposite)
	{
		Composite wsGroup = AptanaPreferencePage.createGroup(appearanceComposite,
				Messages.EditorsPreferencePage_TabInsertion);
		Composite wsComp = new Composite(wsGroup, SWT.NONE);

		GridLayout wsLayout = new GridLayout(3, false);
		wsLayout.marginWidth = 0;
		wsLayout.marginHeight = 0;
		wsComp.setLayout(wsLayout);
		wsComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		tabs = new Button(wsComp, SWT.RADIO);
		Composite spaceComp = new Composite(wsComp, SWT.NONE);
		wsLayout = new GridLayout(2, false);
		wsLayout.marginWidth = 0;
		wsLayout.marginHeight = 0;
		wsLayout.horizontalSpacing = 0;
		spaceComp.setLayout(wsLayout);
		spaceComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		spaces = new Button(spaceComp, SWT.RADIO);
		final Link currentTabSize = new Link(spaceComp, SWT.NONE);
		int size = editorPreferenceStore.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		spaces.setText(StringUtil.format(Messages.EditorsPreferencePage_UseSpaces, size));
		tabs.setText(Messages.EditorsPreferencePage_UseTabs);
		editorPreferenceStore.addPropertyChangeListener(prefListener);
		currentTabSize.setText(Messages.EditorsPreferencePage_EditLink);
		currentTabSize.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						"org.eclipse.ui.preferencePages.GeneralTextEditor", null); //$NON-NLS-1$
			}

		});
		boolean useSpaces = editorPreferenceStore
				.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		spaces.setSelection(useSpaces);
		tabs.setSelection(!useSpaces);
		tabs.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				spaces.setSelection(!tabs.getSelection());
			}

		});
		spaces.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				tabs.setSelection(!spaces.getSelection());
			}

		});

		// Link to general text editor prefs from Eclipse - they can set tabs/spaces/whitespace drawing, etc
		Link link = new Link(appearanceComposite, SWT.NONE);
		link.setText(Messages.EditorsPreferencePage_GeneralTextEditorPrefLink);
		link.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				((IWorkbenchPreferenceContainer) getContainer()).openPage(
						"org.eclipse.ui.preferencePages.GeneralTextEditor", null); //$NON-NLS-1$
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

		for (int i = 0; i < elements.length; i++)
		{
			UserAgentManager.UserAgent userAgent = (UserAgentManager.UserAgent) elements[i];

			al.add(userAgent.ID);
		}

		eplPreferenceStore.setValue(com.aptana.editor.common.contentassist.IPreferenceConstants.USER_AGENT_PREFERENCE,
				StringUtil.join(",", al.toArray(new String[al.size()])) //$NON-NLS-1$
				);

		return super.performOk();
	}

}
