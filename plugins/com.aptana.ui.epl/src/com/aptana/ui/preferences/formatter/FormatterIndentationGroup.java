/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package com.aptana.ui.preferences.formatter;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.aptana.ui.util.SWTFactory;

/**
 * @since 2.0
 */
public class FormatterIndentationGroup
{

	private Combo tabPolicy;
	private Text indentSize;
	private Text tabSize;

	private final String[] tabPolicyItems = new String[] { CodeFormatterConstants.SPACE, CodeFormatterConstants.TAB,
			CodeFormatterConstants.MIXED };

	private class TabPolicyListener extends SelectionAdapter implements IFormatterControlManager.IInitializeListener
	{

		private final IFormatterControlManager manager;

		public TabPolicyListener(IFormatterControlManager manager)
		{
			this.manager = manager;
		}

		@Override
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
					.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR));
			manager.enableControl(indentSize, !tabMode);
		}

	}

	private TabPolicyListener tabPolicyListener;

	public FormatterIndentationGroup(IFormatterControlManager manager, Composite parent)
	{
		Group tabPolicyGroup = SWTFactory.createGroup(parent, FormatterMessages.IndentationTabPage_generalSettings, 2,
				1, GridData.FILL_HORIZONTAL);
		tabPolicy = manager.createCombo(tabPolicyGroup, CodeFormatterConstants.FORMATTER_TAB_CHAR,
				FormatterMessages.IndentationTabPage_general_group_option_tab_policy, tabPolicyItems, new String[] {
						FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE,
						FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB,
						FormatterMessages.IndentationTabPage_general_group_option_tab_policy_MIXED });
		tabPolicyListener = new TabPolicyListener(manager);
		tabPolicy.addSelectionListener(tabPolicyListener);
		manager.addInitializeListener(tabPolicyListener);
		indentSize = manager.createNumber(tabPolicyGroup, CodeFormatterConstants.FORMATTER_INDENTATION_SIZE,
				FormatterMessages.IndentationTabPage_general_group_option_indent_size);
		tabSize = manager.createNumber(tabPolicyGroup, CodeFormatterConstants.FORMATTER_TAB_SIZE,
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
	}

}
