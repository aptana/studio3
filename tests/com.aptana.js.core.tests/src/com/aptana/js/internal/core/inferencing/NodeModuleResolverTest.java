/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;

@SuppressWarnings("nls")
public class NodeModuleResolverTest extends TestCase
{

	private File baseDir;
	private IPath dir;
	private NodeModuleResolver resolver;

	protected void setUp() throws Exception
	{
		super.setUp();

		// Create a tmp dir to hold the structure we'll be traversing
		IPath tmp = FileUtil.getTempDirectory();
		IPath baseDirPath = tmp.append(getName() + System.currentTimeMillis());
		baseDir = baseDirPath.toFile();
		dir = baseDirPath.append("grand_parent").append("parent").append("child");
		assertTrue(dir.toFile().mkdirs());

		// Hook up the resolver to it
		resolver = new NodeModuleResolver(dir);
	}

	protected void tearDown() throws Exception
	{
		try
		{
			FileUtil.deleteRecursively(baseDir);
		}
		finally
		{
			super.tearDown();
		}
	}

	public void testResolveCoreModule()
	{
		fail("Not yet implemented - Need ability to set path to node source");
	}

	public void testResolveRelativeJSFile() throws Exception
	{
		IPath expected = dir.append("sibling.js");
		expected.toFile().createNewFile();

		assertEquals(expected, resolver.resolve("./sibling"));
		assertEquals(expected, resolver.resolve("/sibling"));
	}

	public void testResolveRelativeNodeFile() throws Exception
	{
		IPath expected = dir.append("sibling.node");
		expected.toFile().createNewFile();

		assertEquals(expected, resolver.resolve("./sibling"));
		assertEquals(expected, resolver.resolve("/sibling"));
	}

	public void testResolveRelativeDirectory() throws Exception
	{
		IPath expected = createNodeDirectory(dir, "sibling", "main.js");

		assertEquals(expected, resolver.resolve("./sibling"));
		assertEquals(expected, resolver.resolve("/sibling"));
	}

	public void testResolveJSFileUnderNodeModules() throws Exception
	{
		// "node_modules" directory which is sibling to parent
		IPath nodeModules = dir.removeLastSegments(1).append("node_modules");
		nodeModules.toFile().mkdirs();

		IPath expected = nodeModules.append("file.js");
		expected.toFile().createNewFile();

		// And create same hierarchy one level higher, shouldn't be what we match
		IPath superNodeModules = dir.removeLastSegments(2).append("node_modules");
		superNodeModules.toFile().mkdirs();

		IPath notExpected = superNodeModules.append("file.js");
		notExpected.toFile().createNewFile();

		IPath file2 = superNodeModules.append("file2.js");
		file2.toFile().createNewFile();

		// Finds file in first "node_modules"
		assertEquals(expected, resolver.resolve("file"));
		// Finds file in second "node_modules" (one directory level higher)
		assertEquals(file2, resolver.resolve("file2"));
	}

	public void testResolveDirectoryUnderNodeModules() throws Exception
	{
		// "node_modules" directory which is sibling to parent
		IPath nodeModules = dir.removeLastSegments(1).append("node_modules");
		nodeModules.toFile().mkdirs();

		IPath expected = createNodeDirectory(nodeModules, "file", "main.js");

		// And create same hierarchy one level higher, shouldn't be what we match
		IPath superNodeModules = dir.removeLastSegments(2).append("node_modules");
		superNodeModules.toFile().mkdirs();

		createNodeDirectory(superNodeModules, "file", "main.js");
		IPath file2 = createNodeDirectory(superNodeModules, "file2", "main.js");

		// Finds file in first "node_modules"
		assertEquals(expected, resolver.resolve("file"));
		// Finds file in second "node_modules" (one directory level higher)
		assertEquals(file2, resolver.resolve("file2"));
	}

	// TODO Add test for "node" file underneath node_modules dir above the current location
	// TODO Add test for file in $NODE_PATH

	private IPath createNodeDirectory(IPath parent, String dirName, String mainFilename) throws IOException
	{
		// Create subDir
		IPath fileDir = parent.append(dirName);
		assertTrue(fileDir.toFile().mkdirs());

		// Create main file
		IPath mainFile = fileDir.append(mainFilename);
		mainFile.toFile().createNewFile();

		// Create package.json pointing at main file
		IPath packageJSON = fileDir.append("package.json");
		IOUtil.write(new FileOutputStream(packageJSON.toFile()), "{\"main\": \"" + mainFilename + "\"}");

		return mainFile;
	}
}
