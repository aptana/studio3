package com.aptana.scripting.model;

import java.io.File;

public interface LoadCycleListener
{
	/**
	 * scriptLoaded
	 * 
	 * @param script
	 */
	void scriptLoaded(File script);
	
	/**
	 * scriptReloaded
	 * 
	 * @param script
	 */
	void scriptReloaded(File script);
	
	/**
	 * scriptUnloaded
	 * 
	 * @param script
	 */
	void scriptUnloaded(File script);
}
