package com.aptana.terminal.server;

import java.util.List;
import java.util.Map;

public interface ProcessConfiguration
{
	/**
	 * Perform any post-processing now that the process has been started. This is typically
	 * used to send commands to the newly created shell
	 * 
	 * @param wrapper
	 */
	void afterStart(ProcessWrapper wrapper);
	
	/**
	 * Perform any last-minute setup to the process builder right before it's going to be started
	 * 
	 * @param wrapper
	 * @param builder
	 */
	void beforeStart(ProcessWrapper wrapper, ProcessBuilder builder);
	
	/**
	 * Get a list of command line arguments that should be sent to the process when it is started
	 * 
	 * @return
	 */
	List<String> getCommandLineArguments();
	
	/**
	 * Return the platform for which this configuration can be used
	 * 
	 * @return
	 */
	String getPlatform();
	
	/**
	 * Get the name of the process to start
	 * 
	 * @return
	 */
	String getProcessName();
	
	/**
	 * Determine if this configuration has all it needs to run properly. For example, this method
	 * could check for the existence of the shell it is going to start. If that does not exist,
	 * then this configuration would not be valid
	 * 
	 * @return
	 */
	boolean isValid();
	
	/**
	 * Add or modify an environment variables to be used when the process is started
	 * 
	 * @param env
	 */
	void setupEnvironment(Map<String,String> env);
}
