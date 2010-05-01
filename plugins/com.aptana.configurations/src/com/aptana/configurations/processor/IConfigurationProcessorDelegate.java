package com.aptana.configurations.processor;

import java.util.Set;

/**
 * A base interface for configuration processor delegates.<br>
 * These delegates usually support a specific set of commands, and this class can be queried about those commands, along
 * with some basic functionalities such as installation capabilities, version reading etc.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public interface IConfigurationProcessorDelegate
{
	/**
	 * Version retrieving command type
	 */
	public static final String VERSION_COMMAND = "version"; //$NON-NLS-1$
	/**
	 * Install command type
	 */
	public static final String INSTALL_COMMAND = "install"; //$NON-NLS-1$

	/**
	 * Uninstall command type
	 */
	public static final String UNINSTALL_COMMAND = "uninstall"; //$NON-NLS-1$

	/**
	 * Returns a lower-case name of the application that this delegate supports.
	 * 
	 * @return
	 */
	public abstract String getSupportedApplication();

	/**
	 * Returns the commands that this delegate support.
	 * 
	 * @return A supported commands set.
	 */
	public abstract Set<String> getSupportedCommands();

	/**
	 * Runs a command.
	 * 
	 * @param commandType
	 *            One of the commands that was returned in the {@link #getSupportedCommands()} as available for this
	 *            delegate.
	 * @return An object representing the result of this command (can be null).
	 */
	public abstract Object runCommand(String commandType);
}
