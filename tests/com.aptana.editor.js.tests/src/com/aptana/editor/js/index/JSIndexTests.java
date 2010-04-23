package com.aptana.editor.js.index;

import junit.framework.TestCase;

import com.aptana.editor.js.model.FunctionElement;
import com.aptana.editor.js.model.PropertyElement;
import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexTests extends TestCase
{
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		IndexManager.getInstance().removeIndex(IndexConstants.METADATA);
		
		super.tearDown();
	}

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
	 * writeType
	 * 
	 * @param type
	 */
	private void writeType(TypeElement type)
	{
		JSMetadataIndexWriter writer = new JSMetadataIndexWriter();
		
		writer.writeType(this.getIndex(), type);
	}
	
	/**
	 * testType
	 */
	public void testType()
	{
		String typeName = "MyClass";
		
		TypeElement type = new TypeElement();
		type.setName(typeName);
		this.writeType(type);
		
		TypeElement retrievedType = this.getType(typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
	}
	
	/**
	 * testMethod
	 */
	public void testMethod()
	{
		String typeName = "MyClass";
		String methodName = "myMethod";
		
		// create type
		TypeElement type = new TypeElement();
		type.setName(typeName);
		
		// create method within type
		FunctionElement method = new FunctionElement();
		method.setName(methodName);
		type.addProperty(method);
		
		// write type to index
		this.writeType(type);

		// then retrieve it
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
