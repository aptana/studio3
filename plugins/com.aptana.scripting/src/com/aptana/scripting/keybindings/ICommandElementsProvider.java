package com.aptana.scripting.keybindings;

import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.swt.graphics.Point;

import com.aptana.scripting.model.CommandElement;

/**
 * This interface is used to handle the command elements in the current scope.
 *
 * @author schitale
 */
public interface ICommandElementsProvider
{
	/**
	 * Return the list of CommandElements in the current scope
	 * that are bound to the given key sequence
	 *
	 * @param keySequence to match
	 * @return list of CommandElements
	 */
	List<CommandElement> getCommandElements(KeySequence keySequence);

	/**
	 * Execute the specified CommandElement.
	 *
	 * @param commandElement
	 */
	void execute(CommandElement commandElement);

	/**
	 * Return the display relative location to show the pop-up menu of Commands.
	 *
	 *
	 * @return a suitable display relative location of pop-up. If <code>null</code> is returned the cursor location will be used.
	 */
	Point getCommandElementsPopupLocation();
}
