package com.aptana.editor.js.inferencing;

import java.util.List;

import com.aptana.editor.js.contentassist.model.PropertyElement;
import com.aptana.editor.js.contentassist.model.TypeElement;

public class ObjectInferencingTests extends InferencingTestsBase
{
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

		this.structureTests(typeName, "a");
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

		this.structureTests(typeName, "a");
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

		this.structureTests(typeName, "a", "b");
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

		TypeElement type = this.getType(typeName);
		assertNotNull(type);
		this.structureTests(type, "a");

		PropertyElement property = type.getProperty("b");
		assertNotNull(property);
		List<String> propertyTypeNames = property.getTypeNames();
		assertEquals(1, propertyTypeNames.size());

		String propertyTypeName = propertyTypeNames.get(0);
		TypeElement propertyType = this.getType(propertyTypeName);
		assertNotNull(propertyType);

		this.structureTests(propertyType, "c");
	}
}
