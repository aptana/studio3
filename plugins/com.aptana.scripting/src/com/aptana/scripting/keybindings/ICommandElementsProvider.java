package com.aptana.scripting.keybindings;

import java.util.List;

import org.eclipse.jface.bindings.keys.KeySequence;

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
}
