/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.sax;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

import com.aptana.parsing.ParsingPlugin;

/**
 * XMLSchemaTests
 */
public class XMLSchemaTests
{
	/**
	 * getBundle
	 * 
	 * @return
	 */
	protected Bundle getBundle()
	{
		return ParsingPlugin.getDefault().getBundle();
	}

	/**
	 * getFileStore
	 * 
	 * @param resource
	 * @return
	 */
	protected InputStream getInputStream(String resource)
	{
		Path path = new Path(resource);
		InputStream result = null;

		try
		{
			URL url = FileLocator.find(this.getBundle(), path, null);

			result = url.openStream();
		}
		catch (IOException e)
		{
			fail(e.getMessage());
		}

		assertNotNull(result);

		return result;
	}

	/**
	 * createReader
	 * 
	 * @param schemaResource
	 * @return
	 * @throws SchemaInitializationException
	 */
	protected ValidatingReader createReader(String schemaResource) throws SchemaInitializationException
	{
		InputStream schemaStream = this.getInputStream(schemaResource);
		ValidatingReader reader = new ValidatingReader();

		reader._schema = SchemaBuilder.fromXML(schemaStream);

		return reader;
	}

	/**
	 * loadTest
	 * 
	 * @param schemaResource
	 * @param xmlResource
	 */
	protected void loadTest(String schemaResource, String xmlResource) throws Exception
	{
		InputStream xmlStream = null;

		try
		{
			// create validator from the specified schema
			ValidatingReader reader = this.createReader(schemaResource);

			// validate specify xml resource
			xmlStream = this.getInputStream(xmlResource);
			reader.read(xmlStream);
		}
		finally
		{
			if (xmlStream != null)
			{
				try
				{
					xmlStream.close();
				}
				catch (IOException e)
				{
				}
			}
		}
	}

	/**
	 * testFollowSchema
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testFollowSchema() throws Exception
	{
		loadTest("/xml-schema/StrictSchema.xml", "/xml-schema/follow-schema.xml");
	}

	/**
	 * testViolateSchema
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testViolateSchema()
	{
		try
		{
			loadTest("/xml-schema/StrictSchema.xml", "/xml-schema/freeform.xml");
			fail("freeform.xml should fail against a strict schema");
		}
		catch (Exception e)
		{
			// success
		}
	}

	/**
	 * testFreeFormSchema
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testFreeFormSchema() throws Exception
	{
		loadTest("/xml-schema/FreeFormNodeSchema.xml", "/xml-schema/freeform.xml");
	}

	/**
	 * testInvalidFreeform
	 * 
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@Test
	public void testInvalidFreeform()
	{
		try
		{
			loadTest("/xml-schema/FreeFormNodeSchema.xml", "/xml-schema/invalid-freeform.xml");
			fail("invalid-freeform.xml should fail against a freeform schema");
		}
		catch (Exception e)
		{
			// success
		}
	}
}
