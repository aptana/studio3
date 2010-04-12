package com.aptana.terminal.editor;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class TerminalElementFactory implements IElementFactory
{
	public static final String ID = "com.aptana.terminal.TerminalElementFactory"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IElementFactory#createElement(org.eclipse.ui.IMemento)
	 */
	public IAdaptable createElement(IMemento memento)
	{
		TerminalEditorInput input =  new TerminalEditorInput();
		input.loadState(memento);
		return input;
	}
}
