/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.editor.ruby.formatter.preferences;

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

import com.aptana.editor.ruby.formatter.RubyFormatterConstants;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class RubyFormatterIndentationTabPage extends FormatterModifyTabPage
{

	/**
	 * @param dialog
	 */
	public RubyFormatterIndentationTabPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	private Combo tabPolicy;
	private Text indentSize;
	private Text tabSize;
	private TabPolicyListener tabPolicyListener;

	private final String[] tabPolicyItems = new String[] { CodeFormatterConstants.SPACE, CodeFormatterConstants.TAB,
			CodeFormatterConstants.MIXED };

	private class TabPolicyListener extends SelectionAdapter implements IFormatterControlManager.IInitializeListener
	{

		private final IFormatterControlManager manager;

		public TabPolicyListener(IFormatterControlManager manager)
		{
			this.manager = manager;
		}

		public void widgetSelected(SelectionEvent e)
		{
			int index = tabPolicy.getSelectionIndex();
			if (index >= 0)
			{
				final boolean tabMode = CodeFormatterConstants.TAB.equals(tabPolicyItems[index]);
				manager.enableControl(indentSize, !tabMode);
			}
		}

		public void initialize()
		{
			final boolean tabMode = CodeFormatterConstants.TAB.equals(manager
					.getString(RubyFormatterConstants.FORMATTER_TAB_CHAR));
			manager.enableControl(indentSize, !tabMode);
		}

	}

	@SuppressWarnings("deprecation")
	protected void createOptions(final IFormatterControlManager manager, Composite parent)
	{
		Group tabPolicyGroup = SWTFactory.createGroup(parent, FormatterMessages.FormatterModifyTabPage_generalSettings,
				2, 1, GridData.FILL_HORIZONTAL);
		tabPolicy = manager.createCombo(tabPolicyGroup, RubyFormatterConstants.FORMATTER_TAB_CHAR,
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy, tabPolicyItems);
		tabPolicyListener = new TabPolicyListener(manager);
		tabPolicy.addSelectionListener(tabPolicyListener);
		manager.addInitializeListener(tabPolicyListener);
		indentSize = manager.createNumber(tabPolicyGroup, RubyFormatterConstants.FORMATTER_INDENTATION_SIZE,
				FormatterMessages.IndentationTabPage_general_group_option_indent_size);
		tabSize = manager.createNumber(tabPolicyGroup, RubyFormatterConstants.FORMATTER_TAB_SIZE,
				FormatterMessages.IndentationTabPage_general_group_option_tab_size);
		tabSize.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				int index = tabPolicy.getSelectionIndex();
				if (index >= 0)
				{
					final boolean tabMode = CodeFormatterConstants.TAB.equals(tabPolicyItems[index]);
					if (tabMode)
					{
						indentSize.setText(tabSize.getText());
					}
				}
			}
		});
		//
		Group indentGroup = SWTFactory.createGroup(parent,
				FormatterMessages.RubyFormatterIndentationTabPage_indentDefinitionsGroupTitle, 1, 1,
				GridData.FILL_HORIZONTAL);
		manager.createCheckbox(indentGroup, RubyFormatterConstants.INDENT_CLASS,
				FormatterMessages.RubyFormatterIndentationTabPage_declarationWithinClassBody);
		manager.createCheckbox(indentGroup, RubyFormatterConstants.INDENT_MODULE,
				FormatterMessages.RubyFormatterIndentationTabPage_declarationWithinModuleBody);
		manager.createCheckbox(indentGroup, RubyFormatterConstants.INDENT_METHOD,
				FormatterMessages.RubyFormatterIndentationTabPage_declarationWithinMethodBody);
		Group indentBlocks = SWTFactory.createGroup(parent,
				FormatterMessages.RubyFormatterIndentationTabPage_indentWithinBlocks, 1, 1, GridData.FILL_HORIZONTAL);
		manager.createCheckbox(indentBlocks, RubyFormatterConstants.INDENT_BLOCKS,
				FormatterMessages.RubyFormatterIndentationTabPage_statementWithinBlocksBody);
		manager.createCheckbox(indentBlocks, RubyFormatterConstants.INDENT_IF,
				FormatterMessages.RubyFormatterIndentationTabPage_statementWithinIfBody);
		manager.createCheckbox(indentBlocks, RubyFormatterConstants.INDENT_CASE,
				FormatterMessages.RubyFormatterIndentationTabPage_statementWithinCaseBody);
		manager.createCheckbox(indentBlocks, RubyFormatterConstants.INDENT_WHEN,
				FormatterMessages.RubyFormatterIndentationTabPage_statementWithinWhenBody);
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource("indentation-preview.rb"); //$NON-NLS-1$
	}
}
