/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license-epl.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.formatter.ui.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.aptana.formatter.preferences.IFieldValidator;
import com.aptana.formatter.ui.IFormatterControlManager;
import com.aptana.formatter.ui.IFormatterModifyDialog;
import com.aptana.formatter.ui.util.SWTFactory;
import com.aptana.formatter.ui.util.StatusInfo;

/**
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public abstract class AbstractFormatterOffOnPage extends FormatterModifyTabPage
{
	private Button onOffButton;
	private Text onText;
	private Text offText;

	/**
	 * @param dialog
	 */
	public AbstractFormatterOffOnPage(IFormatterModifyDialog dialog)
	{
		super(dialog);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.formatter.ui.FormatterModifyTabPage#createOptions(com.aptana.formatter.ui.IFormatterControlManager,
	 * org.eclipse.swt.widgets.Composite)
	 */
	protected void createOptions(IFormatterControlManager manager, Composite parent)
	{
		Composite composite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.FILL_HORIZONTAL);
		Label infoLabel = new Label(composite, SWT.WRAP);
		infoLabel.setText(Messages.FormatterOnOffPage_formatterOffOnInfo);
		new Label(composite, SWT.NONE); // Separator gap
		onOffButton = manager.createCheckbox(composite, getOffOnEnablementKey(),
				Messages.FormatterOnOffPage_formatterEnableOffOn);
		Composite indentedComposite = SWTFactory.createComposite(composite, parent.getFont(), 2, 1,
				GridData.FILL_HORIZONTAL, 20, 5);
		IFieldValidator validator = new EmptyTextValidator();
		offText = manager.createText(indentedComposite, getOffTextIdentifierKey(),
				Messages.FormatterOnOffPage_formatterOffTag, validator);
		onText = manager.createText(indentedComposite, getOnTextIdentifierKey(),
				Messages.FormatterOnOffPage_formatterOnTag, validator);
		// Handle the off/on selections
		new OnOffOptionHandler(manager);
	}

	/**
	 * Returns the properties key for the OFF/ON enablement.
	 */
	protected abstract String getOffOnEnablementKey();

	/**
	 * Returns the properties key for the OFF text.
	 */
	protected abstract String getOffTextIdentifierKey();

	/**
	 * Returns the properties key for the ON text.
	 */
	protected abstract String getOnTextIdentifierKey();

	/*
	 * (non-Javadoc)
	 * @see com.aptana.formatter.ui.preferences.FormatterModifyTabPage#getSubstitutionStrings()
	 */
	@Override
	protected String[] getSubstitutionStrings()
	{
		return new String[] { offText.getText(), onText.getText() };
	}

	/**
	 * Listens to changes in the type of tab selected.
	 */
	private class OnOffOptionHandler extends SelectionAdapter implements IFormatterControlManager.IInitializeListener
	{

		private IFormatterControlManager manager;

		/**
		 * Constructor
		 * 
		 * @param controlManager
		 * @param onOffButton
		 * @param onText
		 * @param offText
		 */
		public OnOffOptionHandler(IFormatterControlManager controlManager)
		{
			this.manager = controlManager;
			onOffButton.addSelectionListener(this);
			manager.addInitializeListener(this);
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e)
		{
			update();
		}

		public void initialize()
		{
			update();
		}

		private void update()
		{
			boolean selection = onOffButton.getSelection();
			manager.enableControl(onText, selection);
			manager.enableControl(offText, selection);
		}
	}

	private class EmptyTextValidator implements IFieldValidator
	{
		public IStatus validate(String text)
		{
			StatusInfo status = new StatusInfo();
			if (onOffButton.getSelection())
			{
				if (text.trim().length() == 0)
				{
					status.setError(com.aptana.formatter.ui.preferences.Messages.FieldIsEmpty);
				}
			}
			return status;
		}
	}
}