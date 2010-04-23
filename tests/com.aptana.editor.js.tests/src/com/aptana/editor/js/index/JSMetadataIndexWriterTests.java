package com.aptana.editor.js.index;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

import com.aptana.editor.js.Activator;
import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSMetadataIndexWriterTests extends TestCase
{
	/**
	 * getIndex
	 * 
	 * @return
	 */
	private Index getIndex()
	{
		IndexManager manager = IndexManager.getInstance();

		return manager.getIndex(IndexConstants.METADATA);
	}

	/**
	 * getType
	 * 
	 * @param typeName
	 * @return
	 */
	private TypeElement getType(String typeName)
	{
		JSMetadataIndexReader reader = new JSMetadataIndexReader();
		
		return reader.readType(this.getIndex(), typeName);
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
	 * @throws ScriptDocException
	 */
	private void writeMetadataResource(String resource) throws ScriptDocException
	{
		InputStream stream = this.loadResource(resource);
		assertNotNull(stream);
		
		JSMetadataIndexWriter writer = new JSMetadataIndexWriter();
		writer.loadXML(stream);
		
		writer.writeToIndex(this.getIndex());
	}

	/**
	 * testType
	 * 
	 * @throws ScriptDocException
	 */
	public void testType() throws ScriptDocException
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
	public void testMethod() throws ScriptDocException
	{
		String typeName = "MyClass";
		String methodName = "myMethod";

		// transfer XML to index and grab our class of interest
		this.writeMetadataResource("/metadata/typeWithMethod.xml");
		TypeElement retrievedType = this.getType(typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
		
		// make sure we have one property
		PropertyElement[] properties = retrievedType.getProperties();
		assertNotNull(properties);
		assertTrue(properties.length == 1);
		
		// make sure it is a function
		PropertyElement property = properties[0];
		assertTrue(property instanceof FunctionElement);
		
		// make sure it is the function we added earlier
		FunctionElement retrievedMethod = (FunctionElement) property;
		assertEquals(methodName, retrievedMethod.getName());
	}
}
