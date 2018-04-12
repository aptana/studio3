/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.build.ui.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.aptana.build.ui.internal.preferences.Messages;
import com.aptana.core.build.IBuildParticipantWorkingCopy;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.widgets.CListTable;

public class ValidatorFiltersPreferenceComposite extends Composite
{

	private CListTable filterViewer;
	private IBuildParticipantWorkingCopy participant;

	public ValidatorFiltersPreferenceComposite(Composite parent, IBuildParticipantWorkingCopy participantChanges)
	{
		super(parent, SWT.NONE);
		this.participant = participantChanges;
		setLayout(new FillLayout());
		createFiltersComposite(this);
	}

	private Control createFiltersComposite(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.ValidationPreferencePage_LBL_Filter);
		group.setLayout(GridLayoutFactory.fillDefaults().margins(4, 4).create());

		filterViewer = new CListTable(group, SWT.NONE);
		filterViewer.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 300).create());
		filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_SelectParticipant);
		final IInputValidator inputValidator = new IInputValidator()
		{

			public String isValid(String newText)
			{
				if (StringUtil.isEmpty(newText))
				{
					return Messages.ValidationPreferencePage_ERR_EmptyExpression;
				}
				return null;
			}
		};
		filterViewer.addListener(new CListTable.Listener()
		{

			public Object addItem()
			{
				InputDialog dialog = new InputDialog(getShell(), Messages.ValidationPreferencePage_Ignore_Title,
						Messages.ValidationPreferencePage_Ignore_Message, null, inputValidator);
				if (dialog.open() == Window.OK)
				{
					return dialog.getValue();
				}
				return null;
			}

			public Object editItem(Object item)
			{
				String expression = item.toString();
				InputDialog dialog = new InputDialog(getShell(), Messages.ValidationPreferencePage_Ignore_Title,
						Messages.ValidationPreferencePage_Ignore_Message, expression, inputValidator);
				if (dialog.open() == Window.OK)
				{
					return dialog.getValue();
				}
				// the dialog is canceled; returns the original item
				return item;
			}

			public void itemsChanged(List<Object> rawFilters)
			{
				// Store the new filter expressions in our temporary copy
				String[] filters = new String[rawFilters.size()];
				int i = 0;
				for (Object item : rawFilters)
				{
					filters[i++] = item.toString();
				}
				participant.setFilters(filters);
			}
		});

		filterViewer.setEnabled(true);
		filterViewer.setDescription(Messages.ValidationPreferencePage_Filter_Description);
		List<String> expressions = participant.getFilters();
		filterViewer.setItems(expressions.toArray());

		return group;
	}

}
