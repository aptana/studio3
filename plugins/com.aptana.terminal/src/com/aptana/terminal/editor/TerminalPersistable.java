package com.aptana.terminal.editor;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class TerminalPersistable implements IPersistableElement
{
	/**
	 * getFactoryId
	 */
	public String getFactoryId()
	{
		return TerminalElementFactory.ID;
	}

	/**
	 * saveState
	 */
	public void saveState(IMemento memento)
	{
		// System.out.println("TerminalPersistable: Save state");
	}
}
