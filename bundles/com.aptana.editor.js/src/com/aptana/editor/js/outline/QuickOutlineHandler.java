/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.outline;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.QuickOutlinePopupDialog;

public class QuickOutlineHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event)
	{
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor instanceof AbstractThemeableEditor)
		{
			QuickOutlinePopupDialog dialog = new QuickOutlinePopupDialog(editor.getSite().getShell(),
					(AbstractThemeableEditor) editor);
			dialog.open();
		}
		return null;
	}

}
