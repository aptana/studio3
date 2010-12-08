/**
 * This file Copyright (c) 2005-2008 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.internal.ui;

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
import com.aptana.debug.ui.DebugUiPlugin;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * @author Max Stepanov
 */
public class InstallDebuggerPromptStatusHandler implements IStatusHandler
{
	/**
	 * @see org.eclipse.debug.core.IStatusHandler#handleStatus(org.eclipse.core.runtime.IStatus, java.lang.Object)
	 */
	public Object handleStatus(IStatus status, Object source) throws CoreException
	{
		Shell shell = DebugUiPlugin.getActiveWorkbenchShell();
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
			MessageDialog md = new MessageDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title,
					null,
					Messages.InstallDebuggerPromptStatusHandler_PDMNotInstalled,
					MessageDialog.WARNING,
					new String[] {
						StringUtil.ellipsify(Messages.InstallDebuggerPromptStatusHandler_Download),
						CoreStrings.CONTINUE,
						CoreStrings.CANCEL,
						CoreStrings.HELP
						},
					0);
			switch(md.open()) {
				case 0:
					WorkbenchBrowserUtil.launchExternalBrowser("http://www.aptana.com/pro/pdm.php", "org.eclipse.ui.browser.ie"); //$NON-NLS-1$ //$NON-NLS-2$
					/* continue */
				case 1:
					return new Boolean(true);
				case 3:
					WorkbenchBrowserUtil.launchExternalBrowser("http://docs.aptana.com/docs/index.php/Installing_the_IE_debugger"); //$NON-NLS-1$
					return new Boolean(true);					
				default:
					break;
			}
			return null;
		}
		else if (source instanceof String && ((String)source).startsWith("quit_")) { //$NON-NLS-1$
			MessageDialog.openInformation(shell, title, MessageFormat.format(Messages.InstallDebuggerPromptStatusHandler_BrowserIsRunning, ((String)source).substring(5)));
			return null;
		}
		else if ( source instanceof String && ((String)source).startsWith("installed_") ) { //$NON-NLS-1$
			MessageDialog.openInformation(shell, title, MessageFormat.format(Messages.InstallDebuggerPromptStatusHandler_ExtensionInstalled, ((String)source).substring(10)));
			return null;
		}
		else if ( source instanceof String && ((String)source).startsWith("warning_") ) { //$NON-NLS-1$
			MessageDialog.openWarning(shell, title, ((String)source).substring(8));
			return null;
		}
		else if ( source instanceof String && ((String)source).startsWith("failed_") ) { //$NON-NLS-1$
			MessageDialog md = new MessageDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					title,
					null,
					MessageFormat.format(Messages.InstallDebuggerPromptStatusHandler_ExtensionInstallFailed, new Object[] { ((String)source).substring(7) }),
					MessageDialog.ERROR,
					new String[] {
						IDialogConstants.OK_LABEL,
						CoreStrings.HELP
						},
					0);
			while(true)
			{
				switch(md.open())
				{
					case IDialogConstants.OK_ID:
						return null;
					default:
						break;
				}
				String urlString = ((String)source).indexOf("Internet Explorer") != -1 //$NON-NLS-1$
						? "http://docs.aptana.com/docs/index.php/Installing_the_IE_debugger" //$NON-NLS-1$
						: "http://docs.aptana.com/docs/index.php/Installing_the_JavaScript_debugger"; //$NON-NLS-1$
				WorkbenchBrowserUtil.launchExternalBrowser(urlString);
			}
		}
		IPreferenceStore store = DebugUiPlugin.getDefault().getPreferenceStore();

		String pref = store.getString(IDebugUIConstants.PREF_INSTALL_DEBUGGER);
		if (pref != null)
		{
			if (pref.equals(MessageDialogWithToggle.ALWAYS))
			{
				return new Boolean(true);
			}
		}
		String message = MessageFormat.format(Messages.InstallDebuggerPromptStatusHandler_ExtensionNotInstalled, (String)source);

		MessageDialogWithToggle dialog = new MessageDialogWithToggle(shell, title, null, message,
				MessageDialog.INFORMATION, new String[] {
					IDialogConstants.YES_LABEL,
					IDialogConstants.NO_LABEL,
					CoreStrings.HELP },
				2,
				null,
				false);
		dialog.setPrefKey(IDebugUIConstants.PREF_INSTALL_DEBUGGER);
		dialog.setPrefStore(store);
		
		while(true)
		{
			switch(dialog.open())
			{
				case IDialogConstants.YES_ID:
					return new Boolean(true);
				case IDialogConstants.NO_ID:
					return new Boolean(false);
				default:
					break;
			}
			String urlString = ((String)source).indexOf("Internet Explorer") != -1 //$NON-NLS-1$
					? "http://docs.aptana.com/docs/index.php/Installing_the_IE_debugger" //$NON-NLS-1$
					: "http://docs.aptana.com/docs/index.php/Installing_the_JavaScript_debugger"; //$NON-NLS-1$
			WorkbenchBrowserUtil.launchExternalBrowser(urlString);
		}
	}
}
