/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.JSPlugin;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.index.JSMetadataReader;
import com.aptana.editor.js.contentassist.index.ScriptDocException;
import com.aptana.editor.js.contentassist.model.FunctionElement;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSMetadataIndexWriterTests extends TestCase
{
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		IndexManager.getInstance().removeIndex(URI.create(JSIndexConstants.METADATA_INDEX_LOCATION));
		
		super.tearDown();
	}
	
	/**
	 * getIndex
	 * 
	 * @return
	 */
	private Index getIndex()
	{
		return JSIndexQueryHelper.getIndex();
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	private TypeElement getType(String typeName)
	{
		JSIndexReader reader = new JSIndexReader();
		
		return reader.getType(this.getIndex(), typeName, true);
	}
	
	/**
	 * loadResource
	 * 
	 * @param resource
	 * @return
	 */
	private InputStream loadResource(String resource)
	{
		URL url = FileLocator.find(JSPlugin.getDefault().getBundle(), new Path(resource), null);
		InputStream stream = null;

		if (url != null)
		{
			try
			{
				stream = url.openStream();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return stream;
	}
	
	/**
	 * writeMetadataResource
	 * 
	 * @param resource
	 * @throws Exception 
	 */
	private void writeMetadataResource(String resource) throws Exception
	{
		InputStream stream = this.loadResource(resource);
		assertNotNull(stream);
		
		JSMetadataReader reader = new JSMetadataReader();
		reader.loadXML(stream);
		JSIndexWriter writer = new JSIndexWriter();
		Index index = this.getIndex();
		
		for (TypeElement type : reader.getTypes())
		{
			writer.writeType(index, type);
		}
	}

	/**
	 * testType
	 * 
	 * @throws ScriptDocException
	 */
	public void testType() throws Exception
	{
		String typeName = "MyClass";

		// transfer XML to index and grab our class of interest
		this.writeMetadataResource("/metadata/typeOnly.xml");
		TypeElement retrievedType = this.getType(typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
	}
	
	/**
	 * testMethod
	 * 
	 * @throws ScriptDocException
	 */
	public void testMethod() throws Exception
	{
		String typeName = "MyClass";
		String methodName = "myMethod";

		// transfer XML to index and grab our class of interest
		this.writeMetadataResource("/metadata/typeWithMethod.xml");
		TypeElement retrievedType = this.getType(typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
		
		// make sure we have one property
		List<PropertyElement> properties = retrievedType.getProperties();
		assertNotNull(properties);
		assertTrue(properties.size() == 1);
		
		// make sure it is a function
		PropertyElement property = properties.get(0);
		assertTrue(property instanceof FunctionElement);
		
		// make sure it is the function we added earlier
		FunctionElement retrievedMethod = (FunctionElement) property;
		assertEquals(methodName, retrievedMethod.getName());
	}
	
	/**
	 * testProperty
	 * 
	 * @throws ScriptDocException
	 */
	public void testProperty() throws Exception
	{
		String typeName = "MyClass";
		String propertyName = "myProperty";
		
		// transfer XML to index and grab our class of interest
		this.writeMetadataResource("/metadata/typeWithProperty.xml");
		TypeElement retrievedType = this.getType(typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
		
		// make sure we have one property
		List<PropertyElement> properties = retrievedType.getProperties();
		assertNotNull(properties);
		assertTrue(properties.size() == 1);
		
		// make sure it is a function
		PropertyElement property = properties.get(0);
		assertEquals(propertyName, property.getName());
	}
	
	/**
	 * testTypeDescription
	 * 
	 * @throws ScriptDocException
	 */
	public void testTypeDescription() throws Exception
	{
		String typeName = "MyClass";
		
		// transfer XML to index and grab our class of interest
		this.writeMetadataResource("/metadata/typeWithDescription.xml");
		TypeElement retrievedType = this.getType(typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
		
		// make sure we have one property
		String description = retrievedType.getDescription();
		assertEquals("This is a description of MyClass", description);
	}
}
