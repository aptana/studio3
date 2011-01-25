/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.formatter.preferences;

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

import com.aptana.editor.css.formatter.CSSFormatterConstants;
import com.aptana.formatter.ui.CodeFormatterConstants;
import com.aptana.formatter.ui.FormatterMessages;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.preferences.FormatterModifyTabPage;
import com.aptana.formatter.ui.util.SWTFactory;

public class CSSFormatterControlStatementsPage extends FormatterModifyTabPage
{
	private static final String CONTROL_STATEMENTS_PREVIEW_NAME = "preview.css"; //$NON-NLS-1$
	private static final String[] TAB_OPTION_ITEMS = new String[] { CodeFormatterConstants.SPACE,
			CodeFormatterConstants.TAB };
	private static final String[] TAB_OPTION_NAMES = new String[] {
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_SPACE,
			FormatterMessages.IndentationTabPage_general_group_option_tab_policy_TAB };

	public CSSFormatterControlStatementsPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Group generalGroup = SWTFactory.createGroup(parent,
				Messages.CSSFormatterControlStatementsPage_general_group_label, 2, 1, GridData.FILL_HORIZONTAL);
		final Combo tabOptions = manager.createCombo(generalGroup, CSSFormatterConstants.FORMATTER_TAB_CHAR,
				Messages.CSSFormatterControlStatementsPage_tab_policy_group_option, TAB_OPTION_ITEMS, TAB_OPTION_NAMES);
		final Text indentationSize = manager.createNumber(generalGroup,
				CSSFormatterConstants.FORMATTER_INDENTATION_SIZE,
				Messages.CSSFormatterControlStatementsPage_indentation_size_group_option);
		final Text tabSize = manager.createNumber(generalGroup, CSSFormatterConstants.FORMATTER_TAB_SIZE,
				Messages.CSSFormatterControlStatementsPage_tab_size_group_option);

		tabSize.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				int index = tabOptions.getSelectionIndex();
				if (index >= 0)
				{
					final boolean tabMode = CodeFormatterConstants.TAB.equals(TAB_OPTION_ITEMS[index]);
					if (tabMode)
					{
						indentationSize.setText(tabSize.getText());
					}

				}

			}
		});
		new TabOptionHandler(manager, tabOptions, indentationSize);
	}

	/**
	 * Listens to changes in the type of tab selected.
	 */
	private class TabOptionHandler extends SelectionAdapter implements IFormatterControlManager.IInitializeListener
	{

		private IFormatterControlManager manager;
		private Combo tabOptions;
		private Text indentationSize;

		/**
		 * Constructor.
		 * 
		 * @param controlManager
		 */
		public TabOptionHandler(IFormatterControlManager controlManager, Combo tabOptions, Text indentationSize)
		{
			this.manager = controlManager;
			this.tabOptions = tabOptions;
			this.indentationSize = indentationSize;
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
				final boolean tabMode = CodeFormatterConstants.TAB.equals(TAB_OPTION_ITEMS[index]);
				manager.enableControl(indentationSize, !tabMode);
			}
		}

		public void initialize()
		{
			final boolean tabMode = CodeFormatterConstants.TAB.equals(manager
					.getString(CSSFormatterConstants.FORMATTER_TAB_CHAR));
			manager.enableControl(indentationSize, !tabMode);
		}
	}

	protected URL getPreviewContent()
	{
		return getClass().getResource(CONTROL_STATEMENTS_PREVIEW_NAME);
	}
}
