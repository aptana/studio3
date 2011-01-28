/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LibraryCrossReference
{
	private static final Map<String, Set<String>> LIBS_BY_PATH;
	private static final Map<String, Set<String>> PATHS_BY_LIB;
	private static final String[] NO_STRINGS = new String[0];
	
	private static LibraryCrossReference INSTANCE;
	
	static
	{
		LIBS_BY_PATH = new HashMap<String, Set<String>>();
		PATHS_BY_LIB = new HashMap<String, Set<String>>();
	}
	
	/**
	 * getInstance
	 * 
	 * @return
	 */
	public static LibraryCrossReference getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new LibraryCrossReference();
		}
		
		return INSTANCE;
	}

	/**
	 * LibraryCrossReference
	 */
	private LibraryCrossReference()
	{
	}
	
	/**
	 * getPathsFromLibrary
	 * 
	 * @param libraryPath
	 * @return
	 */
	public String[] getPathsFromLibrary(String libraryPath)
	{
		String[] result = NO_STRINGS;
		
		synchronized (PATHS_BY_LIB)
		{
			Set<String> paths = PATHS_BY_LIB.get(libraryPath);
			
			if (paths != null)
			{
				result = paths.toArray(new String[paths.size()]);
			}
		}
		
		return result;
	}
	
	/**
	 * hasLibrary
	 * 
	 * @param libraryPath
	 * @return
	 */
	public boolean hasLibrary(String libraryPath)
	{
		return PATHS_BY_LIB.containsKey(libraryPath);
	}
	
	/**
	 * registerLibraryReference
	 * 
	 * @param scriptPath
	 * @param libraryPath
	 */
	public void registerLibraryReference(String scriptPath, String ... libraryPaths)
	{
		synchronized (PATHS_BY_LIB)
		{
			for (String libraryPath : libraryPaths)
			{
				if (PATHS_BY_LIB.containsKey(libraryPath) == false)
				{
					PATHS_BY_LIB.put(libraryPath, new HashSet<String>());
				}
				
				Set<String> paths = PATHS_BY_LIB.get(libraryPath);
				
				paths.add(scriptPath);
			}
		}
		synchronized (LIBS_BY_PATH)
		{
			if (LIBS_BY_PATH.containsKey(scriptPath) == false)
			{
				LIBS_BY_PATH.put(scriptPath, new HashSet<String>());
			}
			
			Set<String> libs = LIBS_BY_PATH.get(scriptPath);
			
			for (String libraryPath : libraryPaths)
			{
				libs.add(libraryPath);
			}
		}
	}
	
	/**
	 * unregisterPath
	 * 
	 * @param path
	 */
	public void unregisterPath(String path)
	{
		Set<String> libs = null;
		
		synchronized (LIBS_BY_PATH)
		{
			if (LIBS_BY_PATH.containsKey(path))
			{
				// get the list of libs associated with this element
				libs = LIBS_BY_PATH.get(path);
				
				// don't track libs for this element anymore
				LIBS_BY_PATH.remove(path);
				
			}
		}
		
		if (libs != null)
		{
			synchronized (PATHS_BY_LIB)
			{
				// process each lib that was associated with this element
				for (String lib : libs)
				{
					// grab the list of scripts using this lib
					Set<String> paths = PATHS_BY_LIB.get(lib);
					
					if (paths != null)
					{
						// remove this element from the list
						paths.remove(path);
					}
					
					// remove the entire path list if that was the last one in the list
					if (paths.size() == 0)
					{
						PATHS_BY_LIB.remove(lib);
					}
				}
			}
		}
	}
}
