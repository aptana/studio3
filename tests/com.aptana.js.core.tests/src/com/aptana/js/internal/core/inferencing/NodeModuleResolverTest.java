/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.inferencing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.preferences.IPreferenceConstants;

@SuppressWarnings("nls")
public class NodeModuleResolverTest
{

	@Rule
	public TestName name = new TestName();
	private File baseDir;
	private IPath dir;
	private NodeModuleResolver resolver;

	@Before
	public void setUp() throws Exception
	{
		// super.setUp();

		// Create a tmp dir to hold the structure we'll be traversing
		IPath tmp = FileUtil.getTempDirectory();
		IPath baseDirPath = tmp.append(name.getMethodName() + System.currentTimeMillis());
		baseDir = baseDirPath.toFile();
		dir = baseDirPath.append("grand_parent").append("parent").append("child");
		assertTrue(dir.toFile().mkdirs());

		// Hook up the resolver to it
		resolver = new NodeModuleResolver();
	}

	@After
	public void tearDown() throws Exception
	{
		try
		{
			FileUtil.deleteRecursively(baseDir);
		}
		finally
		{
			// super.tearDown();
		}
	}

	@Test
	public void testResolveCoreModule() throws Exception
	{
		IPath nodeSrc = FileUtil.getTempDirectory().append("node_src");
		IPath lib = nodeSrc.append("lib");
		lib.toFile().mkdirs();

		try
		{
			DefaultScope.INSTANCE.getNode(JSCorePlugin.PLUGIN_ID).put(IPreferenceConstants.NODEJS_SOURCE_PATH,
					nodeSrc.toOSString());

			IPath expected = lib.append("http.js");
			expected.toFile().createNewFile();

			assertEquals(expected, resolver.resolve("http", null, dir, null));
		}
		finally
		{
			FileUtil.deleteRecursively(lib.toFile());
		}
	}

	@Test
	public void testResolveRelativeJSFile() throws Exception
	{
		IPath expected = dir.append("sibling.js");
		expected.toFile().createNewFile();

		assertEquals(expected, resolver.resolve("./sibling", null, dir, null));
		assertEquals(expected, resolver.resolve("/sibling", null, dir, null));
	}

	@Test
	public void testResolveRelativeNodeFile() throws Exception
	{
		IPath expected = dir.append("sibling.node");
		expected.toFile().createNewFile();

		assertEquals(expected, resolver.resolve("./sibling", null, dir, null));
		assertEquals(expected, resolver.resolve("/sibling", null, dir, null));
	}

	@Test
	public void testResolveRelativeDirectory() throws Exception
	{
		IPath expected = createNodeDirectory(dir, "sibling", "main.js");

		assertEquals(expected, resolver.resolve("./sibling", null, dir, null));
		assertEquals(expected, resolver.resolve("/sibling", null, dir, null));
	}

	@Test
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
		assertEquals(expected, resolver.resolve("file", null, dir, null));
		// Finds file in second "node_modules" (one directory level higher)
		assertEquals(file2, resolver.resolve("file2", null, dir, null));
	}

	@Test
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
		assertEquals(expected, resolver.resolve("file", null, dir, null));
		// Finds file in second "node_modules" (one directory level higher)
		assertEquals(file2, resolver.resolve("file2", null, dir, null));
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
