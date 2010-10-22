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
package com.aptana.editor.js.inferencing;

import java.util.List;

import org.eclipse.core.runtime.Path;

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
		List<String> types = this.getLastStatementTypes(Path.fromPortableString("inferencing/nested-objects.js"));

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
