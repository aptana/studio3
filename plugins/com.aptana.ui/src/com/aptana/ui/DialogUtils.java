/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Ingo Muschenetz
 */
public final class DialogUtils
{

	/**
	 * Private constructor for utility class
	 */
	private DialogUtils()
	{
	}

	/**
	 * openIgnoreMessageDialogInformation
	 * 
	 * @param shell
	 * @param title
	 * @param message
	 * @param store
	 * @param key
	 * @return int
	 */
	public static int openIgnoreMessageDialogInformation(Shell shell, String title, String message,
			IPreferenceStore store, String key)
	{
		if (!store.getString(key).equals(MessageDialogWithToggle.ALWAYS))
		{
			MessageDialogWithToggle d = MessageDialogWithToggle.openInformation(shell, title, message,
					Messages.DialogUtils_HideMessage, false, store, key);
			if (d.getReturnCode() == 3)
			{
				return MessageDialog.CANCEL;
			}
		}
		return MessageDialog.OK;
	}

	/**
	 * openIgnoreMessageDialogConfirm
	 * 
	 * @param shell
	 * @param title
	 * @param message
	 * @param store
	 * @param key
	 *            Key to store the show/hide this message. Message will be hidden if true
	 * @return int
	 */
	public static int openIgnoreMessageDialogConfirm(Shell shell, String title, String message, IPreferenceStore store,
			String key)
	{
		if (!store.getString(key).equals(MessageDialogWithToggle.ALWAYS))
		{
			MessageDialogWithToggle d = MessageDialogWithToggle.openYesNoQuestion(shell, title, message,
					Messages.DialogUtils_HideMessage, false, store, key);
			if (d.getReturnCode() == 3)
			{
				return MessageDialog.CANCEL;
			}
		}
		return MessageDialog.OK;
	}
}
