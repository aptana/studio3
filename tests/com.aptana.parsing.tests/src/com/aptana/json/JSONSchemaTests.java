/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.json;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.parsing.ParsingPlugin;

/**
 * JSONSchemaTests
 */
@SuppressWarnings("nls")
public class JSONSchemaTests extends TestCase
{
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
	 * getFileStore
	 * 
	 * @param path
	 * @return
	 */
	protected IFileStore getFileStore(IPath path)
	{
		IFileStore store = null;

		try
		{
			URL url = FileLocator.find(Platform.getBundle(ParsingPlugin.PLUGIN_ID), path, null);

			url = FileLocator.toFileURL(url);

			URI fileURI = ResourceUtil.toURI(url);

			store = EFS.getStore(fileURI);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		return store;
	}

	/**
	 * validate
	 * 
	 * @param name
	 * @return
	 */
	protected void validate(String name)
	{
		// create schema
		SchemaBuilder builder = new SchemaBuilder();
		Schema schema = builder.getSchema();

		// create reader using this schema
		SchemaReader sReader = new SchemaReader(schema);

		// create reader resource's source
		IPath path = Path.fromPortableString("json-schema/" + name);
		IFileStore store = getFileStore(path);
		String source = this.getContent(store);
		StringReader reader = new StringReader(source);

		// create handler and context
		SchemaHandler handler = new SchemaHandler();
		SchemaContext context = new SchemaContext();
		context.setHandler(handler);

		// validate source against schema, possibly firing events along the way
		sReader.read(reader, context);
	}

	/**
	 * testBooleanSchema
	 */
	public void testBooleanSchema()
	{
		this.validate("boolean-schema.json");
	}

	/**
	 * testNumberSchema
	 */
	public void testNumberSchema()
	{
		this.validate("number-schema.json");
	}

	/**
	 * testNullSchema
	 */
	public void testNullSchema()
	{
		this.validate("null-schema.json");
	}

	/**
	 * testStringSchema
	 */
	public void testStringSchema()
	{
		this.validate("string-schema.json");
	}

	/**
	 * testSampleSchema
	 */
	public void testSampleSchema()
	{
		this.validate("sample-schema.json");
	}
}
