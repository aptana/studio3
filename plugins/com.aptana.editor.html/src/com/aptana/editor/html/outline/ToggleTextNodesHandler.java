/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.outline;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.CommonOutlinePage;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.preferences.IPreferenceConstants;

/**
 * @author Michael Xia (mxia@appcelerator.com)
 */
public class ToggleTextNodesHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Command command = event.getCommand();
		boolean oldValue = HandlerUtil.toggleCommandState(command);
		IEclipsePreferences prefs = EclipseUtil.instanceScope().getNode(HTMLPlugin.PLUGIN_ID);
		prefs.putBoolean(IPreferenceConstants.HTML_OUTLINE_SHOW_TEXT_NODES, !oldValue);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e.getMessage(), e);
		}

		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof AbstractThemeableEditor)
		{
			CommonOutlinePage page = ((AbstractThemeableEditor) editor).getOutlinePage();
			page.refresh();
		}
		return null;
	}
}
