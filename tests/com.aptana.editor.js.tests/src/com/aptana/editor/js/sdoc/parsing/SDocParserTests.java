/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.sdoc.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import beaver.Symbol;

import com.aptana.core.util.IOUtil;
import com.aptana.core.util.ResourceUtil;
import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.sdoc.model.DocumentationBlock;

public class SDocParserTests extends TestCase
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
			URL url = FileLocator.find(JSPlugin.getDefault().getBundle(), path, null);
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
	
	protected void parseTest(String resource)
	{
		File file = this.getFile(new Path(resource));
		String source = this.getContent(file);
		SDocParser parser = new SDocParser();
		
		try
		{
			Object result = parser.parse(source);
			
			if (result instanceof Symbol)
			{
				Object value = ((Symbol) result).value;
				
				if (value instanceof DocumentationBlock)
				{
					//System.out.println(((Block) value).toSource());
				}
			}
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	/**
	 * testAdvanced
	 */
	public void testAdvanced()
	{
		this.parseTest("sdoc/advanced.sdoc");
	}
	
	/**
	 * testAlias
	 */
	public void testAlias()
	{
		this.parseTest("sdoc/alias.sdoc");
	}
	
	/**
	 * testArrays
	 */
	public void testArrays()
	{
		this.parseTest("sdoc/arrays.sdoc");
	}
	
	/**
	 * testClassDescription
	 */
	public void testClassDescription()
	{
		this.parseTest("sdoc/classDescription.sdoc");
	}
	
	/**
	 * testConstructor
	 */
	public void testConstructor()
	{
		this.parseTest("sdoc/constructor.sdoc");
	}
	
	/**
	 * testExample
	 */
	public void testExample()
	{
		this.parseTest("sdoc/example.sdoc");
	}
	
	/**
	 * testException
	 */
	public void testException()
	{
		this.parseTest("sdoc/exception.sdoc");
	}
	
	/**
	 * testFunctions
	 */
	public void testFunctions()
	{
		this.parseTest("sdoc/functions.sdoc");
	}
	
	/**
	 * testFunctions2
	 */
	public void testFunctions2()
	{
		this.parseTest("sdoc/functions2.sdoc");
	}
	
	/**
	 * testInternal
	 */
	public void testInternal()
	{
		this.parseTest("sdoc/internal.sdoc");
	}
	
	/**
	 * testMethod
	 */
	public void testMethod()
	{
		this.parseTest("sdoc/method.sdoc");
	}
	
	/**
	 * testNamespace
	 */
	public void testNamespace()
	{
		this.parseTest("sdoc/namespace.sdoc");
	}
	
	/**
	 * testOverview
	 */
	public void testOverview()
	{
		this.parseTest("sdoc/overview.sdoc");
	}
	
	/**
	 * testParam
	 */
	public void testParam()
	{
		this.parseTest("sdoc/param.sdoc");
	}
	
	/**
	 * testPrivate
	 */
	public void testPrivate()
	{
		this.parseTest("sdoc/private.sdoc");
	}
	
	/**
	 * testProperty
	 */
	public void testProperty()
	{
		this.parseTest("sdoc/property.sdoc");
	}
	
	/**
	 * testReturn
	 */
	public void testReturn()
	{
		this.parseTest("sdoc/return.sdoc");
	}
	
	/**
	 * testSee
	 */
	public void testSee()
	{
		this.parseTest("sdoc/see.sdoc");
	}
	
	/**
	 * testText
	 */
	public void testText()
	{
		this.parseTest("sdoc/text.sdoc");
	}
	
	/**
	 * testTextAndAlias
	 */
	public void testTextAndAlias()
	{
		this.parseTest("sdoc/textAndAlias.sdoc");
	}
	
	/**
	 * testTextAndAuthor
	 */
	public void testTextAndAuthor()
	{
		this.parseTest("sdoc/textAndAuthor.sdoc");
	}
	
	/**
	 * testTextAndParam
	 */
	public void testTextAndParam()
	{
		this.parseTest("sdoc/textAndParam.sdoc");
	}
	
	/**
	 * testTextAndAliasAndTags
	 */
	public void testTextAndAliasAndTags()
	{
		this.parseTest("sdoc/textAndAliasAndTags.sdoc");
	}
	
	/**
	 * testTextAndAliasAndTagsAndReturn
	 */
	public void testTextAndAliasAndTagsAndReturn()
	{
		this.parseTest("sdoc/textAndAliasAndTagsAndReturn.sdoc");
	}
	
	/**
	 * testTextAndPrivateAndAliasAndTagsAndReturn
	 */
	public void testTextAndPrivateAndAliasAndTagsAndReturn()
	{
		this.parseTest("sdoc/textAndPrivateAndAliasAndTagsAndReturn.sdoc");
	}
	
	/**
	 * testUnknownTag
	 */
	public void testUnknownTag()
	{
		this.parseTest("sdoc/unknown.sdoc");
	}
}
