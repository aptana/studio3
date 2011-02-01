/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.scripting.model;

public class BundleFileSystemService
{
	private static final String PROJECT_NAME = "TestProject";

	private IBundleFileSystem _fs;
	private Object _project;
	private Object _bundlesDirectory;
	private Object _bundleDirectory;
	private Object _commandsDirectory;
	private Object _snippetsDirectory;
	private Object _libDirectory;

	/**
	 * BundleFileSystemService
	 */
	public BundleFileSystemService(IBundleFileSystem fileSystem)
	{
		this._fs = fileSystem;
	}

	/**
	 * bundleDirectoryExists
	 * 
	 * @return
	 */
	public boolean bundleDirectoryExists()
	{
		return this._fs.exists(this._bundleDirectory);
	}

	/**
	 * bundleFileExists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean bundleFileExists() throws Exception
	{
		return this._fs.exists(this.getBundleFile());
	}

	/**
	 * cleanup
	 * 
	 * @throws Exception
	 */
	public void cleanUp() throws Exception
	{
		// delete bundles directory -- needed for local file system cleanup
		this._fs.deleteDirectory(this._bundlesDirectory);

		// delete project
		this._fs.deleteProject(this._project);
	}

	/**
	 * commandExists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean commandExists() throws Exception
	{
		return this._fs.exists(this.getCommandFile());
	}

	/**
	 * commandsDirectoryExists
	 * 
	 * @return
	 */
	public boolean commandsDirectoryExists()
	{
		return this._fs.exists(this._commandsDirectory);
	}

	/**
	 * createBundleDirectory
	 * 
	 * @throws Exception
	 */
	public void createBundleDirectory() throws Exception
	{
		this._project = this._fs.createProject(PROJECT_NAME);
		this._bundlesDirectory = this._fs.createDirectory(this._project, "bundles");
		this._bundleDirectory = this._fs.createDirectory(this._bundlesDirectory, BundleMonitorTests.BUNDLE_NAME);
		this._commandsDirectory = this._fs.createDirectory(this._bundleDirectory, "commands");
		this._snippetsDirectory = this._fs.createDirectory(this._bundleDirectory, "snippets");
		this._libDirectory = this._fs.createDirectory(this._bundleDirectory, "lib");
	}

	/**
	 * createBundleFile
	 * 
	 * @param content
	 * @throws Exception
	 */
	public void createBundleFile(String content) throws Exception
	{
		this._fs.createFile(this._bundleDirectory, BundleMonitorTests.BUNDLE_FILE_NAME, content);
	}

	/**
	 * createCommand
	 * 
	 * @param content
	 * @throws Exception
	 */
	public void createCommand(String content) throws Exception
	{
		this._fs.createFile(this._commandsDirectory, BundleMonitorTests.COMMAND_NAME + ".rb", content);
	}

	/**
	 * createLib
	 * 
	 * @param content
	 * @throws Exception
	 */
	public void createLib(String content) throws Exception
	{
		this._fs.createFile(this._libDirectory, BundleMonitorTests.LIB_NAME + ".rb", content);
	}

	/**
	 * createSnippet
	 * 
	 * @param content
	 * @throws Exception
	 */
	public void createSnippet(String content) throws Exception
	{
		this._fs.createFile(this._snippetsDirectory, BundleMonitorTests.SNIPPET_NAME + ".rb", content);
	}

	/**
	 * deleteBundleDirectory
	 * 
	 * @throws Exception
	 */
	public void deleteBundleDirectory() throws Exception
	{
		this._fs.deleteDirectory(this._bundleDirectory);
	}

	/**
	 * deleteBundleFile
	 * 
	 * @throws Exception
	 */
	public void deleteBundleFile() throws Exception
	{
		this._fs.deleteFile(this.getBundleFile());
	}

	/**
	 * deleteCommand
	 * 
	 * @throws Exception
	 */
	public void deleteCommand() throws Exception
	{
		this._fs.deleteFile(this.getCommandFile());
	}

