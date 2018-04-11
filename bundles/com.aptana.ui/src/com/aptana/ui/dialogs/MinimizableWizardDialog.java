/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.aptana.ui.util.SWTUtils;

/**
 * A {@link WizardDialog} implementation that allows replacing the "Finish" button with a "Hide" button, and has a
 * default behavior for hiding the wizard. By default, the wizard will be hidden, and a toast will appear on the bottom
 * right side of the studio. Clicking the toast will show the wizard again.<br>
 * 
 * @author sgibly@appcelerator.com
 */
public class MinimizableWizardDialog extends WizardDialog
{
	private boolean hideOnFinish;
	private String infoTitle;
	private String infoMessage;
	private GenericInfoPopupDialog toast;

	/**
	 * Constructs a new MinimizableWizardDialog. By default, the wizard dialog will act as a regular
	 * {@link WizardDialog} , and will display a "Finish" button. In case you wish to change the finish into "Hide",
	 * call the {@link #enableHiding(boolean)} with <code>true</code>. This is useful when you only want to display the
	 * "Hide" when a specific condition is met.
	 * 
	 * @param parentShell
	 * @param newWizard
	 * @param infoTitle
	 *            The title to display for the 'toast' in case the 'Hide' is clicked.
	 * @param infoMessage
	 *            The message to display in the 'toast' in case the 'Hide' is clicked.
	 */
	public MinimizableWizardDialog(Shell parentShell, IWizard newWizard, String infoTitle, String infoMessage)
	{
		super(parentShell, newWizard);
		this.infoTitle = infoTitle;
		this.infoMessage = infoMessage;
	}

	/**
	 * Constructs a new MinimizableWizardDialog.
	 * 
	 * @param parentShell
	 * @param newWizard
	 * @param hideOnFinish
	 *            When <code>true</code>, indicate the this wizard dialog will display "Hide" instead of "Finish" right
	 *            when it opens.
	 * @param infoTitle
	 *            The title to display for the 'toast' in case the 'Hide' is clicked.
	 * @param infoMessage
	 *            The message to display in the 'toast' in case the 'Hide' is clicked.
	 */
	public MinimizableWizardDialog(Shell parentShell, IWizard newWizard, boolean hideOnFinish, String infoTitle,
			String infoMessage)
	{
		this(parentShell, newWizard, infoTitle, infoMessage);
		this.hideOnFinish = hideOnFinish;
	}

	/**
	 * The Hide button has been pressed.
	 */
	public void hidePressed()
	{
		if (hideOnFinish)
		{
			final Shell activeShell = getShell();
			toast = new GenericInfoPopupDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					infoTitle, infoMessage, new Runnable()
					{
						public void run()
						{
							activeShell.setVisible(true);
						}
					});
			toast.open();
			activeShell.setVisible(false);

			activeShell.addListener(SWT.Show, new Listener()
			{
				public void handleEvent(Event event)
				{
					if (toast != null)
					{
						// Incase if the shell is opened through other source, close the toast
						toast.close();
					}
				}
			});

			activeShell.addShellListener(new ShellAdapter()
			{
				@Override
				public void shellClosed(ShellEvent e)
				{
					if (toast != null)
					{
						// In case the shell gets closed programatically, close the toast.
						toast.close();
					}
				}
			});
		}

	}

	/**
	 * Enables the wizard to hide. When <code>true</code>, the 'Finish' button is replaced with 'Hide' button. On
	 * pressing Hide button, the dialog is hidden and a 'toast' appears at the bottom of the screen. Clicking the toast
	 * will bring back the dialog onto the screen.
	 * 
	 * @param enabled
	 */
	public void enableHiding(boolean enabled)
	{
		if (hideOnFinish != enabled)
		{
			hideOnFinish = enabled;
			updateFinishButton(getButton(IDialogConstants.FINISH_ID));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButton(org.eclipse.swt.widgets.Composite, int, java.lang.String,
	 * boolean)
	 */
	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton)
	{
		Button b = super.createButton(parent, id, label, defaultButton);
		if (hideOnFinish && id == IDialogConstants.FINISH_ID)
		{
			updateFinishButton(b);
		}
		return b;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardDialog#finishPressed()
	 */
	@Override
	protected void finishPressed()
	{
		if (hideOnFinish)
		{
			hidePressed();
		}
		else
		{
			super.finishPressed();
		}
	}

	protected void updateFinishButton(Button button)
	{
		if (button != null)
		{
			button.setText(hideOnFinish ? Messages.MinimizableWizardDialog_hideLabel : IDialogConstants.FINISH_LABEL);
		}
	}

	/**
	 * Updates the next and previous buttons since finish was already clicked
	 */
	public void disableButtonsOnFinish()
	{
		int[] ids = new int[] { IDialogConstants.NEXT_ID, IDialogConstants.BACK_ID };

		for (int id : ids)
		{
			Button button = getButton(id);
			if (!SWTUtils.isControlDisposed(button))
			{
				button.setEnabled(false);
			}
		}
	}

	public GenericInfoPopupDialog getToastPopup()
	{
		return toast;
	}
}
