package com.aptana.util;

import java.io.File;

public class FileUtils
{
	private FileUtils()
	{
	}
	
	/**
	 * buildPath
	 * 
	 * @param rootDirectory
	 * @param parts
	 * @return
	 */
	static public File buildPath(File rootDirectory, String ... parts)
	{
		File result = null;
		
		if (rootDirectory != null)
		{
			result = rootDirectory;
			
			for (String part : parts)
			{
				result = new File(result, part);
			}
		}
		
		return result;
	}
}