	/**
	 * deleteCommandsDirectory
	 * 
	 * @throws Exception
	 */
	public void deleteCommandsDirectory() throws Exception
	{
		this._fs.deleteDirectory(this._commandsDirectory);
	}

	/**
	 * deleteLib
	 * 
	 * @throws Exception
	 */
	public void deleteLib() throws Exception
	{
		this._fs.deleteDirectory(this.getLibFile());
	}

	/**
	 * deleteLibDirectory
	 * 
	 * @throws Exception
	 */
	public void deleteLibDirectory() throws Exception
	{
		this._fs.deleteDirectory(this._libDirectory);
	}

	/**
	 * deleteSnippet
	 * 
	 * @throws Exception
	 */
	public void deleteSnippet() throws Exception
	{
		this._fs.deleteFile(this.getSnippetFile());
	}

	/**
	 * deleteSnippetsDirectory
	 * 
	 * @throws Exception
	 */
	public void deleteSnippetsDirectory() throws Exception
	{
		this._fs.deleteDirectory(this._snippetsDirectory);
	}

	/**
	 * getBundleFile
	 * 
	 * @return
	 */
	public Object getBundleFile() throws Exception
	{
		return this._fs.getFile(this._bundleDirectory, BundleMonitorTests.BUNDLE_FILE_NAME);
	}

	/**
	 * getCommandFile
	 * 
	 * @return
	 */
	public Object getCommandFile() throws Exception
	{
		return this._fs.getFile(this._commandsDirectory, BundleMonitorTests.COMMAND_NAME + ".rb");
	}

	/**
	 * getLibFile
	 * 
	 * @return
	 * @throws Exception
	 */
	public Object getLibFile() throws Exception
	{
		return this._fs.getFile(this._libDirectory, BundleMonitorTests.LIB_NAME + ".rb");
	}

	/**
	 * getSnippetFile
	 * 
	 * @return
	 */
	public Object getSnippetFile() throws Exception
	{
		return this._fs.getFile(this._snippetsDirectory, BundleMonitorTests.SNIPPET_NAME + ".rb");
	}

	/**
	 * libDirectoryExists
	 * 
	 * @return
	 */
	public boolean libDirectoryExists()
	{
		return this._fs.exists(this._libDirectory);
	}

	/**
	 * libExists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean libExists() throws Exception
	{
		return this._fs.exists(this.getLibFile());
	}

	/**
	 * moveBundleDirectory
	 * 
	 * @param newName
	 * @throws Exception
	 */
	public void moveBundleDirectory(String newName) throws Exception
	{
		this._fs.moveDirectory(this._bundleDirectory, newName);
	}

	/**
	 * moveBundleFile
	 * 
	 * @param newName
	 * @throws Exception
	 */
	public void moveBundleFile(String newName) throws Exception
	{
		this._fs.moveFile(this.getBundleFile(), newName);
	}

	/**
	 * moveCommand
	 * 
	 * @param newName
	 * @throws Exception
	 */
	public void moveCommand(String newName) throws Exception
	{
		this._fs.moveFile(this.getCommandFile(), newName);
	}

	/**
	 * moveCommandsDirectory
	 * 
	 * @param newName
	 * @throws Exception
	 */
	public void moveCommandsDirectory(String newName) throws Exception
	{
		this._fs.moveDirectory(this._commandsDirectory, newName);
	}

	/**
	 * moveSnippet
	 * 
	 * @param newName
	 * @throws Exception
	 */
	public void moveSnippet(String newName) throws Exception
	{
		this._fs.moveFile(this.getSnippetFile(), newName);
	}

	/**
	 * moveSnippetsDirectory
	 * 
	 * @param newName
	 * @throws Exception
	 */
	public void moveSnippetsDirectory(String newName) throws Exception
	{
		this._fs.moveDirectory(this._snippetsDirectory, newName);
	}

	/**
	 * snippetExists
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean snippetExists() throws Exception
	{
		return this._fs.exists(this.getSnippetFile());
	}

	/**
	 * snippetsDirectoryExists
	 * 
	 * @return
	 */
	public boolean snippetsDirectoryExists()
	{
		return this._fs.exists(this._snippetsDirectory);
	}
}
