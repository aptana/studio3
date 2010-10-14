package com.aptana.editor.common.preferences;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.aptana.editor.common.tasks.TaskTag;

public class TaskTagInputDialog extends StatusDialog
{

	private TaskTag tag;
	private Text fTagNameText;
	private Combo fPriorityCombo;
	private String fPriority;
	private String fTagName;

	public TaskTagInputDialog(TaskTag tag, Shell parent)
	{
		super(parent);
		this.tag = tag;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		// add controls to composite as necessary
		// Add a text field for name
		fTagNameText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		fTagNameText.setText(tag.getName());
		fTagNameText.addModifyListener(new ModifyListener()
		{

			public void modifyText(ModifyEvent e)
			{
				// TODO Verify that the tag name is unique!
				fTagName = fTagNameText.getText();
			}
		});

		// Add a Combo for priority
		fPriorityCombo = new Combo(composite, SWT.DROP_DOWN | SWT.SINGLE);
		fPriorityCombo.add(TaskTag.HIGH);
		fPriorityCombo.add(TaskTag.NORMAL);
		fPriorityCombo.add(TaskTag.LOW);
		fPriorityCombo.setText(tag.getPriorityName());
		fPriorityCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				fPriority = fPriorityCombo.getText();
			}
		});

		return composite;
	}

	public TaskTag getTaskTag()
	{
		return new TaskTag(fTagName, fPriority);
	}

}
