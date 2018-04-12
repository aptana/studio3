/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package com.aptana.formatter.ui.dialogFields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.aptana.formatter.ui.util.SWTUtil;

/**
 * Dialog Field containing a single button such as a radio or checkbox button.
 */
public class SelectionButtonDialogField extends DialogField
{

	private Button fButton;
	private boolean fIsSelected;
	private DialogField[] fAttachedDialogFields;
	private int fButtonStyle;

	/**
	 * Creates a selection button. Allowed button styles: SWT.RADIO, SWT.CHECK, SWT.TOGGLE, SWT.PUSH
	 */
	public SelectionButtonDialogField(int buttonStyle)
	{
		super();
		fIsSelected = false;
		fAttachedDialogFields = null;
		fButtonStyle = buttonStyle;
	}

	/**
	 * Attaches fields to the selection state of the selection button. The attached fields will be disabled if the
	 * selection button is not selected.
	 */
	public void attachDialogFields(DialogField[] dialogFields)
	{
		fAttachedDialogFields = dialogFields;
		for (int i = 0; i < dialogFields.length; i++)
		{
			dialogFields[i].setEnabled(fIsSelected);
		}
	}

	// ------- layout helpers

	/*
	 * @see DialogField#doFillIntoGrid
	 */
	@Override
	public Control[] doFillIntoGrid(Composite parent, int nColumns)
	{
		assertEnoughColumns(nColumns);

		Button button = getSelectionButton(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = nColumns;
		gd.horizontalAlignment = GridData.FILL;
		if (fButtonStyle == SWT.PUSH)
		{
			gd.widthHint = SWTUtil.getButtonWidthHint(button);
		}

		button.setLayoutData(gd);

		return new Control[] { button };
	}

	@Override
	public int getNumberOfControls()
	{
		return 1;
	}

	public Button getSelectionButton()
	{
		return getSelectionButton(null);
	}

	// ------- ui creation

	/**
	 * Returns the selection button widget. When called the first time, the widget will be created.
	 * 
	 * @param group
	 *            The parent composite when called the first time, or <code>null</code> after.
	 */
	public Button getSelectionButton(Composite group)
	{
		if (fButton == null)
		{
			assertCompositeNotNull(group);

			fButton = new Button(group, fButtonStyle);
			fButton.setFont(group.getFont());
			fButton.setText(fLabelText);
			fButton.setEnabled(isEnabled());
			fButton.setSelection(fIsSelected);
			fButton.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(SelectionEvent e)
				{
					doWidgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e)
				{
					doWidgetSelected(e);
				}
			});
		}
		return fButton;
	}

	private void doWidgetSelected(SelectionEvent e)
	{
		if (isOkToUse(fButton))
		{
			changeValue(fButton.getSelection());
		}
	}

	private void changeValue(boolean newState)
	{
		if (fIsSelected != newState)
		{
			fIsSelected = newState;
			if (fAttachedDialogFields != null)
			{
				boolean focusSet = false;
				for (int i = 0; i < fAttachedDialogFields.length; i++)
				{
					fAttachedDialogFields[i].setEnabled(fIsSelected);
					if (fIsSelected && !focusSet)
					{
						focusSet = fAttachedDialogFields[i].setFocus();
					}
				}
			}
			dialogFieldChanged();
		}
		else if (fButtonStyle == SWT.PUSH)
		{
			dialogFieldChanged();
		}
	}

	@Override
	public void setLabelText(String labeltext)
	{
		fLabelText = labeltext;
		if (isOkToUse(fButton))
		{
			fButton.setText(labeltext);
		}
	}

	// ------ model access

	/**
	 * Returns the selection state of the button.
	 */
	public boolean isSelected()
	{
		return fIsSelected;
	}

	/**
	 * Sets the selection state of the button.
	 */
	public void setSelection(boolean selected)
	{
		changeValue(selected);
		if (isOkToUse(fButton))
		{
			fButton.setSelection(selected);
		}
	}

	// ------ enable / disable management

	@Override
	protected void updateEnableState()
	{
		super.updateEnableState();
		if (isOkToUse(fButton))
		{
			fButton.setEnabled(isEnabled());
		}
	}

	@Override
	public void refresh()
	{
		super.refresh();
		if (isOkToUse(fButton))
		{
			fButton.setSelection(fIsSelected);
		}
	}

}
