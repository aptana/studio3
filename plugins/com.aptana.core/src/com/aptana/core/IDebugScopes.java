package com.aptana.core;

/**
 * A interface to capture the various scopes available during debugging. These need to match the items in the .options
 * file at the root of the plugin
 * 
 * @author Ingo Muschenetz
 */
public interface IDebugScopes
{
	/**
	 * Items related to the indexing process
	 */
	String INDEXER = "indexer"; //$NON-NLS-1$

	/**
	 * Items related to running things on hte command line
	 */
	String SHELL = "shell"; //$NON-NLS-1$

}
