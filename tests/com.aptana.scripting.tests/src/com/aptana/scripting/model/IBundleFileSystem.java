/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public interface IBundleFileSystem
{
	/**
	 * createDirectory
	 * 
	 * @param project
	 * @param string
	 * @return
	 */
	Object createDirectory(Object project, String string) throws Exception;

	/**
	 * createFile
	 * 
	 * @param bundlesDirectory
	 * @param bundleFileName
	 * @param content
	 */
	Object createFile(Object bundlesDirectory, String bundleFileName, String content) throws Exception;

	/**
	 * createProject
	 * 
	 * @param projectName
	 * @return
	 */
	Object createProject(String projectName) throws Exception;

	/**
	 * deleteDirectory
	 * 
	 * @param bundleDirectory
	 */
	void deleteDirectory(Object bundleDirectory) throws Exception;

	/**
	 * deleteFile
	 * 
	 * @param bundleFile
	 */
	void deleteFile(Object bundleFile) throws Exception;

	/**
	 * deleteProject
	 * 
	 * @param project
	 */
	void deleteProject(Object project) throws Exception;

	/**
	 * exists
	 * 
	 * @param file
	 * @return
	 */
	boolean exists(Object file);

	/**
	 * getFile
	 * 
	 * @param bundleDirectory
	 * @param bundleFileName
	 * @return
	 */
	Object getFile(Object bundleDirectory, String bundleFileName) throws Exception;

	/**
	 * moveDirectory
	 * 
	 * @param bundleDirectory
	 * @param newName
	 */
	void moveDirectory(Object bundleDirectory, String newName) throws Exception;

	/**
	 * moveFile
	 * 
	 * @param bundleFile
	 * @param newName
	 */
	void moveFile(Object bundleFile, String newName) throws Exception;
}
