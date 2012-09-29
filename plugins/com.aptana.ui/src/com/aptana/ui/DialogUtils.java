/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.util.Set;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.epl.UIEplPlugin;
import com.aptana.ui.preferences.IEplPreferenceConstants;

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

	/**
	 * Conditionally open an information message dialog. In case this is the first time this dialog is opened (defined
	 * by its key), the dialog will be displayed, and a "Do not show this message again" checkbox will be available. The
	 * message dialog will not be opened again when the checkbox is selected.<br>
	 * Once checked, the only way to display this dialog again is by resetting the messaged through the Studio's main
	 * preference page.
	 * 
	 * @param shell
	 * @param title
	 * @param message
	 * @param dialogKey
	 *            A dialog key that will be checked to confirm if the dialog should be diaplayed.
	 * @return The dialog's return code.
	 */
	public static int openIgnoreMessageDialogInformation(Shell shell, String title, String message, String dialogKey)
	{
		final IEclipsePreferences prefs = (EclipseUtil.instanceScope()).getNode(UIEplPlugin.PLUGIN_ID);
		String[] keys = prefs.get(IEplPreferenceConstants.HIDDEN_MESSAGES, StringUtil.EMPTY).split(","); //$NON-NLS-1$
		Set<String> keysSet = CollectionsUtil.newSet(keys);
		if (keysSet.contains(dialogKey))
		{
			return MessageDialog.CANCEL;
		}
		MessageDialogWithToggle dialog = MessageDialogWithToggle.openInformation(shell, title, message,
				Messages.DialogUtils_doNotShowMessageAgain, false, null, null);
		if (dialog.getReturnCode() == Dialog.OK)
		{
			// check the toggle state to see if we need to add the dialog key to the list of hidden dialogs.
			if (dialog.getToggleState())
			{
				keysSet.add(dialogKey);
				prefs.put(IEplPreferenceConstants.HIDDEN_MESSAGES, StringUtil.join(",", keysSet)); //$NON-NLS-1$
				try
				{
					prefs.flush();
				}
				catch (Exception e)
				{
					IdeLog.logError(UIPlugin.getDefault(), e);
				}
			}
		}
		return dialog.getReturnCode();
	}
}
