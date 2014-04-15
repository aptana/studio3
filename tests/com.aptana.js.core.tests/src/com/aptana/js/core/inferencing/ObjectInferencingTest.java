/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.core.inferencing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.aptana.js.core.model.PropertyElement;
import com.aptana.js.core.model.TypeElement;

public class ObjectInferencingTest extends InferencingTestsBase
{
	/**
	 * testObject
	 */
	@Test
	public void testObject()
	{
		String source = "var x = {}; x;";

		lastStatementTypeTests(source, "Object");
	}

	/**
	 * testObjectWithAddedProperties
	 */
	@Test
	public void testObjectWithAddedProperties()
	{
		String source = "var x = {}; x.a = true; x;";
		List<String> types = getLastStatementTypes(source);

		assertEquals(1, types.size());
		String typeName = types.get(0);

		structureTests(typeName, "a");
	}

	/**
	 * testObjectWithProperties
	 */
	@Test
	public void testObjectWithProperties()
	{
		String source = "var x = { a: true }; x;";
		List<String> types = getLastStatementTypes(source);

		assertEquals(1, types.size());
		String typeName = types.get(0);

		structureTests(typeName, "a");
	}

	/**
	 * testObjectWithPropertiesAndAddedProperties
	 */
	@Test
	public void testObjectWithPropertiesAndAddedProperties()
	{
		String source = "var x = { a: true }; x.b = true; x;";
		List<String> types = getLastStatementTypes(source);

		assertEquals(1, types.size());
		String typeName = types.get(0);

		structureTests(typeName, "a", "b");
	}

	/**
	 * testNestedObjects
	 */
	@Test
	public void testNestedObjects()
	{
		List<String> types = getLastStatementTypes(Path.fromPortableString("inferencing/nested-objects.js"));

		assertEquals(1, types.size());
		String typeName = types.get(0);

		List<TypeElement> typeElements = getType(typeName);
		assertNotNull(typeElements);
		structureTests(typeElements, "a");

		PropertyElement property = typeElements.get(0).getProperty("b");
		assertNotNull(property);
		List<String> propertyTypeNames = property.getTypeNames();
		assertEquals(1, propertyTypeNames.size());

		String propertyTypeName = propertyTypeNames.get(0);
		List<TypeElement> propertyTypes = getType(propertyTypeName);
		assertNotNull(propertyTypes);

		structureTests(propertyTypes, "c");
	}
}
