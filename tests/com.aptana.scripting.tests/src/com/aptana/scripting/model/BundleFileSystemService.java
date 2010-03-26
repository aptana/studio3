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

	/**
	 * BundleFileSystemService
	 */
	public BundleFileSystemService(IBundleFileSystem fileSystem)
	{
		this._fs = fileSystem;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#cleanUp()
	 */
	public void cleanUp() throws Exception
	{
		// delete bundles directory -- needed for local file system cleanup
		this._fs.deleteDirectory(this._bundlesDirectory);

		// delete project
		this._fs.deleteProject(this._project);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#createBundleDirectory()
	 */
	public void createBundleDirectory() throws Exception
	{
		this._project = this._fs.createProject(PROJECT_NAME);
		this._bundlesDirectory = this._fs.createDirectory(this._project, "bundles");
		this._bundleDirectory = this._fs.createDirectory(this._bundlesDirectory, BundleMonitorTests.BUNDLE_NAME);
		this._commandsDirectory = this._fs.createDirectory(this._bundleDirectory, "commands");
		this._snippetsDirectory = this._fs.createDirectory(this._bundleDirectory, "snippets");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#createBundleFile(java.lang.String)
	 */
	public void createBundleFile(String content) throws Exception
	{
		this._fs.createFile(this._bundleDirectory, BundleMonitorTests.BUNDLE_FILE_NAME, content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#createCommand(java.lang.String)
	 */
	public void createCommand(String content) throws Exception
	{
		this._fs.createFile(this._commandsDirectory, BundleMonitorTests.COMMAND_NAME + ".rb", content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#createSnippet(java.lang.String)
	 */
	public void createSnippet(String content) throws Exception
	{
		this._fs.createFile(this._snippetsDirectory, BundleMonitorTests.SNIPPET_NAME + ".rb", content);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#deleteBundleDirectory()
	 */
	public void deleteBundleDirectory() throws Exception
	{
		this._fs.deleteDirectory(this._bundleDirectory);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#deleteBundleFile()
	 */
	public void deleteBundleFile() throws Exception
	{
		this._fs.deleteFile(this.getBundleFile());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#deleteCommand()
	 */
	public void deleteCommand() throws Exception
	{
		this._fs.deleteFile(this.getCommandFile());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#deleteCommandsDirectory()
	 */
	public void deleteCommandsDirectory() throws Exception
	{
		this._fs.deleteDirectory(this._commandsDirectory);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#deleteSnippet()
	 */
	public void deleteSnippet() throws Exception
	{
		this._fs.deleteFile(this.getSnippetFile());
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#deleteSnippetsDirectory()
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
	 * getSnippetFile
	 * 
	 * @return
	 */
	public Object getSnippetFile() throws Exception
	{
		return this._fs.getFile(this._snippetsDirectory, BundleMonitorTests.SNIPPET_NAME + ".rb");
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#moveBundleDirectory(java.lang.String)
	 */
	public void moveBundleDirectory(String newName) throws Exception
	{
		this._fs.moveDirectory(this._bundleDirectory, newName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#moveBundleFile(java.lang.String)
	 */
	public void moveBundleFile(String newName) throws Exception
	{
		this._fs.moveFile(this.getBundleFile(), newName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#moveCommand(java.lang.String)
	 */
	public void moveCommand(String newName) throws Exception
	{
		this._fs.moveFile(this.getCommandFile(), newName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#moveCommandsDirectory(java.lang.String)
	 */
	public void moveCommandsDirectory(String newName) throws Exception
	{
		this._fs.moveDirectory(this._commandsDirectory, newName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#moveSnippet(java.lang.String)
	 */
	public void moveSnippet(String newName) throws Exception
	{
		this._fs.moveFile(this.getSnippetFile(), newName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.IBundleDirectoryManager#moveSnippetsDirectory(java.lang.String)
	 */
	public void moveSnippetsDirectory(String newName) throws Exception
	{
		this._fs.moveDirectory(this._snippetsDirectory, newName);
	}
}
