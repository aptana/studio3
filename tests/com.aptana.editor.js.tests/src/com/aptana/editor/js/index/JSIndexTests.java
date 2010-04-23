package com.aptana.editor.js.index;

import junit.framework.TestCase;

import com.aptana.editor.js.model.TypeElement;
import com.aptana.index.core.Index;
import com.aptana.index.core.IndexManager;

public class JSIndexTests extends TestCase
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
	 * testType
	 */
	public void testType()
	{
		Index index = this.getIndex();
		String typeName = "MyClass";
		
		TypeElement type = new TypeElement();
		type.setName(typeName);
		
		JSMetadataIndexWriter writer = new JSMetadataIndexWriter();
		writer.writeType(index, type);
		
		JSMetadataIndexReader reader = new JSMetadataIndexReader();
		TypeElement retrievedType = reader.readType(index, typeName);
		
		assertNotNull(retrievedType);
		assertEquals(typeName, retrievedType.getName());
	}
}
