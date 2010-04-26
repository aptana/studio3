package com.aptana.portal.ui.dispatch;

import com.aptana.configurations.processor.ConfigurationProcessorsRegistry;
import com.aptana.configurations.processor.IConfigurationProcessor;

/**
 * An action controller interface for contributed controllers that will be invoked when a JavaScript request is coming
 * from the internal browser (Portal). Note that there is no consideration here for the arguments that method can
 * accept. The concrete class should not overload methods, and the methods names should be unique.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IActionController
{
	/**
	 * Returns the available actions (methods) for this controller.
	 * 
	 * @return A list of available actions (methods)
	 */
	public String[] getActions();

	/**
	 * Returns true if this controller has an action with the given name. The action name is translated to a method name
	 * on the concrete class, and in this method will return true if a method exists. Note that there is no
	 * consideration here for the arguments that method can accept. The concrete class should not overload methods, and
	 * the methods names should be unique.
	 * 
	 * @param action
	 * @return True if this controller has the action (method); False, otherwise.
	 */
	public boolean hasAction(String action);

	/**
	 * Invoke an action (method) with the given parameters.
	 * 
	 * @param action
	 *            The action name
	 * @param args
	 *            The action arguments. Can be a null, Map, Object array or primitive array parsed from the JSON
	 * @return The invocation result
	 */
	public Object invokeAction(String action, Object args);

	/**
	 * Assign an ID of an {@link IConfigurationProcessor} that can be used by this action controller when needed.
	 * 
	 * @param id
	 *            The configuration processor ID, as defined in its contributing extension.
	 */
	public void setConfigurationProcessorId(String id);

	/**
	 * Returns the {@link IConfigurationProcessor} that was assigned to this action controller.<br>
	 * This ID can then be used to retrieve an instance of an IConfigurationProcessor from the
	 * {@link ConfigurationProcessorsRegistry} class.
	 * 
	 * @return The ID of the configuration processor, or null if none was ever set.
	 */
	public String getConfigurationProcessorId();
}
