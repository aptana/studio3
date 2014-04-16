/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
// $codepro.audit.disable variableDeclaredInLoop
// $codepro.audit.disable unnecessaryExceptions

package com.aptana.js.debug.ui.internal;

import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.aptana.core.CoreStrings;
import com.aptana.core.util.StringUtil;
import com.aptana.js.debug.ui.JSDebugUIPlugin;
import com.aptana.ui.util.UIUtils;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author Max Stepanov
 */
public class InstallDebuggerPromptStatusHandler implements IStatusHandler
{

	private static final String URL_INSTALL_PDM = "http://go.appcelerator.com/Install+PDM"; //$NON-NLS-1$
	private static final String URL_DOCS_INSTALL_DEBUGGER = "http://go.appcelerator.com/Installing+the+JavaScript+debugger"; //$NON-NLS-1$
	private static final String URL_DOCS_INSTALL_IE_DEBUGGER = "http://go.appcelerator.com/Installing+the+Internet+Explorer+debugger"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.debug.core.IStatusHandler#handleStatus(org.eclipse.core.runtime.IStatus, java.lang.Object)
	 */
	public Object handleStatus(IStatus status, Object source) throws CoreException
	{
		Shell shell = UIUtils.getActiveShell();
		String title = Messages.InstallDebuggerPromptStatusHandler_InstallDebuggerExtension;

		if ("install".equals(source)) { //$NON-NLS-1$
			MessageDialog.openInformation(shell, title,
					Messages.InstallDebuggerPromptStatusHandler_WaitbrowserLaunches_AcceptExtensionInstallation_Quit);
			return null;
		}
		else if ("postinstall".equals(source)) { //$NON-NLS-1$
			MessageDialog.openInformation(shell, title,
					Messages.InstallDebuggerPromptStatusHandler_WaitbrowserLaunches_Quit);
			return null;
		}
		else if ("nopdm".equals(source)) { //$NON-NLS-1$
			MessageDialog md = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title, null, Messages.InstallDebuggerPromptStatusHandler_PDMNotInstalled, MessageDialog.WARNING,
					new String[] { StringUtil.ellipsify(Messages.InstallDebuggerPromptStatusHandler_Download),
							CoreStrings.CONTINUE, CoreStrings.CANCEL, CoreStrings.HELP }, 0);
			switch (md.open())
			{
				case 0:
					WorkbenchBrowserUtil.launchExternalBrowser(URL_INSTALL_PDM, "org.eclipse.ui.browser.ie"); //$NON-NLS-1$
					return Boolean.TRUE;
				case 1:
					return Boolean.TRUE;
				case 3:
					WorkbenchBrowserUtil.launchExternalBrowser(URL_DOCS_INSTALL_IE_DEBUGGER);
					return Boolean.TRUE;
				default:
					break;
			}
			return null;
		}
		else if (source instanceof String && ((String) source).startsWith("quit_")) { //$NON-NLS-1$
			MessageDialog.openInformation(shell, title, MessageFormat.format(
					Messages.InstallDebuggerPromptStatusHandler_BrowserIsRunning, ((String) source).substring(5)));
			return null;
		}
		else if (source instanceof String && ((String) source).startsWith("installed_")) { //$NON-NLS-1$
			MessageDialog.openInformation(shell, title, MessageFormat.format(
					Messages.InstallDebuggerPromptStatusHandler_ExtensionInstalled, ((String) source).substring(10)));
			return null;
		}
		else if (source instanceof String && ((String) source).startsWith("warning_")) { //$NON-NLS-1$
			MessageDialog.openWarning(shell, title, ((String) source).substring(8));
			return null;
		}
		else if (source instanceof String && ((String) source).startsWith("failed_")) { //$NON-NLS-1$
			MessageDialog md = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title, null, MessageFormat.format(
							Messages.InstallDebuggerPromptStatusHandler_ExtensionInstallFailed,
							new Object[] { ((String) source).substring(7) }), MessageDialog.ERROR, new String[] {
							IDialogConstants.OK_LABEL, CoreStrings.HELP }, 0);
			while (true)
			{
				switch (md.open())
				{
					case IDialogConstants.OK_ID:
						return null;
					default:
						break;
				}
				String urlString = (((String) source).indexOf("Internet Explorer") != -1) //$NON-NLS-1$
				? URL_DOCS_INSTALL_IE_DEBUGGER
						: URL_DOCS_INSTALL_DEBUGGER;
				WorkbenchBrowserUtil.launchExternalBrowser(urlString);
			}
		}
		IPreferenceStore store = JSDebugUIPlugin.getDefault().getPreferenceStore();

		String pref = store.getString(IJSDebugUIConstants.PREF_INSTALL_DEBUGGER);
		if (pref != null)
		{
			if (pref.equals(MessageDialogWithToggle.ALWAYS))
			{
				return Boolean.TRUE;
			}
		}
		String message = MessageFormat.format(Messages.InstallDebuggerPromptStatusHandler_ExtensionNotInstalled,
				(String) source);

		MessageDialogWithToggle dialog = new MessageDialogWithToggle(shell, title, null, message,
				MessageDialog.INFORMATION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL,
						CoreStrings.HELP }, 2, null, false);
		dialog.setPrefKey(IJSDebugUIConstants.PREF_INSTALL_DEBUGGER);
		dialog.setPrefStore(store);

		while (true)
		{
			switch (dialog.open())
			{
				case IDialogConstants.YES_ID:
					return Boolean.TRUE;
				case IDialogConstants.NO_ID:
					return Boolean.FALSE;
				default:
					break;
			}
			String urlString = (((String) source).indexOf("Internet Explorer") != -1) //$NON-NLS-1$
			? URL_DOCS_INSTALL_IE_DEBUGGER
					: URL_DOCS_INSTALL_DEBUGGER;
			WorkbenchBrowserUtil.launchExternalBrowser(urlString);
		}
	}
}
