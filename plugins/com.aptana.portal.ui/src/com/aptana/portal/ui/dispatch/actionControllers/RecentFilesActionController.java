/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
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
package com.aptana.portal.ui.dispatch.actionControllers;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.EditorHistory;
import org.eclipse.ui.internal.EditorHistoryItem;
import org.eclipse.ui.internal.IPreferenceConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.mortbay.util.ajax.JSON;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A action controller for recent-files related actions.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class RecentFilesActionController extends AbstractActionController
{

	// ############## Actions ###############

	/**
	 * Returns a JSON representation of the recently opened files.
	 */
	@ControllerAction
	public Object getRecentFiles()
	{
		EditorHistory editorHistory = ((Workbench) PortalUIPlugin.getDefault().getWorkbench()).getEditorHistory();
		EditorHistoryItem[] items = editorHistory.getItems();
		int itemsToShow = WorkbenchPlugin.getDefault().getPreferenceStore().getInt(IPreferenceConstants.RECENT_FILES);
		Map<String, String> files = new LinkedHashMap<String, String>();
		if (items != null && itemsToShow > 0)
		{
			for (EditorHistoryItem item : items)
			{
				// Ignore any item that describes the portal browser that we opened.
				IEditorDescriptor editorDescriptor = item.getDescriptor();
				if (editorDescriptor == null || !editorDescriptor.getId().startsWith(PortalUIPlugin.PLUGIN_ID))
				{
					if (itemsToShow-- == 0)
						break;
					files.put(item.getName(), item.getToolTipText());
				}
			}
		}
		return JSON.toString(files);
	}

	/**
	 * Open a recently-opened file in the editor.
	 * 
	 * @param arguments
	 *            A list of file identifiers (paths) that are known to the EditorHistory. The given arguments are
	 *            expected to be an Object array.
	 */
	@ControllerAction
	public Object openRecentFiles(Object arguments)
	{
		// We use a similar mechanism to open a history item as eclipse does at the
		// ReopenEditorMenu class.
		if (!(arguments instanceof Object[]))
		{
			PortalUIPlugin.logError(new Exception(
					"Wrong argument type passed to RecentFilesActionController::openRecentFile. Expected Object[] and got " //$NON-NLS-1$
							+ ((arguments == null) ? "null" : arguments.getClass().getName()))); //$NON-NLS-1$
			return BrowserNotifier.toJSONErrorNotification(IBrowserNotificationConstants.JSON_ERROR_WRONG_ARGUMENTS,
					null);
		}
		Object[] args = (Object[]) arguments;
		if (args != null && args.length > 0)
		{
			Set<String> filesToOpen = new LinkedHashSet<String>();
			for (Object file : args)
			{
				filesToOpen.add(file.toString());
			}
			EditorHistory editorHistory = ((Workbench) PortalUIPlugin.getDefault().getWorkbench()).getEditorHistory();
			EditorHistoryItem[] items = editorHistory.getItems();
			if (items != null)
			{
				for (EditorHistoryItem item : items)
				{
					if (filesToOpen.contains(item.getToolTipText()))
					{
						open(item);
					}
				}
			}
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/*
	 * Reopens the editor for the given history item.
	 */
	private void open(EditorHistoryItem item)
	{
		IWorkbenchPage page = PortalUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null)
		{
			try
			{
				String itemName = item.getName();
				if (!item.isRestored())
				{
					item.restoreState();
				}
				IEditorInput input = item.getInput();
				IEditorDescriptor desc = item.getDescriptor();
				if (input == null || desc == null)
				{
					String title = WorkbenchMessages.OpenRecent_errorTitle;
					String msg = NLS.bind(WorkbenchMessages.OpenRecent_unableToOpen, itemName);
					MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, msg);
				}
				else
				{
					page.openEditor(input, desc.getId());
				}
			}
			catch (PartInitException e2)
			{
				String title = WorkbenchMessages.OpenRecent_errorTitle;
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), title, e2.getMessage());
			}
		}
	}

	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// do nothing
	}
}
