/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.aptana.core.util.FileUtil;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.index.core.FileStoreBuildContext;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;
import com.aptana.index.core.IndexPlugin;
import com.aptana.index.core.build.BuildContext;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.JSTypeConstants;
import com.aptana.js.core.index.JSFileIndexingParticipant;
import com.aptana.js.core.index.JSIndexQueryHelper;
import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;
import com.aptana.js.core.parsing.GraalJSParser;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.core.parsing.ast.JSParseRootNode;
import com.aptana.js.internal.core.index.JSIndexReader;
import com.aptana.js.internal.core.index.JSMetadataLoader;
import com.aptana.parsing.IParser;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;

public abstract class InferencingTestsBase
{
	@Rule
	public TestName name = new TestName();

	public class Indexer extends JSFileIndexingParticipant
	{
		public void indexTree(IFileStore file, String source, Index index, JSParseRootNode root)
		{
			BuildContext context = new FileStoreBuildContext(file);
			processParseResults(context, index, root, new NullProgressMonitor());
		}
	}

	private JSIndexReader reader;
	private File dir;

	/**
	 * getContent
	 * 
	 * @param file
	 * @return
	 */
	protected String getContent(IFileStore file)
	{
		String result = StringUtil.EMPTY;

		try
		{
			InputStream input = file.openInputStream(EFS.NONE, new NullProgressMonitor());

			result = IOUtil.read(input);
		}
		catch (CoreException e)
		{
			fail(e.getMessage());
		}

		return result;
	}

	/**
	 * Loads JS Metadata synchronously.
	 * 
	 * @throws InterruptedException
	 */
	protected void loadJSMetadata() throws InterruptedException
	{
		JSMetadataLoader loader = new JSMetadataLoader();
		loader.schedule();
		loader.join();
	}

	/**
	 * JSScope
	 * 
	 * @param ast
	 * @return
	 */
	protected JSScope getGlobals(JSParseRootNode ast)
	{
		return ast.getGlobals();
	}

	/**
	 * getGlobals
	 * 
	 * @param source
	 * @return
	 */
	protected JSScope getGlobals(String source)
	{
		IParseNode root = getParseRootNode(source);
		assertTrue(root instanceof JSParseRootNode);

		return getGlobals((JSParseRootNode) root);
	}

	/**
	 * getIndex
	 * 
	 * @return
	 */
	protected Index getIndex()
	{
		URI indexURI = getIndexURI();
		Index result = null;

		if (indexURI != null)
		{
			result = getIndexManager().getIndex(indexURI);
		}

		return result;
	}

	/**
	 * getIndexURI
	 * 
	 * @return
	 */
	protected URI getIndexURI()
	{
		IPath dir = FileUtil.getTempDirectory().append(name.getMethodName());
		dir.toFile().mkdirs();
		dir.toFile().deleteOnExit();
		return dir.toFile().toURI();
	}

	/**
	 * getLastStatementTypes
	 * 
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	protected List<String> getLastStatementTypes(String rawSource)
	{
		// Create a temp file with the contents and then grab filestore pointing at it?
		try
		{
			File file = File.createTempFile("infer", ".js");
			file.deleteOnExit();
			IFileStore store = EFS.getLocalFileSystem().fromLocalFile(file);
			IOUtil.write(store.openOutputStream(EFS.NONE, new NullProgressMonitor()), rawSource);
			return getLastStatementTypes(store);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		return Collections.emptyList();
	}

	/**
	 * getLastStatementTypes
	 * 
	 * @param path
	 * @return
	 * @throws CoreException
	 */
	protected List<String> getLastStatementTypes(IPath path)
	{
		return getLastStatementTypes(getFileStore(path));
	}

	/**
	 * getLastStatementTypes
	 * 
	 * @param store
	 * @return
	 * @throws CoreException
	 */
	protected List<String> getLastStatementTypes(IFileStore store)
	{
		String source = getContent(store);

		IParseNode root = getParseRootNode(source);
		assertNotNull(root);
		assertTrue(root instanceof JSParseRootNode);

		IParseNode statement = root.getLastChild();
		assertNotNull(statement);
		assertTrue(statement instanceof JSNode);

		JSScope globals = getGlobals((JSParseRootNode) root);
		assertNotNull(globals);

		Indexer indexer = new Indexer();

		indexer.indexTree(store, source, getIndex(), (JSParseRootNode) root);

		return getTypes(globals, (JSNode) statement);
	}

