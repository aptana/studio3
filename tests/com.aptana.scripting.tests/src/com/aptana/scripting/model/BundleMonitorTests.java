package com.aptana.scripting.model;

import junit.framework.TestCase;

public abstract class BundleMonitorTests extends TestCase
{
	public static final String BUNDLE_NAME = "TestBundle";
	public static final String BUNDLE_FILE_NAME = "bundle.rb";
	public static final String COMMAND_NAME = "MyCommand";
	public static final String SNIPPET_NAME = "MySnippet";

	private BundleFileSystemService _fileSystemService;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();

		// setup test bundle
		this._fileSystemService = new BundleFileSystemService(this.createFileSystem());
		this._fileSystemService.createBundleDirectory();

		// turn on monitoring
		BundleMonitor.getInstance().beginMonitoring();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.scripting.model.BundleTestBase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		try
		{
			BundleMonitor.getInstance().endMonitoring();
			this._fileSystemService.cleanUp();
		}
		finally
		{
			super.tearDown();
		}
	}

	/**
	 * createBundleDirectoryManager
	 * 
	 * @return
	 */
	protected abstract IBundleFileSystem createFileSystem();

	/**
	 * addBundleFile
	 * 
	 * @throws Exception
	 */
	protected void addBundleFile() throws Exception
	{
		String content = "";

		this._fileSystemService.createBundleFile(content);
	}

	/**
	 * addCommand
	 * 
	 * @throws Exception
	 */
	protected void addCommand() throws Exception
	{
		String content = "";

		this._fileSystemService.createCommand(content);
	}

	/**
	 * addSnippet
	 * 
	 * @throws Exception
	 */
	protected void addSnippet() throws Exception
	{
		String content = "";

		this._fileSystemService.createSnippet(content);
	}

	/**
	 * removeBundleFile
	 * 
	 * @throws Exception
	 */
	protected void removeBundleFile() throws Exception
	{
		this._fileSystemService.deleteBundleFile();
	}

	/**
	 * removeCommand
	 * 
	 * @throws Exception
	 */
	protected void removeCommand() throws Exception
	{
		this._fileSystemService.deleteCommand();
	}

	/**
	 * removeSnippet
	 * 
	 * @throws Exception
	 */
	protected void removeSnippet() throws Exception
	{
		this._fileSystemService.deleteSnippet();
	}

	/**
	 * testAddBundleFile
	 * 
	 * @throws Exception
	 */
	public void testAddBundleFile() throws Exception
	{
		this.addBundleFile();
	}

	/**
	 * testAddCommandAfterBundleFile
	 * 
	 * @throws Exception
	 */
	public void testAddCommandAfterBundleFile() throws Exception
	{
		this.addBundleFile();
		this.addCommand();
	}

	/**
	 * testAddCommandBeforeBundleFile
	 * 
	 * @throws Exception
	 */
	public void testAddCommandBeforeBundleFile() throws Exception
	{
		this.addCommand();
		this.addBundleFile();
	}

	/**
	 * testAddSnippetAfterBundleFile
	 * 
	 * @throws Exception
	 */
	public void testAddSnippetAfterBundleFile() throws Exception
	{
		this.addBundleFile();
		this.addSnippet();
	}

	/**
	 * testAddSnippetBeforeBundleFile
	 * 
	 * @throws Exception
	 */
	public void testAddSnippetNoBundleFile() throws Exception
	{
		this.addSnippet();
		this.addBundleFile();
	}

	/**
	 * testRemoveCommand
	 * 
	 * @throws Exception
	 */
	public void testRemoveCommand() throws Exception
	{
		this.addBundleFile();
		this.addCommand();

		this.removeCommand();
	}

	/**
	 * testRemoveCommandNoBundleFile
	 * 
	 * @throws Exception
	 */
	public void testRemoveCommandNoBundleFile() throws Exception
	{
		this.addCommand();
		this.removeCommand();
	}

	/**
	 * testRemoveSnippet
	 * 
	 * @throws Exception
	 */
	public void testRemoveSnippet() throws Exception
	{
		this.addBundleFile();
		this.addSnippet();

		this.removeSnippet();
	}

	/**
	 * testRemoveSnippetNoBundleFile
	 * 
	 * @throws Exception
	 */
	public void testRemoveSnippetNoBundleFile() throws Exception
	{
		this.addSnippet();
		this.removeSnippet();
	}

	/**
	 * testRemoveBundleFile
	 * 
	 * @throws Exception
	 */
	public void testRemoveBundleFileWithoutMembers() throws Exception
	{
		this.addBundleFile();
		this.removeBundleFile();
	}

	/**
	 * testRemoveBundleFileBeforeMembers
	 * 
	 * @throws Exception
	 */
	public void testRemoveBundleFileBeforeMembers() throws Exception
	{
		this.addBundleFile();
		this.addCommand();
		this.addSnippet();

		this.removeBundleFile();
	}

	/**
	 * testRemoveBundleFileAfterMembers
	 * 
	 * @throws Exception
	 */
	public void testRemoveBundleFileAfterMembers() throws Exception
	{
		this.addBundleFile();
		this.addCommand();
		this.addSnippet();

		this.removeCommand();
		this.removeSnippet();
		this.removeBundleFile();
	}

	/**
	 * testRemoveBundleDirectory
	 * 
	 * @throws Exception
	 */
	public void testRemoveBundleDirectory() throws Exception
	{
		this.addBundleFile();
		this.addCommand();
		this.addSnippet();

		this._fileSystemService.deleteBundleDirectory();
	}

	/**
	 * testRemoveCommandsDirectory
	 * 
	 * @throws Exception
	 */
	public void testRemoveCommandsDirectory() throws Exception
	{
		this.addBundleFile();
		this.addCommand();

		this._fileSystemService.deleteCommandsDirectory();
	}

	/**
	 * testRemoveSnippetsDirectory
	 * 
	 * @throws Exception
	 */
	public void testRemoveSnippetsDirectory() throws Exception
	{
		this.addBundleFile();
		this.addSnippet();

		this._fileSystemService.deleteSnippetsDirectory();
	}

	/**
	 * testRenameCommand
	 * 
	 * @throws Exception
	 */
	public void testRenameCommand() throws Exception
	{
		this.addBundleFile();
		this.addCommand();

		this._fileSystemService.moveCommand(COMMAND_NAME + "2.rb");
	}

	/**
	 * testRenameSnippet
	 * 
	 * @throws Exception
	 */
	public void testRenameSnippet() throws Exception
	{
		this.addBundleFile();
		this.addSnippet();

		this._fileSystemService.moveSnippet(SNIPPET_NAME + "2.rb");
	}

	/**
	 * testRenameBundleFile
	 * 
	 * @throws Exception
	 */
	public void testRenameBundleFile() throws Exception
	{
		this.addBundleFile();
		this.addCommand();
		this.addSnippet();

		this._fileSystemService.moveBundleFile("someName.rb");
	}

	/**
	 * testRenameBundleDirectory
	 * 
	 * @throws Exception
	 */
	public void testRenameBundleDirectory() throws Exception
	{
		this.addBundleFile();
		this.addCommand();
		this.addSnippet();

		this._fileSystemService.moveBundleDirectory(BUNDLE_NAME + "2");
	}
}
