/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

import java.io.File;
import java.io.FileWriter;

public class LocalFileSystem implements IBundleFileSystem
{
	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#createDirectory(java.lang.Object, java.lang.String)
	 */
	public Object createDirectory(Object folder, String name) throws Exception
	{
		File result = new File((File) folder, name);
		
		result.mkdirs();
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#createFile(java.lang.Object, java.lang.String,
	 * java.lang.String)
	 */
	public Object createFile(Object folder, String name, String content) throws Exception
	{
		File result = new File((File) folder, name);
		FileWriter writer = new FileWriter(result);
		
		writer.write(content);
		writer.close();
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#createProject(java.lang.String)
	 */
	public Object createProject(String name) throws Exception
	{
		String tempDirectory = System.getProperty("java.io.tmpdir");
		File result = new File(tempDirectory);
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#deleteDirectory(java.lang.Object)
	 */
	public void deleteDirectory(Object directory) throws Exception
	{
		File current = (File) directory;
		
		for (File file : current.listFiles())
		{
			if (file.isDirectory())
			{
				this.deleteDirectory(file);
			}
			else
			{
				file.delete();
			}
		}
		
		current.delete();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#deleteFile(java.lang.Object)
	 */
	public void deleteFile(Object file) throws Exception
	{
		((File) file).delete();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#deleteProject(java.lang.Object)
	 */
	public void deleteProject(Object project) throws Exception
	{
		// do nothing since the equivalent of a project is the OSes temp directory. We
		// don't want to delete that :)
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#exists(java.lang.Object)
	 */
	public boolean exists(Object file)
	{
		return ((File) file).exists();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#getFile(java.lang.Object, java.lang.String)
	 */
	public Object getFile(Object directory, String name) throws Exception
	{
		return new File((File) directory, name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#moveDirectory(java.lang.Object, java.lang.String)
	 */
	public void moveDirectory(Object directory, String newName) throws Exception
	{
		File current = (File) directory;
		
		current.renameTo(new File(current.getParentFile(), newName));
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleFileSystem#moveFile(java.lang.Object, java.lang.String)
	 */
	public void moveFile(Object file, String newName) throws Exception
	{
		File current = (File) file;
		
		current.renameTo(new File(current.getParentFile(), newName));
	}
}
