package com.aptana.editor.js.outline;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.aptana.editor.common.AbstractThemeableEditor;
import com.aptana.editor.common.outline.QuickOutlinePopupDialog;
import com.aptana.editor.common.outline.CommonQuickOutlinePage;

public class QuickOutlineHandler extends AbstractHandler
{

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		CommonQuickOutlinePage page = new CommonQuickOutlinePage((AbstractThemeableEditor) editor);
		// FIXME The "info text" is a status field text.
		QuickOutlinePopupDialog dialog = new QuickOutlinePopupDialog(editor.getSite().getShell(),
				(AbstractThemeableEditor) editor, page, "!!!!info text!!!!");

		dialog.open();
		return null;
	}

}
