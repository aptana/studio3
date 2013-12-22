/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.junit.After;
import org.junit.Before;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.core.parsing.ast.JSNode;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.parsing.ParseState;
import com.aptana.parsing.ast.IParseNode;
import com.aptana.parsing.xpath.ParseNodeXPath;

/**
 * DocNodeAttachementTestBase
 */
public abstract class DocNodeAttachementTestCase
{
	private JSParser parser;

	@Before
	public void setUp() throws Exception
	{
		parser = new JSParser();
	}

	@After
	public void tearDown() throws Exception
	{
		parser = null;
	}

	/**
	 * getContent
	 * 
	 * @param file
	 * @return
	 */
	protected String getContent(IFileStore file)
	{
		String result = "";

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

	protected IParseNode getAST(String resource)
	{
		IPath path = Path.fromPortableString(resource);
		String source = getContent(getFileStore(path));

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

	protected JSNode selectNode(String resource, String xpath)
	{
		JSNode result = null;

		IParseNode ast = getAST(resource);
		assertNotNull(ast);

		// NOTE: sysouts are here for debugging purposes
		// System.out.println(ast.getFirstChild().toString());
		// System.out.println(((JSNode) ast.getFirstChild()).toXML());
		// System.out.println();

		try
		{
			XPath nodeSelector = new ParseNodeXPath(xpath);
			Object object = nodeSelector.evaluate(ast);

			assertNotNull(object);
			assertTrue(object instanceof List<?>);

			List<?> nodes = (List<?>) object;
			assertEquals(1, nodes.size());

			Object node = nodes.get(0);
			assertTrue(node instanceof JSNode);

			result = (JSNode) node;
		}
		catch (JaxenException e)
		{
			fail(e.getMessage());
		}

		return result;
	}

	protected void assertDescription(String resource, String xpath, String description)
	{
		JSNode node = selectNode(resource, xpath);
		assertNotNull(node);

		DocumentationBlock docs = node.getDocumentation();
		assertNotNull(docs);
		assertEquals("Checking description", description, docs.getText());
	}
}
