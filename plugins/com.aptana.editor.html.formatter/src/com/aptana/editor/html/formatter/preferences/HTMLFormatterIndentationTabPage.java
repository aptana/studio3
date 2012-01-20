/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.formatter.preferences;

import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.formatter.HTMLFormatterConstants;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

/**
 * HTML formatter indentation tab.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class HTMLFormatterIndentationTabPage extends FormatterModifyTabPage
{
	private static final String INDENTATION_PREVIEW_FILE = "indentation-preview.html"; //$NON-NLS-1$
	private final String[] tabOptionItems = new String[] { CodeFormatterConstants.SPACE, CodeFormatterConstants.TAB,
			CodeFormatterConstants.MIXED, CodeFormatterConstants.EDITOR };
	private final String[] tabOptionNames = new String[] {
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE,
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB,
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED,
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_EDITOR };

	/**
	 * Constructs a new HTMLFormatterIndentationTabPage
	 * 
	 * @param dialog
	 */
	public HTMLFormatterIndentationTabPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.IFormatterControlManager,
	 * org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group group = SWTFactory.createGroup(parent,
				Messages.HTMLFormatterIndentationTabPage_indentationGeneralGroupLabel, 2, 1, GridData.FILL_HORIZONTAL);
		final Combo tabOptions = manager.createCombo(group, HTMLFormatterConstants.FORMATTER_TAB_CHAR,
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy, tabOptionItems, tabOptionNames);
		final Text indentationSize = manager.createNumber(group, HTMLFormatterConstants.FORMATTER_INDENTATION_SIZE,
				FormatterMessages.IndentationTabPage_general_group_option_indent_size, 1);
		final Text tabSize = manager.createNumber(group, HTMLFormatterConstants.FORMATTER_TAB_SIZE,
				FormatterMessages.IndentationTabPage_general_group_option_tab_size, 1);
		tabSize.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				int index = tabOptions.getSelectionIndex();
				if (index >= 0)
				{
					final boolean tabMode = CodeFormatterConstants.TAB.equals(tabOptionItems[index]);
					if (tabMode)
					{
						indentationSize.setText(tabSize.getText());
					}
				}
			}
		});
		new TabOptionHandler(manager, tabOptions, indentationSize, tabSize);

		group = SWTFactory.createGroup(parent, Messages.HTMLFormatterTabPage_exclusionsGroupLabel, 1, 1,
				GridData.FILL_BOTH);
		Label exclutionLabel = new Label(group, SWT.WRAP);
		exclutionLabel.setText(Messages.HTMLFormatterIndentationTabPage_exclusionsMessage);
		manager.createManagedList(group, HTMLFormatterConstants.INDENT_EXCLUDED_TAGS);
	}

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

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			int index = tabOptions.getSelectionIndex();
			if (index >= 0)
			{
				final boolean tabMode = CodeFormatterConstants.TAB.equals(tabOptionItems[index]);
				final boolean editorSettingsMode = CodeFormatterConstants.EDITOR.equals(tabOptionItems[index]);
				manager.enableControl(indentationSize, !(tabMode || editorSettingsMode));
				manager.enableControl(tabSize, !editorSettingsMode);
				if (editorSettingsMode)
				{
					setEditorTabWidth(HTMLPlugin.getDefault().getBundle().getSymbolicName(), tabSize, indentationSize);
				}
			}
		}

		public void initialize()
		{
			final boolean tabMode = CodeFormatterConstants.TAB.equals(manager
					.getString(HTMLFormatterConstants.FORMATTER_TAB_CHAR));
			final boolean editorSettingsMode = CodeFormatterConstants.EDITOR.equals(manager
					.getString(HTMLFormatterConstants.FORMATTER_TAB_CHAR));
			manager.enableControl(indentationSize, !(tabMode || editorSettingsMode));
			manager.enableControl(tabSize, !editorSettingsMode);
			if (editorSettingsMode)
			{
				setEditorTabWidth(HTMLPlugin.getDefault().getBundle().getSymbolicName(), tabSize, indentationSize);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.FormatterModifyTabPage#getPreviewContent()
	 */
	protected URL getPreviewContent()
	{
		return getClass().getResource(INDENTATION_PREVIEW_FILE);
	}
}
