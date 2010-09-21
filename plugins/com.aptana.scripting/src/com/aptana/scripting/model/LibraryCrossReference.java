/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
