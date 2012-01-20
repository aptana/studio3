/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.preferences;

import java.net.URL;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.aptana.editor.json.JSONPlugin;
import com.aptana.editor.json.formatter.JSONFormatter;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * JSONFormatWhitespacePage
 */
public class JSONFormatWhitespacePage extends FormatterModifyTabPage
{
	/**
	 * Listens to changes in the type of tab selected.
	 */
	private class TabOptionHandler extends SelectionAdapter implements IFormatterControlManager.IInitializeListener
	{
		private IFormatterControlManager manager;
		private Combo tabOptions;
		private Text indentationSize;
		private final Text tabSize;

		/**
		 * Constructor.
		 * 
		 * @param controlManager
		 * @param tabSize
		 */
		public TabOptionHandler(IFormatterControlManager controlManager, Combo tabOptions, Text indentationSize,
				Text tabSize)
		{
			this.manager = controlManager;
			this.tabOptions = tabOptions;
			this.indentationSize = indentationSize;
			this.tabSize = tabSize;
			tabOptions.addSelectionListener(this);
			manager.addInitializeListener(this);
		}

		public void initialize()
		{
			boolean tabMode = CodeFormatterConstants.TAB.equals(manager
					.getString(IPreferenceConstants.FORMATTER_TAB_CHAR));
			final boolean editorSettingsMode = CodeFormatterConstants.EDITOR.equals(manager
					.getString(IPreferenceConstants.FORMATTER_TAB_CHAR));
			manager.enableControl(indentationSize, !(tabMode || editorSettingsMode));
			manager.enableControl(tabSize, !editorSettingsMode);
			if (editorSettingsMode)
			{
				setEditorTabWidth(JSONPlugin.getDefault().getBundle().getSymbolicName(), tabSize, indentationSize);
			}
		}

		public void widgetSelected(SelectionEvent e)
		{
			int index = tabOptions.getSelectionIndex();

			if (index >= 0)
			{
				boolean tabMode = CodeFormatterConstants.TAB.equals(TAB_OPTION_ITEMS[index]);
				final boolean editorSettingsMode = CodeFormatterConstants.EDITOR.equals(TAB_OPTION_ITEMS[index]);
				manager.enableControl(indentationSize, !(tabMode || editorSettingsMode));
				manager.enableControl(tabSize, !editorSettingsMode);
				if (editorSettingsMode)
				{
					setEditorTabWidth(JSONPlugin.getDefault().getBundle().getSymbolicName(), tabSize, indentationSize);
				}
			}
		}
	}

	private static final String CONTROL_STATEMENTS_PREVIEW_NAME = "formatting-preview.json"; //$NON-NLS-1$
	private static final String[] TAB_OPTION_ITEMS = new String[] { CodeFormatterConstants.SPACE,
			CodeFormatterConstants.TAB, CodeFormatterConstants.EDITOR };
	private static final String[] TAB_OPTION_NAMES = new String[] {
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE,
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB,
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_EDITOR };

	/**
	 * JSONFormatterControlStatementsPage
	 * 
	 * @param dialog
	 */
	public JSONFormatWhitespacePage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @seecom.aptana.formatter.ui.preferences.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.
	 * IFormatterControlManager, org.eclipse.swt.widgets.Composite)
	 */
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group generalGroup = SWTFactory.createGroup(parent,
				Messages.JSONFormatterControlStatementsPage_general_group_label, 2, 1, GridData.FILL_HORIZONTAL);
		final Combo tabOptions = manager
				.createCombo(generalGroup, IPreferenceConstants.FORMATTER_TAB_CHAR,
						Messages.JSONFormatterControlStatementsPage_tab_policy_group_option, TAB_OPTION_ITEMS,
						TAB_OPTION_NAMES);
		final Text indentationSize = manager.createNumber(generalGroup,
				IPreferenceConstants.FORMATTER_INDENTATION_SIZE,
				Messages.JSONFormatterControlStatementsPage_indentation_size_group_option, 1);
		final Text tabSize = manager.createNumber(generalGroup, IPreferenceConstants.FORMATTER_TAB_SIZE,
				Messages.JSONFormatterControlStatementsPage_tab_size_group_option, 1);

		tabSize.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				int index = tabOptions.getSelectionIndex();

				if (index >= 0)
				{
					boolean tabMode = CodeFormatterConstants.TAB.equals(TAB_OPTION_ITEMS[index]);

					if (tabMode)
					{
						indentationSize.setText(tabSize.getText());
					}
				}
			}
		});

		new TabOptionHandler(manager, tabOptions, indentationSize, tabSize);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return JSONFormatter.class.getResource(CONTROL_STATEMENTS_PREVIEW_NAME);
	}
}
