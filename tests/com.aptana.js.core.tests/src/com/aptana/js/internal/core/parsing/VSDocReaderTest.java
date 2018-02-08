/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.js.core.JSCorePlugin;
import com.aptana.js.internal.core.parsing.sdoc.model.DocumentationBlock;
import com.aptana.js.internal.core.parsing.sdoc.model.ExampleTag;
import com.aptana.js.internal.core.parsing.sdoc.model.ExceptionTag;
import com.aptana.js.internal.core.parsing.sdoc.model.ParamTag;
import com.aptana.js.internal.core.parsing.sdoc.model.PrivateTag;
import com.aptana.js.internal.core.parsing.sdoc.model.ReturnTag;
import com.aptana.js.internal.core.parsing.sdoc.model.SeeTag;
import com.aptana.js.internal.core.parsing.sdoc.model.Tag;
import com.aptana.js.internal.core.parsing.sdoc.model.TagType;
import com.aptana.js.internal.core.parsing.sdoc.model.Type;
import com.aptana.js.internal.core.parsing.sdoc.model.Usage;

public class VSDocReaderTest
{
	/**
	 * getContent
	 * 
	 * @param file
	 * @return
	 */
	protected String getContent(File file)
	{
		String result = "";

		try
		{
			FileInputStream input = new FileInputStream(file);

			result = IOUtil.read(input);
		}
		catch (IOException e)
		{
		}

		return result;
	}

	/**
	 * getFile
	 * 
	 * @param path
	 * @return
	 */
	protected File getFile(IPath path)
	{
		File result = null;

		try
		{
			URL url = FileLocator.find(JSCorePlugin.getDefault().getBundle(), path, null);
			URL fileURL = FileLocator.toFileURL(url);
			URI fileURI = ResourceUtil.toURI(fileURL);

			result = new File(fileURI);
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}
		catch (URISyntaxException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(result);
		assertTrue(result.exists());

		return result;
	}

	/**
	 * getDocumentationBlock
	 * 
	 * @param resource
	 * @return
	 */
	private DocumentationBlock getDocumentationBlock(String resource)
	{
		File file = this.getFile(new Path(resource));
		String source = this.getContent(file);
		VSDocReader parser = new VSDocReader();
		InputStream input = new ByteArrayInputStream(source.getBytes());

		try
		{
			parser.loadXML(input, resource);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		return parser.getBlock();
	}

	/**
	 * testSummary
	 */
	@Test
	public void testSummary()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/summary.vsdoc");

		assertEquals("This is a summary", block.getText());
	}

	/**
	 * testPara
	 */
	@Test
	public void testPara()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/para.vsdoc");

		assertEquals("This is a summary\nWith a new line of text", block.getText());
	}

	/**
	 * testException
	 */
	@Test
	public void testException()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/exception.vsdoc");
		List<Tag> exceptions = block.getTags(EnumSet.of(TagType.EXCEPTION));

		assertNotNull(exceptions);
		assertEquals(1, exceptions.size());

		ExceptionTag tag = (ExceptionTag) exceptions.get(0);
		List<Type> types = tag.getTypes();

		assertNotNull(types);
		assertEquals(1, types.size());
		assertEquals("CustomError", types.get(0).getName());

		assertEquals("This is an exception", tag.getText());
	}

	/**
	 * testParam
	 */
	@Test
	public void testParam()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/param.vsdoc");
		List<Tag> params = block.getTags(EnumSet.of(TagType.PARAM));

		assertNotNull(params);
		assertEquals(1, params.size());

		ParamTag tag = (ParamTag) params.get(0);

		assertEquals("xyz", tag.getName());
		assertEquals(Usage.REQUIRED, tag.getUsage());

		List<Type> types = tag.getTypes();

		assertNotNull(types);
		assertEquals(1, types.size());
		assertEquals("String", types.get(0).getName());

		assertEquals("This is a parameter", tag.getText());
	}

	/**
	 * testReturns
	 */
	@Test
	public void testReturns()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/returns.vsdoc");
		List<Tag> returns = block.getTags(EnumSet.of(TagType.RETURN));

		assertNotNull(returns);
		assertEquals(1, returns.size());

		ReturnTag tag = (ReturnTag) returns.get(0);

		List<Type> types = tag.getTypes();

		assertNotNull(types);
		assertEquals(1, types.size());
		assertEquals("String", types.get(0).getName());

		assertEquals("This is a return type", tag.getText());
	}

	/**
	 * testSee
	 */
	@Test
	public void testSee()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/see.vsdoc");
		List<Tag> sees = block.getTags(EnumSet.of(TagType.SEE));

		assertNotNull(sees);
		assertEquals(1, sees.size());

		SeeTag tag = (SeeTag) sees.get(0);

		assertEquals("Number", tag.getText());
	}

	/**
	 * testPrivate
	 */
	@Test
	public void testPrivate()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/private.vsdoc");
		List<Tag> privates = block.getTags(EnumSet.of(TagType.PRIVATE));

		assertNotNull(privates);
		assertEquals(1, privates.size());

		PrivateTag tag = (PrivateTag) privates.get(0);

		assertEquals("This is private", tag.getText());
	}

	/**
	 * testExample
	 */
	@Test
	public void testExample()
	{
		DocumentationBlock block = this.getDocumentationBlock("vsdoc/example.vsdoc");
		List<Tag> returns = block.getTags(EnumSet.of(TagType.EXAMPLE));

		assertNotNull(returns);
		assertEquals(1, returns.size());

		ExampleTag tag = (ExampleTag) returns.get(0);

		assertEquals("This is an example", tag.getText());
	}
}
