package com.aptana.terminal.editor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.aptana.terminal.Utils;

/**
 * Shows the terminal editor.
 * 
 * @author schitale
 *
 */
public class ShowTerminalEditorAction implements IWorkbenchWindowActionDelegate
{

	@Override
	public void dispose()
	{
	}

	@Override
	public void init(IWorkbenchWindow workbenchWindow)
	{
	}

	@Override
	public void run(IAction action)
	{
		Utils.openTerminalEditor(TerminalEditor.ID, true);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
