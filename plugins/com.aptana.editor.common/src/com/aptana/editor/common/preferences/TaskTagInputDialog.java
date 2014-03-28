/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.preferences;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.core.resources.TaskTag;
import com.aptana.core.util.StringUtil;
import com.aptana.editor.common.CommonEditorPlugin;

class TaskTagInputDialog extends StatusDialog
{

	private Text fTagNameText;
	private Combo fPriorityCombo;
	private String fPriority;
	private String fTagName;
	private List<TaskTag> existingTags;

	public TaskTagInputDialog(TaskTag tag, List<TaskTag> existingTags, Shell parent)
	{
		super(parent);
		this.existingTags = existingTags;
		this.fTagName = tag.getName();
		this.fPriority = tag.getPriorityName();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		((GridLayout) composite.getLayout()).numColumns = 2;
		((GridLayout) composite.getLayout()).makeColumnsEqualWidth = false;

		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setText(Messages.TaskTagInputDialog_NameLabel);

		// Add a text field for name
		fTagNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		fTagNameText.setText(fTagName);
		fTagNameText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				fTagName = fTagNameText.getText().trim();
				verifyUniqueTagName();
			}
		});
		fTagNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label priorityLabel = new Label(composite, SWT.NONE);
		priorityLabel.setText(Messages.TaskTagInputDialog_PriorityLabel);

		// Add a Combo for priority
		fPriorityCombo = new Combo(composite, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
		fPriorityCombo.add(TaskTag.HIGH);
		fPriorityCombo.add(TaskTag.NORMAL);
		fPriorityCombo.add(TaskTag.LOW);
		fPriorityCombo.setText(fPriority);
		fPriorityCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fPriority = fPriorityCombo.getText();
				verifyUniqueTagName();
			}
		});
		fPriorityCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return composite;
	}

	@Override
	public void create()
	{
		super.create();
		// Don't enable OK until the user has entered something
		updateButtonsEnableState(new Status(IStatus.ERROR, CommonEditorPlugin.PLUGIN_ID, StringUtil.EMPTY));
	}

	public TaskTag getTaskTag()
	{
		return new TaskTag(fTagName, fPriority);
	}

	private void verifyUniqueTagName()
	{
		if (fTagName.length() == 0)
		{
			updateStatus(new Status(IStatus.ERROR, CommonEditorPlugin.PLUGIN_ID,
					Messages.TaskTagInputDialog_NonEmptyNameError));
			return;
		}
		for (TaskTag existingTag : existingTags)
		{
			if (existingTag.getName().equals(fTagName))
			{
				// Add a error message to dialog!
				updateStatus(new Status(IStatus.ERROR, CommonEditorPlugin.PLUGIN_ID,
						Messages.TaskTagInputDialog_UniqueNameError));
				return;
			}
		}
		// Remove error message
		updateStatus(Status.OK_STATUS);
	}
}