	protected IFileStore getFileStore(IPath path)
	{
		IFileStore store = null;
		try
		{
			URL url = FileLocator.find(Platform.getBundle(JSCorePlugin.PLUGIN_ID), path, null);
			url = FileLocator.toFileURL(url);
			URI fileURI = ResourceUtil.toURI(url);
			store = EFS.getStore(fileURI);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		assertNotNull(store);
		// assertTrue(store.exists());

		return store;
	}

	/**
	 * getURI
	 * 
	 * @return
	 */
	protected synchronized URI getLocation()
	{
		if (dir == null)
		{
			IPath tmpDir = FileUtil.getTempDirectory().append(name.getMethodName() + System.currentTimeMillis());
			dir = tmpDir.toFile();
			assertTrue(dir.mkdirs());
		}
		return new File(dir, "inference_file.js").toURI();
	}

	/**
	 * getParseRootNode
	 * 
	 * @param source
	 * @return
	 */
	protected IParseNode getParseRootNode(String source)
	{
		IParser parser = new GraalJSParser();
		ParseState parseState = new ParseState(source);

		try
		{
			return parser.parse(parseState).getRootNode();
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		throw new AssertionError("Should never get here.");
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	protected List<TypeElement> getType(String typeName)
	{
		return reader.getType(getIndex(), typeName, true);
	}

	/**
	 * getTypes
	 * 
	 * @param node
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, JSNode node)
	{
		JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, getIndex(), getLocation(),
				new JSIndexQueryHelper(getIndex()));

		node.accept(walker);

		return walker.getTypes();
	}

	/**
	 * getTypes
	 * 
	 * @param nodes
	 * @return
	 */
	protected List<String> getTypes(JSScope globals, List<JSNode> nodes)
	{
		List<String> result = new LinkedList<String>();

		for (IParseNode node : nodes)
		{
			JSNodeTypeInferrer walker = new JSNodeTypeInferrer(globals, getIndex(), getLocation(),
					new JSIndexQueryHelper(getIndex()));

			assertTrue(node instanceof JSNode);

			((JSNode) node).accept(walker);

			result.addAll(walker.getTypes());
		}

		return result;
	}

	/**
	 * invocationTypeTests
	 * 
	 * @param source
	 * @param types
	 */
	public void lastStatementTypeTests(String rawSource, String... types)
	{
		List<String> statementTypes = getLastStatementTypes(rawSource);
		assertStatementTypes(statementTypes, types);
	}

	public void lastStatementTypeTests(IPath path, String... types)
	{
		List<String> statementTypes = getLastStatementTypes(path);
		assertStatementTypes(statementTypes, types);
	}

	protected void assertStatementTypes(List<String> statementTypes, String... types)
	{
		assertNotNull(statementTypes);

		assertEquals("Number of types doesn't match", types.length, statementTypes.size());

		for (String type : types)
		{
			String message = MessageFormat.format("Unable to locate {0} in {1}", type, statementTypes);

			if (JSTypeConstants.DYNAMIC_CLASS_PREFIX.equals(type))
			{
				boolean match = false;

				for (String returnedType : statementTypes)
				{
					if (returnedType.startsWith(JSTypeConstants.DYNAMIC_CLASS_PREFIX))
					{
						match = true;
						break;
					}
				}

				if (match == false)
				{
					fail(message);
				}
			}
			else
			{
				assertTrue(message, statementTypes.contains(type));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	// @Override
	@Before
	public void setUp() throws Exception
	{
		// super.setUp();

		reader = new JSIndexReader();
		loadJSMetadata();
		// EditorTestHelper.joinBackgroundActivities();
	}

	/**
	 * structureTests
	 * 
	 * @param typeName
	 * @param propertyNames
	 */
	protected void structureTests(String typeName, String... propertyNames)
	{
		assertNotNull(typeName);

		structureTests(getType(typeName), propertyNames);
	}

	/**
	 * structureTests
	 * 
	 * @param types
	 * @param propertyNames
	 */
	protected void structureTests(List<TypeElement> types, String... propertyNames)
	{
		List<String> parentTypes = new ArrayList<String>();

		for (TypeElement type : types)
		{
			parentTypes.addAll(type.getParentTypes());
		}
		assertEquals(1, parentTypes.size());
		assertEquals(JSTypeConstants.OBJECT_TYPE, parentTypes.get(0));

		TypeElement type = types.get(0);

		for (String propertyName : propertyNames)
		{
			PropertyElement property = type.getProperty(propertyName);
			assertNotNull(propertyName + " does not exist", property);

			List<String> propertyTypeNames = property.getTypeNames();
			assertNotNull(propertyName + " does not have a type", propertyTypeNames);

			assertEquals(1, propertyTypeNames.size());
			assertEquals(JSTypeConstants.BOOLEAN_TYPE, propertyTypeNames.get(0));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	// @Override
	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (dir != null)
			{
				FileUtil.deleteRecursively(dir);
				dir = null;
			}

			URI indexURI = getIndexURI();
			if (indexURI != null)
			{
				getIndexManager().removeIndex(indexURI);
			}
		}
		finally
		{
			reader = null;

			// super.tearDown();
		}
	}

	protected IndexManager getIndexManager()
	{
		return IndexPlugin.getDefault().getIndexManager();
	}

	/**
	 * varTypeTests
	 * 
	 * @param source
	 * @param symbol
	 * @param types
	 */
	public void varTypeTests(String source, String symbol, String... types)
	{
		JSScope globals = getGlobals(source);

		assertTrue(globals.hasLocalSymbol(symbol));
		JSPropertyCollection object = globals.getSymbol(symbol);
		assertNotNull(object);
		List<JSNode> values = object.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());

		List<String> symbolTypes = getTypes(globals, values);
		assertNotNull(types);
		assertEquals(types.length, symbolTypes.size());

		for (String type : types)
		{
			assertTrue(MessageFormat.format("Expected to find type ''{0}'' in list: {1}", type, symbolTypes),
					symbolTypes.contains(type));
		}
	}
}
