package com.aptana.editor.common.scripting.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * A no-op handler.
 * 
 * @author schitale
 *
 */
public class ShowCommandsMenuHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return null;
	}
}
