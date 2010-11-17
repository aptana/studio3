/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.EnumSet;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.contentassist.JSIndexQueryHelper;
import com.aptana.editor.js.contentassist.index.JSIndexConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.index.JSIndexWriter;
import com.aptana.editor.js.contentassist.index.JSMetadataReader;
import com.aptana.editor.js.contentassist.index.ScriptDocException;
import com.aptana.editor.js.contentassist.model.ContentSelector;
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
		
		return reader.getType(this.getIndex(), typeName, EnumSet.allOf(ContentSelector.class));
	}
	
	/**
	 * loadResource
	 * 
	 * @param resource
	 * @return
	 */
	private InputStream loadResource(String resource)
	{
		URL url = FileLocator.find(Activator.getDefault().getBundle(), new Path(resource), null);
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
