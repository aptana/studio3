package com.aptana.editor.js.inferencing;

import java.util.EnumSet;
import java.util.List;

import com.aptana.editor.js.JSTypeConstants;
import com.aptana.editor.js.contentassist.index.JSIndexReader;
import com.aptana.editor.js.contentassist.model.ContentSelector;
import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;

public class ObjectInferencingTests extends InferencingTestsBase
{
	private static final EnumSet<ContentSelector> PARENT_TYPES_AND_PROPERTIES = EnumSet.of(ContentSelector.PARENT_TYPES, ContentSelector.PROPERTIES);
	private JSIndexReader _reader;

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		this._reader = new JSIndexReader();
	}

	/**
	 * structureTests
	 * 
	 * @param type
	 * @param propertyNames
	 */
	private void structureTests(TypeElement type, String... propertyNames)
	{
		List<String> parentTypes = type.getParentTypes();
		assertEquals(1, parentTypes.size());
		assertEquals(JSTypeConstants.OBJECT_TYPE, parentTypes.get(0));
	
		for (String propertyName : propertyNames)
		{
			PropertyElement property = type.getProperty(propertyName);
			assertNotNull(propertyName + " does not exist", property);
			
			List<String> propertyTypeNames = property.getTypeNames();
			assertNotNull(propertyName + " does not have a type", propertyTypeNames);
			
			assertEquals(1, propertyTypeNames.size());
			assertEquals(JSTypeConstants.BOOLEAN_TYPE, propertyTypeNames.get(0));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.aptana.editor.js.inferencing.InferencingTestsBase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		this._reader = null;

		super.tearDown();
	}
	
	/**
	 * testObject
	 */
	public void testObject()
	{
		String source = "var x = {}; x;";
		
		this.lastStatementTypeTests(source, "Object");
	}

	/**
	 * testObjectWithAddedProperties
	 */
	public void testObjectWithAddedProperties()
	{
		String source = "var x = {}; x.a = true; x;";
		List<String> types = this.getLastStatementTypes(source);

		assertEquals(1, types.size());
		String typeName = types.get(0);

		TypeElement type = this._reader.getType(this.getIndex(), typeName, PARENT_TYPES_AND_PROPERTIES);
		assertNotNull(type);

		structureTests(type, "a");
	}
	
	/**
	 * testObjectWithProperties
	 */
	public void testObjectWithProperties()
	{
		String source = "var x = { a: true }; x;";
		List<String> types = this.getLastStatementTypes(source);

		assertEquals(1, types.size());
		String typeName = types.get(0);
		
		TypeElement type = this._reader.getType(this.getIndex(), typeName, PARENT_TYPES_AND_PROPERTIES);
		assertNotNull(type);

		structureTests(type, "a");
	}
	
	/**
	 * testObjectWithPropertiesAndAddedProperties
	 */
	public void testObjectWithPropertiesAndAddedProperties()
	{
		String source = "var x = { a: true }; x.b = true; x;";
		List<String> types = this.getLastStatementTypes(source);
		
		assertEquals(1, types.size());
		String typeName = types.get(0);
		
		TypeElement type = this._reader.getType(this.getIndex(), typeName, PARENT_TYPES_AND_PROPERTIES);
		assertNotNull(type);

		structureTests(type, "a", "b");
	}
	
	/**
	 * testNestedObjects
	 */
	public void testNestedObjects()
	{
		String source = this.getContent("inferencing/nested-objects.js");
		List<String> types = this.getLastStatementTypes(source);
		
		assertEquals(1, types.size());
		String typeName = types.get(0);
		
		TypeElement type = this._reader.getType(this.getIndex(), typeName, PARENT_TYPES_AND_PROPERTIES);
		assertNotNull(type);
		
		structureTests(type, "a");
		
		PropertyElement property = type.getProperty("b");
		assertNotNull(property);
		List<String> propertyTypeNames = property.getTypeNames();
		assertEquals(1, propertyTypeNames.size());
		
		TypeElement propertyType = this._reader.getType(this.getIndex(), propertyTypeNames.get(0), PARENT_TYPES_AND_PROPERTIES);
		assertNotNull(propertyType);
		
		structureTests(propertyType, "c");
	}
}
